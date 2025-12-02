package net.bettercombat.neoforge;

import net.bettercombat.neoforge.attachment.NeoForgePlayerAttachments;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerAttachmentsImpl {
    public static String getMainHandIdleAnimation(PlayerEntity player) {
        return NeoForgePlayerAttachments.getMainHandIdleAnimation(player);
    }

    public static String getOffHandIdleAnimation(PlayerEntity player) {
        return NeoForgePlayerAttachments.getOffHandIdleAnimation(player);
    }

    public static void setMainHandIdleAnimation(PlayerEntity player, String animation) {
        NeoForgePlayerAttachments.setMainHandIdleAnimation(player, animation);
    }

    public static void setOffHandIdleAnimation(PlayerEntity player, String animation) {
        NeoForgePlayerAttachments.setOffHandIdleAnimation(player, animation);
    }
}

