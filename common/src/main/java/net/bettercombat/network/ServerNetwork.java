package net.bettercombat.network;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.Platform;
import net.bettercombat.api.CombatFlags;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.bettercombat.logic.TargetHelper;
import net.bettercombat.logic.knockback.ConfigurableKnockback;
import net.bettercombat.mixin.player.LivingEntityAccessor;
import net.bettercombat.utils.AttributeModifierHelper;
import net.bettercombat.utils.MathHelper;
import net.bettercombat.utils.SoundHelper;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import org.slf4j.Logger;

public class ServerNetwork {
    static final Logger LOGGER = LogUtils.getLogger();

    public static void handleAttackAnimation(Packets.AttackAnimation packet, MinecraftServer server, ServerPlayer player) {
        ServerLevel world = Iterables.tryFind(server.getAllLevels(), (element) -> element == player.level())
                .orNull();
        if (world == null || world.isClientSide()) {
            return;
        }
        final var forwardPacket = new Packets.AttackAnimation(
                player.getId(), packet.animatedHand(), packet.animationName(),
                packet.length(), packet.upswing(),
                packet.weaponRange(), packet.upswingTicks(), packet.particles() // Fixme: particles.appearance should be resolved server-side
        );
        try {
            //send info back for Replaymod Compat
            if (Platform.networkS2C_CanSend(player, Packets.AttackAnimation.ID)) {
                Platform.networkS2C_Send(player, forwardPacket);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        Platform.tracking(player).forEach(serverPlayer -> {
            try {
                if (Platform.networkS2C_CanSend(serverPlayer, Packets.AttackAnimation.ID)) {
                    Platform.networkS2C_Send(serverPlayer, forwardPacket);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public static Identifier TEMPORARY_ATTACK = Identifier.fromNamespaceAndPath(BetterCombatMod.ID, "temp_attack");

    public static void handleAttackRequest(Packets.C2S_AttackRequest request, MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler) {
        ServerLevel world = Iterables.tryFind(server.getAllLevels(), (element) -> element == player.level())
                .orNull();
        if (world == null || world.isClientSide()) {
            return;
        }
        if (CombatFlags.isAttackDisabled(player)) {
            return;
        }
        final var hand = PlayerAttackHelper.getCurrentAttack(player, request.comboCount());
        if (hand == null) {
            LOGGER.error("Server handling Packets.C2S_AttackRequest - No current attack hand!");
            LOGGER.error("Combo count: " + request.comboCount() + " is dual wielding: " + PlayerAttackHelper.isDualWielding(player));
            LOGGER.error("Main-hand stack: " + player.getMainHandItem());
            LOGGER.error("Off-hand stack: " + player.getOffhandItem());
            LOGGER.error("Selected slot server: " + player.getInventory().getSelectedSlot() + " | client: " + request.selectedSlot());
            return;
        }
        final var attack = hand.attack();
        final var attributes = hand.attributes();
        final boolean useVanillaPacket = Packets.C2S_AttackRequest.UseVanillaPacket;
        world.getServer().executeIfPossible(() -> {
            ((PlayerAttackProperties)player).setComboCount(request.comboCount());

            PlayerAttackHelper.swapHandAttributes(player, hand.isOffHand(), () -> {

                double damageBaseMultiplier = 0.0;
                double range = 18.0;
                boolean isDualWielding = PlayerAttackHelper.isDualWielding(player);

                if (attributes != null && attack != null) {
                    range = PlayerAttackHelper.getRangeForItem(player, hand.itemStack());

                    double comboMultiplier = attack.damageMultiplier() - 1;
                    damageBaseMultiplier += comboMultiplier;

                    var dualWieldingMultiplier = PlayerAttackHelper.getDualWieldingAttackDamageMultiplier(player, hand) - 1;
                    damageBaseMultiplier += dualWieldingMultiplier;

                    SoundHelper.playSound(world, player, attack.swingSound());

                    if (BetterCombatMod.config.allow_reworked_sweeping && request.entityIds().length > 1) {
                        double multiplier = 0
                                - (BetterCombatMod.config.reworked_sweeping_maximum_damage_penalty / BetterCombatMod.config.reworked_sweeping_extra_target_count)
                                * Math.min(BetterCombatMod.config.reworked_sweeping_extra_target_count, request.entityIds().length - 1);
                        var sweepRatio = player.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO);

                        damageBaseMultiplier += multiplier + (BetterCombatMod.config.reworked_sweeping_maximum_damage_penalty * sweepRatio);

                        boolean playEffects = !BetterCombatMod.config.reworked_sweeping_sound_and_particles_only_for_swords;
                        if (BetterCombatMod.config.reworked_sweeping_plays_sound && playEffects) {
                            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0f, 1.0f);
                        }
//                        if (BetterCombatMod.config.reworked_sweeping_emits_particles && playEffects) {
//                            player.spawnSweepAttackParticles();
//                        }
                    }
                }

                Multimap<Holder<Attribute>, AttributeModifier> damageModifier = null;
                if (damageBaseMultiplier != 0) {
                    AttributeModifierHelper.fromModifier(Attributes.ATTACK_DAMAGE, null);
                    damageModifier = AttributeModifierHelper.fromModifier(
                            Attributes.ATTACK_DAMAGE,
                            new AttributeModifier(TEMPORARY_ATTACK, damageBaseMultiplier, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                    player.getAttributes().addTransientAttributeModifiers(damageModifier);
                }

                var attackCooldown = PlayerAttackHelper.getAttackCooldownTicksCapped(player);
                var knockbackMultiplier = 1F;
                if (BetterCombatMod.config.knockback_reduced_for_fast_attacks)  {
                    knockbackMultiplier = MathHelper.clamp(attackCooldown / BetterCombatMod.config.knockback_reduction_threshold, 0.1F, 1F);
                    switch (BetterCombatMod.config.knockback_reduction_curve) {
                        case SQUARE -> { knockbackMultiplier *= knockbackMultiplier; }
                        case HALF_SQUARE -> { knockbackMultiplier = (knockbackMultiplier * knockbackMultiplier + knockbackMultiplier) * 0.5F; }
                        default -> {}
                    }
                    // System.out.println("Attack cooldown: " + attackCooldown + " Knockback multiplier: " + knockbackMultiplier);
                }

                var lastAttackedTicks = ((LivingEntityAccessor) player).betterCombat_getTicksSinceLastAttack();
                if (!useVanillaPacket) {
                    player.setShiftKeyDown(request.isSneaking());
                }

                var validationRangeSquared = range * range * BetterCombatMod.config.target_search_range_multiplier;
                var attackedAnyEntity = false;
                for (int entityId : request.entityIds()) {
                    // getEntityById(entityId);
                    boolean isBossPart = false;
                    Entity entity = world.getEntity(entityId);
                    if (entity == null) {
                        isBossPart = true;
                        entity = world.getEntityOrPart(entityId); // Get LivingEntity or DragonPart
                    }

                    if (entity == null
                            || (entity.equals(player.getVehicle()) && !TargetHelper.isAttackableMount(entity))
                            || (entity instanceof ArmorStand && ((ArmorStand) entity).isMarker())) {
                        continue;
                    }

                    var isDirectHit = entityId == request.cursorTarget();
                    if (!TargetHelper.isHitAllowed(isDirectHit, TargetHelper.getRelation(player, entity))) {
                        continue;
                    }

                    if (entity instanceof LivingEntity livingEntity) {
                        if (BetterCombatMod.config.allow_fast_attacks) {
                            livingEntity.invulnerableTime = 0;
                        }
                        if (knockbackMultiplier != 1F) {
                            ((ConfigurableKnockback) livingEntity).setKnockbackMultiplier_BetterCombat(knockbackMultiplier);
                        }
                    }
                    ((LivingEntityAccessor) player).betterCombat_setTicksSinceLastAttack(lastAttackedTicks);
                    // System.out.println("Server - Attacking hand: " + (hand.isOffHand() ? "offhand" : "mainhand") + " CD: " + player.getAttackCooldownProgress(0));
                    if (!isBossPart && useVanillaPacket) {
                        // System.out.println("HIT - A entity: " + entity.getEntityName() + " id: " + entity.getId() + " class: " + entity.getClass());
                        // 26.1: attacks were split out of ServerboundInteractPacket into a dedicated packet
                        ServerboundAttackPacket vanillaAttackPacket = new ServerboundAttackPacket(entity.getId());
                        handler.handleAttack(vanillaAttackPacket);
                        attackedAnyEntity = true;
                    } else {
                        // System.out.println("HIT - B entity: " + entity.getEntityName() + " id: " + entity.getId() + " class: " + entity.getClass());
                        if (!BetterCombatMod.config.server_target_range_validation
                                || player.distanceToSqr(entity) <= validationRangeSquared) {
                            if (entity instanceof ItemEntity || entity instanceof ExperienceOrb || entity instanceof AbstractArrow || entity == player) {
                                handler.disconnect(Component.translatable("multiplayer.disconnect.invalid_entity_attacked"));
                                LOGGER.warn("Player {} tried to attack an invalid entity", (Object) player.getName().getString());
                                return;
                            }
                            player.attack(entity);
                            attackedAnyEntity = true;
                        }
                    }
                    if (entity instanceof LivingEntity livingEntity) {
                        if (knockbackMultiplier != 1F) {
                            ((ConfigurableKnockback) livingEntity).setKnockbackMultiplier_BetterCombat(1F);
                        }
                    }
                }

                if (!attackedAnyEntity) {
                    // Vanilla `PiercingWeaponComponent.stab` fires attack enchantment effects (such as Lunge)
                    // even when nothing is hit. Swings with targets get this via `PlayerEntity.attack`.
                    player.postPiercingAttack();
                }

                if (!useVanillaPacket) {
                    player.resetLastActionTime();
                }


                if (damageModifier != null) {
                    player.getAttributes().removeAttributeModifiers(damageModifier);
                }

                ((PlayerAttackProperties) player).setComboCount(-1);
            });
        });
    }

    public static void handleBlockHit(Packets.C2S_BlockHit packet, MinecraftServer server, ServerPlayer player) {
        var world = player.level();
        if (world == null) {
            return;
        }
        var block = world.getBlockState(packet.pos());
        if (block == null || block.isAir()) {
            return;
        }
        var soundGroup = block.getSoundType();
        if (soundGroup != null) {
            world.playSound(null, packet.pos().getX(), packet.pos().getY(), packet.pos().getZ(), soundGroup.getHitSound(), player.getSoundSource(), 1.0F, 1.0F);
        }
    }
}
