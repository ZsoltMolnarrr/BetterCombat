package net.bettercombat.logic;

import net.minecraft.resources.Identifier;

public class PlayerAttachments {
    public static final Identifier MAIN_HAND_IDLE_ANIMATION = Identifier.fromNamespaceAndPath("bettercombat", "main_hand_idle_animation");
    public static final Identifier OFF_HAND_IDLE_ANIMATION = Identifier.fromNamespaceAndPath("bettercombat", "off_hand_idle_animation");
    /*
     * Per-player combat flags, see `CombatFlags` for the public API.
     *   Bit 0 (0b00000001) TAG_DISABLED - attacks disabled, mirror of the `bettercombat_disabled` command tag, written only by `PlayerEntityMixin.updateCombatFlagsFromCommandTags`
     *   Bit 1 (0b00000010) API_DISABLED - attacks disabled by a mod, written only via `CombatFlags.setAttacksDisabled`
     *   Bit 2 (0b00000100) unused, reserved for future flags (pose suppression, HUD)
     *   Bit 3 (0b00001000) unused
     *   Bit 4 (0b00010000) unused
     *   Bit 5 (0b00100000) unused
     *   Bit 6 (0b01000000) unused
     *   Bit 7 (0b10000000) unused
     */
    public static final Identifier COMBAT_FLAGS = Identifier.fromNamespaceAndPath("bettercombat", "combat_flags");
}

