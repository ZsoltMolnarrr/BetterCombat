package net.bettercombat.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Per-player runtime flags controlling Better Combat behavior.
 * Synced to all tracking clients automatically.
 *
 * Admins (datapacks, command blocks) can disable Better Combat attacks for a player
 * via the vanilla command tag {@link #DISABLED_TAG}, for example:
 * {@code /tag <player> add bettercombat_disabled}
 * The tag is persisted in player NBT by vanilla (surviving death and relog), and is
 * mirrored into {@link #TAG_DISABLED} by the server. Mods disable attacks via
 * {@link #setAttacksDisabled}, which flips {@link #API_DISABLED} independently of the tag.
 * {@link #API_DISABLED} is not persisted, mods need to re-apply it after death or relog.
 */
public final class CombatFlags {
    private CombatFlags() { }

    /**
     * Vanilla command tag. While present on a player, Better Combat attack handling
     * is disabled for them. Do not confuse with {@link #TAG_DISABLED}.
     */
    public static final String DISABLED_TAG = "bettercombat_disabled";

    /**
     * Bit 0: Mirror of the {@link #DISABLED_TAG} command tag, written only by the server.
     */
    public static final int TAG_DISABLED = 0b1;

    /**
     * Bit 1: Better Combat attack handling disabled by a mod, written only via
     * {@link #setAttacksDisabled}. Bits 2-7 are reserved for future flags.
     */
    public static final int API_DISABLED = 0b10;

    public static byte get(Player player) {
        return ((EntityPlayer_BetterCombat) player).getCombatFlags();
    }

    public static void set(ServerPlayer player, byte flags) {
        ((EntityPlayer_BetterCombat) player).setCombatFlags(flags);
    }

    /**
     * The player uses fully vanilla combat while disabled by the tag, a mod, or both.
     */
    public static boolean isAttackDisabled(Player player) {
        return (get(player) & (TAG_DISABLED | API_DISABLED)) != 0;
    }

    public static void setAttacksDisabled(ServerPlayer player, boolean disabled) {
        var flags = get(player);
        set(player, (byte) (disabled ? (flags | API_DISABLED) : (flags & ~API_DISABLED)));
    }
}
