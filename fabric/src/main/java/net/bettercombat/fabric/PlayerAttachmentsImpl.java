package net.bettercombat.fabric;

import net.bettercombat.fabric.attachment.FabricPlayerAttachments;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerAttachmentsImpl {
    public static String getMainHandIdleAnimation(PlayerEntity player) {
        return FabricPlayerAttachments.getMainHandIdleAnimation(player);
    }

    public static String getOffHandIdleAnimation(PlayerEntity player) {
        return FabricPlayerAttachments.getOffHandIdleAnimation(player);
    }

    public static void setMainHandIdleAnimation(PlayerEntity player, String animation) {
        FabricPlayerAttachments.setMainHandIdleAnimation(player, animation);
    }

    public static void setOffHandIdleAnimation(PlayerEntity player, String animation) {
        FabricPlayerAttachments.setOffHandIdleAnimation(player, animation);
    }
}

