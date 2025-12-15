package net.bettercombat;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Platform-agnostic attachment helper for player idle animations.
 * Defers to Fabric Data Attachments API or NeoForge Data Attachments
 * Values are automatically synced to all clients.
 * Values are not serialized/saved to disk
 */
public class PlayerAttachments {
    public static final Identifier MAIN_HAND_IDLE_ANIMATION = Identifier.of("bettercombat", "main_hand_idle_animation");
    public static final Identifier OFF_HAND_IDLE_ANIMATION = Identifier.of("bettercombat", "off_hand_idle_animation");

    /**
     * Gets the main hand idle animation string from the player's attachment.
     * Returns empty string if not set.
     */
    @ExpectPlatform
    public static String getMainHandIdleAnimation(PlayerEntity player) {
        throw new AssertionError();
    }

    /**
     * Gets the off hand idle animation string from the player's attachment.
     * Returns empty string if not set.
     */
    @ExpectPlatform
    public static String getOffHandIdleAnimation(PlayerEntity player) {
        throw new AssertionError();
    }

    /**
     * Sets the main hand idle animation string on the player's attachment.
     */
    @ExpectPlatform
    public static void setMainHandIdleAnimation(PlayerEntity player, String animation) {
        throw new AssertionError();
    }

    /**
     * Sets the off hand idle animation string on the player's attachment.
     */
    @ExpectPlatform
    public static void setOffHandIdleAnimation(PlayerEntity player, String animation) {
        throw new AssertionError();
    }
}

