package net.bettercombat.network;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import net.bettercombat.BetterCombat;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.bettercombat.logic.TargetHelper;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.logic.knockback.ConfigurableKnockback;
import net.bettercombat.mixin.LivingEntityAccessor;
import net.bettercombat.utils.MathHelper;
import net.bettercombat.utils.SoundHelper;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.slf4j.Logger;

import java.util.UUID;

public class ServerNetwork {
    static final Logger LOGGER = LogUtils.getLogger();

    private static PacketByteBuf configSerialized = PacketByteBufs.create();

    private static final UUID COMBO_DAMAGE_MODIFIER_ID = UUID.randomUUID();
    private static final UUID DUAL_WIELDING_MODIFIER_ID = UUID.randomUUID();
    private static final UUID SWEEPING_MODIFIER_ID = UUID.randomUUID();

    public static void initializeHandlers() {
        configSerialized = Packets.ConfigSync.write(BetterCombat.config);
        ServerPlayConnectionEvents.JOIN.register( (handler, sender, server) -> {
            sender.sendPacket(Packets.WeaponRegistrySync.ID, WeaponRegistry.getEncodedRegistry());
            sender.sendPacket(Packets.ConfigSync.ID, configSerialized);
        });

        ServerPlayNetworking.registerGlobalReceiver(Packets.AttackAnimation.ID, (server, player, handler, buf, responseSender) -> {
            ServerWorld world = Iterables.tryFind(server.getWorlds(), (element) -> element == player.world)
                    .orNull();
            if (world == null || world.isClient) {
                return;
            }
            final var packet = Packets.AttackAnimation.read(buf);
            final var forwardBuffer = new Packets.AttackAnimation(player.getId(), packet.animatedHand(), packet.animationName(), packet.length(), packet.upswing()).write();
            PlayerLookup.tracking(player).forEach(serverPlayer -> {
                try {
                    if (serverPlayer.getId() != player.getId() && ServerPlayNetworking.canSend(serverPlayer, Packets.AttackAnimation.ID)) {
                        ServerPlayNetworking.send(serverPlayer, Packets.AttackAnimation.ID, forwardBuffer);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            });//the provided entity is a player, it is not guaranteed by the contract that said player is included in the resulting stream.
            Entity initiator = world.getEntityById(player.getId());
            if (initiator instanceof ServerPlayerEntity){
                if (ServerPlayNetworking.canSend((ServerPlayerEntity)initiator, Packets.AttackAnimation.ID)) {
                    ServerPlayNetworking.send((ServerPlayerEntity)initiator, Packets.AttackAnimation.ID, forwardBuffer);
                }
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(Packets.C2S_AttackRequest.ID, (server, player, handler, buf, responseSender) -> {
            ServerWorld world = Iterables.tryFind(server.getWorlds(), (element) -> element == player.world)
                    .orNull();
            if (world == null || world.isClient) {
                return;
            }
            final var request = Packets.C2S_AttackRequest.read(buf);
            final var hand = PlayerAttackHelper.getCurrentAttack(player, request.comboCount());
            if (hand == null) {
                LOGGER.error("Server handling Packets.C2S_AttackRequest - No current attack hand!");
                LOGGER.error("Combo count: " + request.comboCount() + " is dual wielding: " + PlayerAttackHelper.isDualWielding(player));
                LOGGER.error("Main-hand stack: " + player.getMainHandStack());
                LOGGER.error("Off-hand stack: " + player.getOffHandStack());
                LOGGER.error("Selected slot server: " + player.getInventory().selectedSlot + " | client: " + request.selectedSlot());
                return;
            }
            final var attack = hand.attack();
            final var attributes = hand.attributes();
            final boolean useVanillaPacket = Packets.C2S_AttackRequest.UseVanillaPacket;
            world.getServer().executeSync(() -> {
                ((PlayerAttackProperties)player).setComboCount(request.comboCount());
                Multimap<EntityAttribute, EntityAttributeModifier> comboAttributes = null;
                Multimap<EntityAttribute, EntityAttributeModifier> dualWieldingAttributes = null;
                Multimap<EntityAttribute, EntityAttributeModifier> sweepingModifiers = HashMultimap.create();
                double range = 18.0;
                if (attributes != null && attack != null) {
                    range = attributes.attackRange();

                    comboAttributes = HashMultimap.create();
                    double comboMultiplier = attack.damageMultiplier() - 1;
                    comboAttributes.put(
                            EntityAttributes.GENERIC_ATTACK_DAMAGE,
                            new EntityAttributeModifier(COMBO_DAMAGE_MODIFIER_ID, "COMBO_DAMAGE_MULTIPLIER", comboMultiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE));
                    player.getAttributes().addTemporaryModifiers(comboAttributes);

                    var dualWieldingMultiplier = PlayerAttackHelper.getDualWieldingAttackDamageMultiplier(player, hand) - 1;
                    if (dualWieldingMultiplier != 0) {
                        dualWieldingAttributes = HashMultimap.create();
                        dualWieldingAttributes.put(
                                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                                new EntityAttributeModifier(DUAL_WIELDING_MODIFIER_ID, "DUAL_WIELDING_DAMAGE_MULTIPLIER", dualWieldingMultiplier, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
                        player.getAttributes().addTemporaryModifiers(dualWieldingAttributes);
                    }

                    if (hand.isOffHand()) {
                        PlayerAttackHelper.setAttributesForOffHandAttack(player, true);
                    }

                    SoundHelper.playSound(world, player, attack.swingSound());

                    if (BetterCombat.config.allow_reworked_sweeping && request.entityIds().length > 1) {
                        double multiplier = 1.0
                                - (BetterCombat.config.reworked_sweeping_maximum_damage_penalty / BetterCombat.config.reworked_sweeping_extra_target_count)
                                * Math.min(BetterCombat.config.reworked_sweeping_extra_target_count, request.entityIds().length - 1);
                        int sweepingLevel = EnchantmentHelper.getLevel(Enchantments.SWEEPING, hand.itemStack());
                        double sweepingSteps = BetterCombat.config.reworked_sweeping_enchant_restores / ((double)Enchantments.SWEEPING.getMaxLevel());
                        multiplier += sweepingLevel * sweepingSteps;
                        multiplier = Math.min(multiplier, 1);
                        sweepingModifiers.put(
                                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                                new EntityAttributeModifier(SWEEPING_MODIFIER_ID, "SWEEPING_DAMAGE_MODIFIER", multiplier - 1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
                        // System.out.println("Applied sweeping multiplier " + multiplier + " , sweepingSteps " + sweepingSteps + " , enchant bonus: " + (sweepingLevel * sweepingSteps));
                        player.getAttributes().addTemporaryModifiers(sweepingModifiers);

                        boolean playEffects = !BetterCombat.config.reworked_sweeping_sound_and_particles_only_for_swords
                                || (hand.itemStack().getItem() instanceof SwordItem);
                        if (BetterCombat.config.reworked_sweeping_plays_sound && playEffects) {
                            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0f, 1.0f);
                        }
                        if (BetterCombat.config.reworked_sweeping_emits_particles && playEffects) {
                            player.spawnSweepAttackParticles();
                        }
                    }
                }

                var attackCooldown = PlayerAttackHelper.getAttackCooldownTicksCapped(player);
                var knockbackMultiplier = BetterCombat.config.knockback_reduced_for_fast_attacks
                        ? MathHelper.clamp(attackCooldown / 12.5F, 0.1F, 1F)
                        : 1F;
                var lastAttackedTicks = ((LivingEntityAccessor)player).getLastAttackedTicks();
                if (!useVanillaPacket) {
                    player.setSneaking(request.isSneaking());
                }


                for (int entityId: request.entityIds()) {
                    // getEntityById(entityId);
                    boolean isBossPart = false;
                    Entity entity = world.getEntityById(entityId);
                    if (entity == null) {
                        isBossPart = true;
                        entity = world.getDragonPart(entityId); // Get LivingEntity or DragonPart
                    }

                    if (entity == null
                            || (entity.equals(player.getVehicle()) && !TargetHelper.isAttackableMount(entity))
                            || (entity instanceof ArmorStandEntity && ((ArmorStandEntity)entity).isMarker())) {
                        continue;
                    }
                    if (entity instanceof LivingEntity livingEntity) {
                        if (BetterCombat.config.allow_fast_attacks) {
                            livingEntity.timeUntilRegen = 0;
                        }
                        if (knockbackMultiplier != 1F) {
                            ((ConfigurableKnockback)livingEntity).setKnockbackMultiplier_BetterCombat(knockbackMultiplier);
                        }
                    }
                    ((LivingEntityAccessor) player).setLastAttackedTicks(lastAttackedTicks);
                    // System.out.println("Server - Attacking hand: " + (hand.isOffHand() ? "offhand" : "mainhand") + " CD: " + player.getAttackCooldownProgress(0));
                    if (!isBossPart && useVanillaPacket) {
                        // System.out.println("HIT - A entity: " + entity.getEntityName() + " id: " + entity.getId() + " class: " + entity.getClass());
                        PlayerInteractEntityC2SPacket vanillaAttackPacket = PlayerInteractEntityC2SPacket.attack(entity, request.isSneaking());
                        handler.onPlayerInteractEntity(vanillaAttackPacket);
                    } else {
                        // System.out.println("HIT - B entity: " + entity.getEntityName() + " id: " + entity.getId() + " class: " + entity.getClass());
                        if (true || player.squaredDistanceTo(entity) < range * BetterCombat.config.target_search_range_multiplier) {
                            if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof PersistentProjectileEntity || entity == player) {
                                handler.disconnect(Text.translatable("multiplayer.disconnect.invalid_entity_attacked"));
                                LOGGER.warn("Player {} tried to attack an invalid entity", (Object)player.getName().getString());
                                return;
                            }
                            player.attack(entity);
                        }
                    }
                    if (entity instanceof LivingEntity livingEntity) {
                        if (knockbackMultiplier != 1F) {
                            ((ConfigurableKnockback)livingEntity).setKnockbackMultiplier_BetterCombat(1F);
                        }
                    }
                }

                if (!useVanillaPacket) {
                    player.updateLastActionTime();
                }

                if (comboAttributes != null) {
                    player.getAttributes().removeModifiers(comboAttributes);
                    if (hand.isOffHand()) {
                        PlayerAttackHelper.setAttributesForOffHandAttack(player, false);
                    }
                }
                if (dualWieldingAttributes != null) {
                    player.getAttributes().removeModifiers(dualWieldingAttributes);
                }
                if (!sweepingModifiers.isEmpty()) {
                    player.getAttributes().removeModifiers(sweepingModifiers);
                }
                ((PlayerAttackProperties)player).setComboCount(-1);
            });
        });
    }
}
