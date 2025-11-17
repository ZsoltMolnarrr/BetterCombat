package net.bettercombat.client.particle;

import net.bettercombat.api.AttackHand;
import net.bettercombat.network.Packets;
import net.bettercombat.particle.BetterCombatParticles;
import net.bettercombat.particle.SlashParticleEffect;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Map;

public class ParticleUtil {


    // public static final Map<Identifier,

    public static void spawnParticles(ClientPlayerEntity player, AttackHand hand, Packets.SwingParticles settingsList, float weaponRange, boolean light, String colorHex) {

        for (var settings: settingsList.particles())  {

            var id = Identifier.of(settings.id());
            var resolvedType = Registries.PARTICLE_TYPE.get(id);
            var particleType = (ParticleType<SlashParticleEffect>)resolvedType;


            // TODO
            var offsetX = 0F;
            var offsetY = 0F;
            var offsetZ = 0F;
            var pitchOffset = 0F;
            var yawOffset = 0F;
            var rollOffset = 0F;

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

            player.getWorld().addParticle(new SlashParticleEffect(particleType, weaponRange, pitch + pitchOffset, yaw, yawOffset * offhandFlip, (rollOffset - 45.0F + offhandRoll) * offhandFlip, light, colorHex), xStab, yStab, zStab, 0.0, 0.0, 0.0);

//            switch (settings.particleType()) {
//                case "stab":
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.BOTSTAB, weaponRange, player.getPitch() + settings.pitchAddition(), player.getYaw(), settings.localYaw() * offhandFlip, (settings.rollSet() - 45.0F + offhandRoll) * offhandFlip, light, colorHex), xStab, yStab, zStab, 0.0, 0.0, 0.0);
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.BOTSTAB, weaponRange, player.getPitch() + settings.pitchAddition(), player.getYaw(), settings.localYaw() * offhandFlip, (settings.rollSet() + 45.0F + offhandRoll) * offhandFlip, light, colorHex), xStab, yStab, zStab, 0.0, 0.0, 0.0);
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.TOPSTAB, weaponRange, player.getPitch() + settings.pitchAddition(), player.getYaw(), settings.localYaw() * offhandFlip, (settings.rollSet() - 45.0F + offhandRoll) * offhandFlip, light, colorHexSec), xStab, yStab, zStab, 0.0, 0.0, 0.0);
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.TOPSTAB, weaponRange, player.getPitch() + settings.pitchAddition(), player.getYaw(), settings.localYaw() * offhandFlip, (settings.rollSet() + 45.0F + offhandRoll) * offhandFlip, light, colorHexSec), xStab, yStab, zStab, 0.0, 0.0, 0.0);
//                    break;
//                case "slash45":
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.BOTSLASH45, weaponRange, player.getPitch() + settings.pitchAddition(), player.getYaw(), settings.localYaw() * offhandFlip, (settings.rollSet() + offhandRoll) * offhandFlip, light, colorHex), x, y, z, 0.0, 0.0, 0.0);
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.TOPSLASH45, weaponRange, player.getPitch() + settings.pitchAddition(), player.getYaw(), settings.localYaw() * offhandFlip, (settings.rollSet() + offhandRoll) * offhandFlip, light, colorHexSec), x, y, z, 0.0, 0.0, 0.0);
//                    break;
//                case "slash90":
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.BOTSLASH90, weaponRange, player.getPitch() + settings.pitchAddition(), player.getYaw(), settings.localYaw() * offhandFlip, (settings.rollSet() + offhandRoll) * offhandFlip, light, colorHex), x, y, z, 0.0, 0.0, 0.0);
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.TOPSLASH90, weaponRange, player.getPitch() + settings.pitchAddition(), player.getYaw(), settings.localYaw() * offhandFlip, (settings.rollSet() + offhandRoll) * offhandFlip, light, colorHexSec), x, y, z, 0.0, 0.0, 0.0);
//                    break;
//                case "slash180":
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.BOTSLASH180, weaponRange, player.getPitch() + settings.pitchAddition(), player.getYaw(), settings.localYaw() * offhandFlip, (settings.rollSet() + offhandRoll) * offhandFlip, light, colorHex), x, y, z, 0.0, 0.0, 0.0);
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.TOPSLASH180, weaponRange, player.getPitch() + settings.pitchAddition(), player.getYaw(), settings.localYaw() * offhandFlip, (settings.rollSet() + offhandRoll) * offhandFlip, light, colorHexSec), x, y, z, 0.0, 0.0, 0.0);
//                    break;
//                case "slash270":
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.BOTSLASH270, weaponRange, player.getPitch() + settings.pitchAddition(), player.getYaw(), settings.localYaw() * offhandFlip, (settings.rollSet() + offhandRoll) * offhandFlip, light, colorHex), x, y, z, 0.0, 0.0, 0.0);
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.TOPSLASH270, weaponRange, player.getPitch() + settings.pitchAddition(), player.getYaw(), settings.localYaw() * offhandFlip, (settings.rollSet() + offhandRoll) * offhandFlip, light, colorHexSec), x, y, z, 0.0, 0.0, 0.0);
//                    break;
//                case "slash360":
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.BOTSLASH360, weaponRange, player.getPitch() + settings.pitchAddition(), player.getYaw(), settings.localYaw() * offhandFlip, (settings.rollSet() + offhandRoll) * offhandFlip, light, colorHex), x, y, z, 0.0, 0.0, 0.0);
//                    player.getWorld().addParticle(new SlashParticleEffect(ModParticles.TOPSLASH360, weaponRange, player.getPitch() + settings.pitchAddition(), player.getYaw(), settings.localYaw() * offhandFlip, (settings.rollSet() + offhandRoll) * offhandFlip, light, colorHexSec), x, y, z, 0.0, 0.0, 0.0);
//            }
        }
    }
}
