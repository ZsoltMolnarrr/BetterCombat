package net.bettercombat.fabric.attachment;

import net.bettercombat.logic.PlayerAttachments;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.codec.PacketCodecs;

@SuppressWarnings("UnstableApiUsage")
public class FabricPlayerAttachments {
    private static AttachmentType<String> MAIN_HAND_IDLE_ANIMATION_TYPE;
    private static AttachmentType<String> OFF_HAND_IDLE_ANIMATION_TYPE;

    public static void init() {
        // Register attachment types with default empty string values and syncing support
        // Sync to all clients that can see the player (allButTarget syncs to other players, targetOnly syncs to the player themselves)
        // We use all() to sync to everyone including the player themselves
        MAIN_HAND_IDLE_ANIMATION_TYPE = AttachmentRegistry.create(
                PlayerAttachments.MAIN_HAND_IDLE_ANIMATION,
                builder -> builder
                        .initializer(() -> "")
                        .syncWith(PacketCodecs.STRING, AttachmentSyncPredicate.all())
        );
        OFF_HAND_IDLE_ANIMATION_TYPE = AttachmentRegistry.create(
                PlayerAttachments.OFF_HAND_IDLE_ANIMATION,
                builder -> builder
                        .initializer(() -> "")
                        .syncWith(PacketCodecs.STRING, AttachmentSyncPredicate.all())
        );
    }

    public static String getMainHandIdleAnimation(PlayerEntity player) {
        return player.getAttachedOrCreate(MAIN_HAND_IDLE_ANIMATION_TYPE);
    }

    public static String getOffHandIdleAnimation(PlayerEntity player) {
        return player.getAttachedOrCreate(OFF_HAND_IDLE_ANIMATION_TYPE);
    }

    public static void setMainHandIdleAnimation(PlayerEntity player, String animation) {
        player.setAttached(MAIN_HAND_IDLE_ANIMATION_TYPE, animation);
    }

    public static void setOffHandIdleAnimation(PlayerEntity player, String animation) {
        player.setAttached(OFF_HAND_IDLE_ANIMATION_TYPE, animation);
    }
}

