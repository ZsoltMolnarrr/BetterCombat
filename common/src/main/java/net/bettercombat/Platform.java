package net.bettercombat;

import dev.architectury.injectables.annotations.ExpectPlatform;
import java.util.Collection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;


public class Platform {
    public static final boolean Fabric;
    public static final boolean Forge;
    public static final boolean NeoForge;

    static
    {
        Fabric = getPlatformType() == Type.FABRIC;
        Forge  = getPlatformType() == Type.FORGE;
        NeoForge = getPlatformType() == Type.NEOFORGE;
    }

    public enum Type { FABRIC, FORGE, NEOFORGE }

    @ExpectPlatform
    protected static Type getPlatformType() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isModLoaded(String modid) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isCastingSpell(Player player) { throw new AssertionError(); }

    // MARK: Network hooks

    @ExpectPlatform
    public static FriendlyByteBuf createByteBuffer() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Collection<ServerPlayer> tracking(ServerPlayer player) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Collection<ServerPlayer> around(ServerLevel world, Vec3 origin, double distance) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean networkS2C_CanSend(ServerPlayer player, Identifier packetId) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void networkS2C_Send(ServerPlayer player, CustomPacketPayload payload) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void networkC2S_Send(CustomPacketPayload payload) {
        throw new AssertionError();
    }

    public interface PlayerAttachments {
        public static final Identifier MAIN_HAND_IDLE_ANIMATION = Identifier.fromNamespaceAndPath("bettercombat", "main_hand_idle_animation");
        public static final Identifier OFF_HAND_IDLE_ANIMATION = Identifier.fromNamespaceAndPath("bettercombat", "off_hand_idle_animation");

        /**
         * Gets the main hand idle animation string from the player's attachment.
         * Returns empty string if not set.
         */
        String getMainHandIdleAnimation(Player player);

        /**
         * Gets the off hand idle animation string from the player's attachment.
         * Returns empty string if not set.
         */
        String getOffHandIdleAnimation(Player player);

        /**
         * Sets the main hand idle animation string on the player's attachment.
         */
        void setMainHandIdleAnimation(Player player, String animation);

        /**
         * Sets the off hand idle animation string on the player's attachment.
         */
        void setOffHandIdleAnimation(Player player, String animation);

        /**
         * Gets the combat flags byte from the player's attachment.
         * See `CombatFlags` for flag values and helpers.
         */
        byte getCombatFlags(Player player);

        /**
         * Sets the combat flags byte on the player's attachment.
         * Server-side only, clients receive the value via sync.
         */
        void setCombatFlags(Player player, byte flags);
    }
    @ExpectPlatform public static PlayerAttachments playerAttachments() { throw new AssertionError(); }
}
