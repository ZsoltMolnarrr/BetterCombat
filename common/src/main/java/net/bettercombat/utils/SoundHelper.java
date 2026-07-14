package net.bettercombat.utils;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.Platform;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.network.Packets;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.Random;

public class SoundHelper {
    private static Random rng = new Random();

    public static void playSound(ServerLevel world, Entity entity, WeaponAttributes.Sound sound) {
        if (sound == null) {
            return;
        }
        
        try {
            float pitch = (sound.randomness() > 0)
                    ?  rng.nextFloat(sound.pitch() - sound.randomness(), sound.pitch() + sound.randomness())
                    : sound.pitch();
            var packet = new Packets.AttackSound(
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    sound.id(),
                    sound.volume(),
                    pitch,
                    rng.nextLong());

            var soundEvent = BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse(sound.id()));
            var distance = soundEvent.getRange(sound.volume());
            var origin = new Vec3(entity.getX(), entity.getY(), entity.getZ());
            Platform.around(world, origin, distance).forEach(serverPlayer -> {
                var channel = Packets.AttackSound.ID;
                try {
                    if (Platform.networkS2C_CanSend(serverPlayer, channel)) {
                        Platform.networkS2C_Send(serverPlayer, packet);
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
            var soundId = Identifier.fromNamespaceAndPath(BetterCombatMod.ID, soundKey);
            var soundEvent = SoundEvent.createVariableRangeEvent(soundId);
            Registry.register(BuiltInRegistries.SOUND_EVENT, soundId, soundEvent);
        }
    }
}
