package net.bettercombat.neoforge.attachment;

import net.bettercombat.logic.PlayerAttachments;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NeoForgePlayerAttachments {
    private static AttachmentType<String> MAIN_HAND_IDLE_ANIMATION_TYPE;
    private static AttachmentType<String> OFF_HAND_IDLE_ANIMATION_TYPE;
    private static AttachmentType<Byte> COMBAT_FLAGS_TYPE;

    public static void init(DeferredRegister<AttachmentType<?>> attachmentTypes) {
        // Register attachment types with serialization and syncing support
        // Sync to all clients that can see the player
        MAIN_HAND_IDLE_ANIMATION_TYPE = AttachmentType.builder(() -> "")
                .sync(ByteBufCodecs.STRING_UTF8)
                .build();
        OFF_HAND_IDLE_ANIMATION_TYPE = AttachmentType.builder(() -> "")
                .sync(ByteBufCodecs.STRING_UTF8)
                .build();
        COMBAT_FLAGS_TYPE = AttachmentType.builder(() -> (byte) 0)
                .sync(ByteBufCodecs.BYTE)
                .build();

        attachmentTypes.register(PlayerAttachments.MAIN_HAND_IDLE_ANIMATION.getPath(), () -> MAIN_HAND_IDLE_ANIMATION_TYPE);
        attachmentTypes.register(PlayerAttachments.OFF_HAND_IDLE_ANIMATION.getPath(), () -> OFF_HAND_IDLE_ANIMATION_TYPE);
        attachmentTypes.register(PlayerAttachments.COMBAT_FLAGS.getPath(), () -> COMBAT_FLAGS_TYPE);
    }

    public static String getMainHandIdleAnimation(Player player) {
        return player.getData(MAIN_HAND_IDLE_ANIMATION_TYPE);
    }

    public static String getOffHandIdleAnimation(Player player) {
        return player.getData(OFF_HAND_IDLE_ANIMATION_TYPE);
    }

    public static void setMainHandIdleAnimation(Player player, String animation) {
        player.setData(MAIN_HAND_IDLE_ANIMATION_TYPE, animation);
    }

    public static void setOffHandIdleAnimation(Player player, String animation) {
        player.setData(OFF_HAND_IDLE_ANIMATION_TYPE, animation);
    }

    public static byte getCombatFlags(Player player) {
        return player.getData(COMBAT_FLAGS_TYPE);
    }

    public static void setCombatFlags(Player player, byte flags) {
        player.setData(COMBAT_FLAGS_TYPE, flags);
    }
}

