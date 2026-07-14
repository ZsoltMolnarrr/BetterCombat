package net.bettercombat.fabric.attachment;

import net.bettercombat.logic.PlayerAttachments;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.player.Player;

@SuppressWarnings("UnstableApiUsage")
public class FabricPlayerAttachments {
    private static AttachmentType<String> MAIN_HAND_IDLE_ANIMATION_TYPE;
    private static AttachmentType<String> OFF_HAND_IDLE_ANIMATION_TYPE;
    private static AttachmentType<Byte> COMBAT_FLAGS_TYPE;

    public static void init() {
        // Register attachment types with default empty string values and syncing support
        // Sync to all clients that can see the player (allButTarget syncs to other players, targetOnly syncs to the player themselves)
        // We use all() to sync to everyone including the player themselves
        MAIN_HAND_IDLE_ANIMATION_TYPE = AttachmentRegistry.create(
                PlayerAttachments.MAIN_HAND_IDLE_ANIMATION,
                builder -> builder
                        .initializer(() -> "")
                        .syncWith(ByteBufCodecs.STRING_UTF8, AttachmentSyncPredicate.all())
        );
        OFF_HAND_IDLE_ANIMATION_TYPE = AttachmentRegistry.create(
                PlayerAttachments.OFF_HAND_IDLE_ANIMATION,
                builder -> builder
                        .initializer(() -> "")
                        .syncWith(ByteBufCodecs.STRING_UTF8, AttachmentSyncPredicate.all())
        );
        COMBAT_FLAGS_TYPE = AttachmentRegistry.create(
                PlayerAttachments.COMBAT_FLAGS,
                builder -> builder
                        .initializer(() -> (byte) 0)
                        .syncWith(ByteBufCodecs.BYTE, AttachmentSyncPredicate.all())
        );
    }

    public static String getMainHandIdleAnimation(Player player) {
        return player.getAttachedOrCreate(MAIN_HAND_IDLE_ANIMATION_TYPE);
    }

    public static String getOffHandIdleAnimation(Player player) {
        return player.getAttachedOrCreate(OFF_HAND_IDLE_ANIMATION_TYPE);
    }

    public static void setMainHandIdleAnimation(Player player, String animation) {
        player.setAttached(MAIN_HAND_IDLE_ANIMATION_TYPE, animation);
    }

    public static void setOffHandIdleAnimation(Player player, String animation) {
        player.setAttached(OFF_HAND_IDLE_ANIMATION_TYPE, animation);
    }

    public static byte getCombatFlags(Player player) {
        return player.getAttachedOrCreate(COMBAT_FLAGS_TYPE);
    }

    public static void setCombatFlags(Player player, byte flags) {
        player.setAttached(COMBAT_FLAGS_TYPE, flags);
    }
}

