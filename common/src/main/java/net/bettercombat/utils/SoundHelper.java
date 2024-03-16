package net.bettercombat.utils;

import net.bettercombat.BetterCombat;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.network.Packets;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Random;

public class SoundHelper {
    private static final Random rng = new Random();

    public static void playSound(PlayerEntity playerEntity, WeaponAttributes.Sound sound) {
        if (sound == null) return;

        try {
            playerEntity.playSound(
                    getSoundEvent(sound),
                    sound.volume(),
                    getSoundPitch(sound));
        } catch (Exception e) {
            System.out.println("Failed to play sound: " + sound.id());
            e.printStackTrace();
        }
    }

    public static void playSound(ServerWorld world, Entity entity, WeaponAttributes.Sound sound) {
        if (sound == null) return;
        
        try {
            var packet = new Packets.AttackSound(
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    sound.id(),
                    sound.volume(),
                    getSoundPitch(sound),
                    rng.nextLong())
                    .write();

            var distance = getSoundEvent(sound).getDistanceToTravel(sound.volume());
            var origin = new Vec3d(entity.getX(), entity.getY(), entity.getZ());
            PlayerLookup.around(world, origin, distance).forEach(serverPlayer -> {
                var channel = Packets.AttackSound.ID;
                try {
                    if (ServerPlayNetworking.canSend(serverPlayer, channel)) {
                        ServerPlayNetworking.send(serverPlayer, channel, packet);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.out.println("Failed to play sound: " + sound.id());
            e.printStackTrace();
        }
    }

    public static List<String> soundKeys = List.of(
            "anchor_slam",
            "axe_slash",
            "claymore_swing",
            "claymore_stab",
            "claymore_slam",
            "dagger_slash",
            "double_axe_swing",
            "fist_punch",
            "glaive_slash_quick",
            "glaive_slash_slow",
            "hammer_slam",
            "katana_slash",
            "mace_slam",
            "mace_slash",
            "pickaxe_swing",
            "rapier_slash",
            "rapier_stab",
            "scythe_slash",
            "spear_stab",
            "staff_slam",
            "staff_slash",
            "staff_spin",
            "staff_stab",
            "sickle_slash",
            "sword_slash",
            "wand_swing"
    );

    public static void registerSounds() {
        for (var soundKey: soundKeys) {
            var soundId = new Identifier(BetterCombat.MODID, soundKey);
            var soundEvent = SoundEvent.of(soundId);
            Registry.register(Registries.SOUND_EVENT, soundId, soundEvent);
        }
    }

    private static float getSoundPitch(WeaponAttributes.Sound sound) {
        return (sound.randomness() > 0)
                ? rng.nextFloat(sound.pitch() - sound.randomness(), sound.pitch() + sound.randomness())
                : sound.pitch();
    }

    private static SoundEvent getSoundEvent(WeaponAttributes.Sound sound) {
        return Registries.SOUND_EVENT.get(new Identifier(sound.id()));
    }
}
