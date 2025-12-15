package net.bettercombat.neoforge.attachment;

import net.bettercombat.logic.PlayerAttachments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.codec.PacketCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NeoForgePlayerAttachments {
    private static AttachmentType<String> MAIN_HAND_IDLE_ANIMATION_TYPE;
    private static AttachmentType<String> OFF_HAND_IDLE_ANIMATION_TYPE;

    public static void init(DeferredRegister<AttachmentType<?>> attachmentTypes) {
        // Register attachment types with serialization and syncing support
        // Sync to all clients that can see the player
        MAIN_HAND_IDLE_ANIMATION_TYPE = AttachmentType.builder(() -> "")
                .sync(PacketCodecs.STRING)
                .build();
        OFF_HAND_IDLE_ANIMATION_TYPE = AttachmentType.builder(() -> "")
                .sync(PacketCodecs.STRING)
                .build();

        attachmentTypes.register(PlayerAttachments.MAIN_HAND_IDLE_ANIMATION.getPath(), () -> MAIN_HAND_IDLE_ANIMATION_TYPE);
        attachmentTypes.register(PlayerAttachments.OFF_HAND_IDLE_ANIMATION.getPath(), () -> OFF_HAND_IDLE_ANIMATION_TYPE);
    }

    public static String getMainHandIdleAnimation(PlayerEntity player) {
        return player.getData(MAIN_HAND_IDLE_ANIMATION_TYPE);
    }

    public static String getOffHandIdleAnimation(PlayerEntity player) {
        return player.getData(OFF_HAND_IDLE_ANIMATION_TYPE);
    }

    public static void setMainHandIdleAnimation(PlayerEntity player, String animation) {
        player.setData(MAIN_HAND_IDLE_ANIMATION_TYPE, animation);
    }

    public static void setOffHandIdleAnimation(PlayerEntity player, String animation) {
        player.setData(OFF_HAND_IDLE_ANIMATION_TYPE, animation);
    }
}

