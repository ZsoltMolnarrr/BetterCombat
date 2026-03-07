package net.bettercombat.client.particle;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.fx.ParticlePlacement;
import net.bettercombat.api.fx.TrailAppearance;
import net.bettercombat.client.BetterCombatClientMod;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.particle.SlashParticleEffect;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class SlashParticleUtil {
    public record SpawnArgs(
            AbstractClientPlayerEntity player,
            boolean isOffhand,
            float weaponRange,
            List<ParticlePlacement> settingsList,
            TrailAppearance appearance
    ) {}
    public record ScheduledSpawnArgs(
            SpawnArgs args,
            int time
    ) {}

    public static void spawnParticles(SpawnArgs args) {
        spawnParticles(args.player, args.isOffhand, args.weaponRange, args.settingsList, args.appearance);
    }

    public static void spawnParticles(AbstractClientPlayerEntity player, boolean isOffhand, float weaponRange, List<ParticlePlacement> settingsList, TrailAppearance appearance) {
        if (!BetterCombatClientMod.config.isShowingWeaponTrails) {
            return;
        }
        if (settingsList.isEmpty()) {
            return;
        }
        var isLeftHanded = player.getMainArm() == Arm.LEFT;
        var mirror = isOffhand;
        if (isLeftHanded) {
            mirror = !mirror;
        }
        weaponRange -= -0.25F;
        for (var settings: settingsList)  {

            var id = settings.particle_type();
            var trails = TrailParticles.ENTRIES.get(id);
            if (trails == null) {
                continue;
            }

            var offsetX = settings.x_addition();
            var offsetY = settings.y_addition();
            var offsetZ = settings.z_addition();

            float offhandRoll = mirror ? 180.0F : 0.0F;
            float offhandFlip = mirror ? -1.0F : 1.0F;
            float yaw = player.getYaw();
            float pitch = player.getPitch();
            Vec3d right = Vec3d.fromPolar(0.0F, yaw + 90.0F).normalize();
            Vec3d forward = Vec3d.fromPolar(pitch, yaw).normalize();
            double baseX = player.getX();
            double baseY = player.getEyeY() - 0.25 + (double)offsetY;
            double baseZ = player.getZ();
            Vec3d finalPosition = (new Vec3d(baseX, baseY, baseZ)).add(forward.multiply(offsetZ)).add(right.multiply((offsetX * offhandFlip)));
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
                    player.getEntityWorld().addParticleClient(new SlashParticleEffect(
                            layeredParticle.bottom(), weaponRange,
                            player.getPitch() + settings.pitch_addition(), player.getYaw(),
                            settings.local_yaw() * offhandFlip,
                            (settings.roll_set() + trail.rollOffset() + offhandRoll) * offhandFlip,
                            appearance.primary.glows(), appearance.primary.color_rgba()),
                            posX, posY, posZ, 0.0, 0.0, 0.0);

                    player.getEntityWorld().addParticleClient(new SlashParticleEffect(
                            layeredParticle.top(), weaponRange,
                            player.getPitch() + settings.pitch_addition(), player.getYaw(),
                            settings.local_yaw() * offhandFlip,
                            (settings.roll_set() + trail.rollOffset() + offhandRoll) * offhandFlip,
                            appearance.secondary.glows(), appearance.secondary.color_rgba()),
                            posX, posY, posZ, 0.0, 0.0, 0.0);
                }
            }
        }
    }

    public static List<ParticlePlacement> trailParticlesFromAttack(AttackHand attackHand) {
        if (!attackHand.attack().trailParticles().isEmpty()) {
            return attackHand.attack().trailParticles();
        }
        var config = BetterCombatMod.trailConfig.value;
        var animations = config.animation_based;
        if (animations != null) {
            var animationSpecific = animations.get(attackHand.attack().animation());
            if (animationSpecific != null) {
                return animationSpecific;
            }
        }
        return List.of();
    }

    public static TrailAppearance appearanceFromItemStack(ItemStack stack) {
        var defaults = BetterCombatMod.trailConfig.value.trail_appearance;
        var weaponAttributes = WeaponRegistry.getAttributes(stack);
        if (weaponAttributes != null && weaponAttributes.trailAppearance() != null) {
            return defaults.merge(weaponAttributes.trailAppearance()).resolve(stack);
        } else {
            return defaults.resolve(stack);
        }
    }
}
