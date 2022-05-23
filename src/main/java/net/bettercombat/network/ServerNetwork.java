package net.bettercombat.network;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import net.bettercombat.SoundHelper;
import net.bettercombat.WeaponRegistry;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.mixin.LivingEntityAccessor;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import org.slf4j.Logger;

import java.util.UUID;

public class ServerNetwork {
    static final Logger LOGGER = LogUtils.getLogger();

    public static void initializeHandlers() {
        ServerPlayConnectionEvents.JOIN.register( (handler, sender, server) -> {
            sender.sendPacket(Packets.WeaponRegistrySync.ID, WeaponRegistry.getEncodedRegistry());
        });

        ServerPlayNetworking.registerGlobalReceiver(Packets.AttackAnimation.ID, (server, player, handler, buf, responseSender) -> {
            ServerWorld world = Iterables.tryFind(server.getWorlds(), (element) -> element == player.world)
                    .orNull();
            if (world == null || world.isClient) {
                return;
            }
            final var packet = Packets.AttackAnimation.read(buf);
            final var forwardBuffer = Packets.AttackAnimation.writePlay(player.getId(), packet.animationName());;
            PlayerLookup.tracking(player).forEach(serverPlayer -> {
                try {
                    if (serverPlayer.getId() != player.getId() && ServerPlayNetworking.canSend(serverPlayer, Packets.AttackAnimation.ID)) {
                        ServerPlayNetworking.send(serverPlayer, Packets.AttackAnimation.ID, forwardBuffer);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(Packets.C2S_AttackRequest.ID, (server, player, handler, buf, responseSender) -> {
            ServerWorld world = Iterables.tryFind(server.getWorlds(), (element) -> element == player.world)
                    .orNull();
            if (world == null || world.isClient) {
                return;
            }
            final var request = Packets.C2S_AttackRequest.read(buf);
            final WeaponAttributes attributes = WeaponRegistry.getAttributes(player.getMainHandStack());
            final boolean useVanillaPacket = Packets.C2S_AttackRequest.UseVanillaPacket;
            world.getServer().executeSync(() -> {
                Multimap<EntityAttribute, EntityAttributeModifier> temporaryAttributes = null;
                double range = 18.0;
                if (attributes != null) {
                    range = attributes.attackRange();
                    WeaponAttributes.Attack attack = attributes.currentAttack(request.comboCount());
                    var multiplier = attack.damageMultiplier();
                    var key = EntityAttributes.GENERIC_ATTACK_DAMAGE;
                    var value = new EntityAttributeModifier(UUID.randomUUID(), "COMBO_DAMAGE_MULTIPLIER", multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE);
                    temporaryAttributes = HashMultimap.create();
                    temporaryAttributes.put(key, value);
                    player.getAttributes().addTemporaryModifiers(temporaryAttributes);

                    SoundHelper.playSounds(world, player, attack.swingSound());
                }

                var lastAttackedTicks = ((LivingEntityAccessor)player).getLastAttackedTicks();
                if (!useVanillaPacket) {
                    player.setSneaking(request.isSneaking());
                }

                for (int entityId: request.entityIds()) {
                    Entity entity = world.getEntityById(entityId);
                    if (entity == null
                            || entity.isTeammate(player)
                            || (entity instanceof ArmorStandEntity && ((ArmorStandEntity)entity).isMarker())) {
                        continue;
                    }
                    ((LivingEntityAccessor) player).setLastAttackedTicks(lastAttackedTicks);
                    if (useVanillaPacket) {
                        PlayerInteractEntityC2SPacket vanillaAttackPacket = PlayerInteractEntityC2SPacket.attack(entity, request.isSneaking());
                        handler.onPlayerInteractEntity(vanillaAttackPacket);
                    } else {
                        if (player.squaredDistanceTo(entity) < range * Packets.C2S_AttackRequest.RangeTolerance) {
                            if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof PersistentProjectileEntity || entity == player) {
                                handler.disconnect(new TranslatableText("multiplayer.disconnect.invalid_entity_attacked"));
                                LOGGER.warn("Player {} tried to attack an invalid entity", (Object)player.getName().getString());
                                return;
                            }
                            player.attack(entity);
                        }
                    }
                }

                if (!useVanillaPacket) {
                    player.updateLastActionTime();
                }

                if (temporaryAttributes != null) {
                    player.getAttributes().removeModifiers(temporaryAttributes);
                }
            });
        });
    }
}
