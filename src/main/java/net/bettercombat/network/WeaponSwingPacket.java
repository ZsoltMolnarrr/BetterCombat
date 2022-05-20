package net.bettercombat.network;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import net.bettercombat.BetterCombat;
import net.bettercombat.WeaponRegistry;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.mixin.LivingEntityAccessor;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.List;
import java.util.UUID;

public class WeaponSwingPacket {

    static final Logger LOGGER = LogUtils.getLogger();

    public record C2S_AttackRequest(int comboCount, int stack, boolean isSneaking, int[] entityIds) {
        public static Identifier ID = new Identifier(BetterCombat.MODID, "c2s_request_attack");
        public static double RangeTolerance = 2.0;
        public static boolean UseVanillaPacket = true;
        public static PacketByteBuf write(PacketByteBuf buffer, int comboCount, boolean useMainHand, boolean isSneaking, List<Entity> entities) {
            int[] ids = new int[entities.size()];
            for(int i = 0; i < entities.size(); i++) {
                ids[i] = entities.get(i).getId();
            }
            buffer.writeInt(comboCount);
            buffer.writeInt(useMainHand ? 0 : 1);
            buffer.writeBoolean(isSneaking);
            buffer.writeIntArray(ids);
            return buffer;
        }

        public static C2S_AttackRequest read(PacketByteBuf buffer) {
            int comboCount = buffer.readInt();
            int stack = buffer.readInt();
            boolean isSneaking = buffer.readBoolean();
            int[] ids = buffer.readIntArray();
            return new C2S_AttackRequest(comboCount, stack, isSneaking, ids);
        }
    }

//    public static Identifier C2S_REQUEST_SWING = new Identifier("C2S_REQUEST_SWING");
//    public static Identifier S2C_PERFORM_SWING = new Identifier("C2S_REQUEST_SWING");

    public static void initializeHandlers() {
//        ServerPlayNetworking.registerGlobalReceiver(WeaponSwingPacket.C2S_REQUEST_SWING, (server, player, handler, buf, responseSender) -> {
//            ServerWorld world = Iterables.tryFind(server.getWorlds(), (element) -> element == player.world)
//                    .orNull();
//            if (world == null) {
//                return;
//            }
//
//            // world.isPlayerInRange()
//            // ServerPlayNetworking.send((ServerPlayerEntity) user, TutorialNetworkingConstants.HIGHLIGHT_PACKET_ID, PacketByteBufs.empty());
//            // world.getChunkManager().sendToOtherNearbyPlayers(player, asd);
//            // ServerChunkManager serverChunkManager = server.chunk
//        });

        ServerPlayNetworking.registerGlobalReceiver(C2S_AttackRequest.ID, (server, player, handler, buf, responseSender) -> {
            ServerWorld world = Iterables.tryFind(server.getWorlds(), (element) -> element == player.world)
                    .orNull();
            if (world == null || world.isClient) {
                return;
            }

            final C2S_AttackRequest request = C2S_AttackRequest.read(buf);
            final WeaponAttributes attributes = WeaponRegistry.getAttributes(player.getMainHandStack());
            final boolean useVanillaPacket = C2S_AttackRequest.UseVanillaPacket;

            world.getServer().executeSync(() -> {
                Multimap<EntityAttribute, EntityAttributeModifier> temporaryAttributes = null;
                double range = 18.0;
                if (attributes != null) {
                    range = attributes.attackRange();
                    WeaponAttributes.Attack attack = attributes.currentAttack(request.comboCount);
                    var multiplier = attack.damageMultiplier();
                    var key = EntityAttributes.GENERIC_ATTACK_DAMAGE;
                    var value = new EntityAttributeModifier(UUID.randomUUID(), "COMBO_DAMAGE_MULTIPLIER", multiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE);
                    temporaryAttributes = HashMultimap.create();
                    temporaryAttributes.put(key, value);
                    player.getAttributes().addTemporaryModifiers(temporaryAttributes);
                }

                var lastAttackedTicks = ((LivingEntityAccessor)player).getLastAttackedTicks();
                if (!useVanillaPacket) {
                    player.setSneaking(request.isSneaking);
                }

                for (int entityId: request.entityIds) {
                    Entity entity = world.getEntityById(entityId);
                    if (entity == null
                            || entity.isTeammate(player)
                            || (entity instanceof ArmorStandEntity && ((ArmorStandEntity)entity).isMarker())) {
                        continue;
                    }
                    ((LivingEntityAccessor) player).setLastAttackedTicks(lastAttackedTicks);
                    if (useVanillaPacket) {
                        PlayerInteractEntityC2SPacket vanillaAttackPacket = PlayerInteractEntityC2SPacket.attack(entity, request.isSneaking);
                        handler.onPlayerInteractEntity(vanillaAttackPacket);
                    } else {
                        if (player.squaredDistanceTo(entity) < range * C2S_AttackRequest.RangeTolerance) {
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
