package net.bettercombat.utils;

import net.bettercombat.api.WeaponAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Random;

public class SoundHelper {
    private static Random rng = new Random();

    public static void playSound(World world, Entity entity, WeaponAttributes.Sound sound) {
        if (sound == null) {
            return;
        }
        try {
            var id = new Identifier(sound.id());
            var soundEvent = Registry.SOUND_EVENT.get(new Identifier(sound.id()));
            float pitch = (sound.randomness() > 0)
                    ?  rng.nextFloat(sound.pitch() - sound.randomness(), sound.pitch() + sound.randomness())
                    : sound.pitch();
            world.playSound(null,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    soundEvent,
                    SoundCategory.PLAYERS,
                    sound.volume(),
                    pitch);
        } catch (Exception e) {
            System.out.println("Failed to play sound: " + sound.id());
            e.printStackTrace();
        }
    }
}
