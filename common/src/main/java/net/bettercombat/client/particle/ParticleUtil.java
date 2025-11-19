package net.bettercombat.client.particle;

import malfu.bc_particle.particle.ModParticles;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.trail.ParticleSettings;
import net.bettercombat.particle.SlashParticleEffect;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class ParticleUtil {
    public static void spawnParticles(ClientPlayerEntity player, AttackHand hand, List<ParticleSettings> settingsList, float weaponRange, boolean light, String colorHex) {
        if (settingsList.isEmpty()) {
            return;
        }
        for (var settings: settingsList)  {

            var id = settings.particle_type();
            var trails = TrailParticles.ENTRIES.get(id);
            if (trails == null) {
                continue;
            }

            var offsetX = settings.x_addition();
            var offsetY = settings.y_addition();
            var offsetZ = settings.z_addition();
            var pitchOffset = settings.pitch_addition();
            var yawOffset = settings.local_yaw();
            var rollOffset = settings.roll_set();

            boolean isOffhand = hand.isOffHand();
            float offhandRoll = isOffhand ? 180.0F : 0.0F;
            float offhandFlip = isOffhand ? -1.0F : 1.0F;
            float yaw = player.getYaw();
            float pitch = player.getPitch();
            Vec3d right = Vec3d.fromPolar(0.0F, yaw + 90.0F).normalize();
            Vec3d forward = Vec3d.fromPolar(pitch, yaw).normalize();
            double baseX = player.getX();
            double baseY = player.getEyeY() - 0.25 + (double)offsetY;
            double baseZ = player.getZ();
            Vec3d finalPosition = (new Vec3d(baseX, baseY, baseZ)).add(forward.multiply((double)offsetZ)).add(right.multiply((double)(offsetX * offhandFlip)));
            Vec3d stabFinalPosition = (new Vec3d(finalPosition.getX(), finalPosition.getY(), finalPosition.getZ())).add(forward.multiply((double)weaponRange - 1.5));
            double x = finalPosition.getX();
            double y = finalPosition.getY();
            double z = finalPosition.getZ();
            double xStab = stabFinalPosition.getX();
            double yStab = stabFinalPosition.getY();
            double zStab = stabFinalPosition.getZ();

            for (var trail: trails) {
                var posX = trail.stabPosition() ? xStab : x;
                var posY = trail.stabPosition() ? yStab : y;
                var posZ = trail.stabPosition() ? zStab : z;
                for (var layeredParticle: trail.particles()) {

                    player.getWorld().addParticle(new SlashParticleEffect(
                            layeredParticle.top(), weaponRange,
                            player.getPitch() + settings.pitch_addition(), player.getYaw(),
                            settings.local_yaw() * offhandFlip,
                            (settings.roll_set() + trail.rollOffset() + offhandRoll) * offhandFlip, light, colorHex),
                            posX, posY, posZ, 0.0, 0.0, 0.0);


                    player.getWorld().addParticle(new SlashParticleEffect(
                                    layeredParticle.bottom(), weaponRange,
                                    player.getPitch() + settings.pitch_addition(), player.getYaw(),
                                    settings.local_yaw() * offhandFlip,
                                    (settings.roll_set() + trail.rollOffset() + offhandRoll) * offhandFlip, light, colorHex),
                            posX, posY, posZ, 0.0, 0.0, 0.0);


//                    player.getWorld().addParticle(new SlashParticleEffect(layeredParticle.top(), weaponRange,
//                            pitch + pitchOffset, yaw, yawOffset * offhandFlip, (rollOffset + trail.rollOffset() + offhandRoll) * offhandFlip,
//                                    light, colorHex),
//                            posX, posY, posZ,
//                            0.0, 0.0, 0.0);
//                    player.getWorld().addParticle(new SlashParticleEffect(layeredParticle.bottom(), weaponRange,
//                                    pitch + pitchOffset, yaw, yawOffset * offhandFlip, (rollOffset + trail.rollOffset() + offhandRoll) * offhandFlip,
//                                    light, "999999"),
//                            posX, posY, posZ,
//                            0.0, 0.0, 0.0);
                }
            }


//            switch (settings.particle_type()) {
//                case "stab":
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.BOTSTAB, weaponRange, player.getPitch() + settings.pitch_addition(), player.getYaw(), settings.local_yaw() * offhandFlip, (settings.roll_set() - 45.0F + offhandRoll) * offhandFlip, light, colorHex), xStab, yStab, zStab, 0.0, 0.0, 0.0);
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.BOTSTAB, weaponRange, player.getPitch() + settings.pitch_addition(), player.getYaw(), settings.local_yaw() * offhandFlip, (settings.roll_set() + 45.0F + offhandRoll) * offhandFlip, light, colorHex), xStab, yStab, zStab, 0.0, 0.0, 0.0);
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.TOPSTAB, weaponRange, player.getPitch() + settings.pitch_addition(), player.getYaw(), settings.local_yaw() * offhandFlip, (settings.roll_set() - 45.0F + offhandRoll) * offhandFlip, light, colorHexSec), xStab, yStab, zStab, 0.0, 0.0, 0.0);
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.TOPSTAB, weaponRange, player.getPitch() + settings.pitch_addition(), player.getYaw(), settings.local_yaw() * offhandFlip, (settings.roll_set() + 45.0F + offhandRoll) * offhandFlip, light, colorHexSec), xStab, yStab, zStab, 0.0, 0.0, 0.0);
//                    break;
//                case "slash45":
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.BOTSLASH45, weaponRange, player.getPitch() + settings.pitch_addition(), player.getYaw(), settings.local_yaw() * offhandFlip, (settings.roll_set() + offhandRoll) * offhandFlip, light, colorHex), x, y, z, 0.0, 0.0, 0.0);
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.TOPSLASH45, weaponRange, player.getPitch() + settings.pitch_addition(), player.getYaw(), settings.local_yaw() * offhandFlip, (settings.roll_set() + offhandRoll) * offhandFlip, light, colorHexSec), x, y, z, 0.0, 0.0, 0.0);
//                    break;
//                case "slash90":
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.BOTSLASH90, weaponRange, player.getPitch() + settings.pitch_addition(), player.getYaw(), settings.local_yaw() * offhandFlip, (settings.roll_set() + offhandRoll) * offhandFlip, light, colorHex), x, y, z, 0.0, 0.0, 0.0);
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.TOPSLASH90, weaponRange, player.getPitch() + settings.pitch_addition(), player.getYaw(), settings.local_yaw() * offhandFlip, (settings.roll_set() + offhandRoll) * offhandFlip, light, colorHexSec), x, y, z, 0.0, 0.0, 0.0);
//                    break;
//                case "slash180":
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.BOTSLASH180, weaponRange, player.getPitch() + settings.pitch_addition(), player.getYaw(), settings.local_yaw() * offhandFlip, (settings.roll_set() + offhandRoll) * offhandFlip, light, colorHex), x, y, z, 0.0, 0.0, 0.0);
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.TOPSLASH180, weaponRange, player.getPitch() + settings.pitch_addition(), player.getYaw(), settings.local_yaw() * offhandFlip, (settings.roll_set() + offhandRoll) * offhandFlip, light, colorHexSec), x, y, z, 0.0, 0.0, 0.0);
//                    break;
//                case "slash270":
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.BOTSLASH270, weaponRange, player.getPitch() + settings.pitch_addition(), player.getYaw(), settings.local_yaw() * offhandFlip, (settings.roll_set() + offhandRoll) * offhandFlip, light, colorHex), x, y, z, 0.0, 0.0, 0.0);
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.TOPSLASH270, weaponRange, player.getPitch() + settings.pitch_addition(), player.getYaw(), settings.local_yaw() * offhandFlip, (settings.roll_set() + offhandRoll) * offhandFlip, light, colorHexSec), x, y, z, 0.0, 0.0, 0.0);
//                    break;
//                case "slash360":
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.BOTSLASH360, weaponRange, player.getPitch() + settings.pitch_addition(), player.getYaw(), settings.local_yaw() * offhandFlip, (settings.roll_set() + offhandRoll) * offhandFlip, light, colorHex), x, y, z, 0.0, 0.0, 0.0);
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.TOPSLASH360, weaponRange, player.getPitch() + settings.pitch_addition(), player.getYaw(), settings.local_yaw() * offhandFlip, (settings.roll_set() + offhandRoll) * offhandFlip, light, colorHexSec), x, y, z, 0.0, 0.0, 0.0);
//            }
        }
    }
}
