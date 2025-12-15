package net.bettercombat;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;


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
    public static boolean isCastingSpell(PlayerEntity player) { throw new AssertionError(); }

    // MARK: Network hooks

    @ExpectPlatform
    public static PacketByteBuf createByteBuffer() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Collection<ServerPlayerEntity> tracking(ServerPlayerEntity player) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Collection<ServerPlayerEntity> around(ServerWorld world, Vec3d origin, double distance) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean networkS2C_CanSend(ServerPlayerEntity player, Identifier packetId) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void networkS2C_Send(ServerPlayerEntity player, CustomPayload payload) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void networkC2S_Send(CustomPayload payload) {
        throw new AssertionError();
    }

    public interface PlayerAttachments {
        public static final Identifier MAIN_HAND_IDLE_ANIMATION = Identifier.of("bettercombat", "main_hand_idle_animation");
        public static final Identifier OFF_HAND_IDLE_ANIMATION = Identifier.of("bettercombat", "off_hand_idle_animation");

        /**
         * Gets the main hand idle animation string from the player's attachment.
         * Returns empty string if not set.
         */
        String getMainHandIdleAnimation(PlayerEntity player);

        /**
         * Gets the off hand idle animation string from the player's attachment.
         * Returns empty string if not set.
         */
        String getOffHandIdleAnimation(PlayerEntity player);

        /**
         * Sets the main hand idle animation string on the player's attachment.
         */
        void setMainHandIdleAnimation(PlayerEntity player, String animation);

        /**
         * Sets the off hand idle animation string on the player's attachment.
         */
        void setOffHandIdleAnimation(PlayerEntity player, String animation);
    }
    @ExpectPlatform public static PlayerAttachments playerAttachments() { throw new AssertionError(); }
}
