package net.bettercombat.client.particle;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.fx.ParticlePlacement;
import net.bettercombat.api.fx.TrailAppearance;
import net.bettercombat.api.fx.TrailAppearanceOverride;
import net.bettercombat.client.BetterCombatClientMod;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.particle.SlashParticleEffect;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public class SlashParticleUtil {
    public record SpawnArgs(
            AbstractClientPlayer player,
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

    public static void spawnParticles(AbstractClientPlayer player, boolean isOffhand, float weaponRange, List<ParticlePlacement> settingsList, TrailAppearance appearance) {
        if (!BetterCombatClientMod.config.isShowingWeaponTrails) {
            return;
        }
        if (settingsList.isEmpty()) {
            return;
        }
        if (appearance == null) {
            return;
        }
        var isLeftHanded = player.getMainArm() == HumanoidArm.LEFT;
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
            float yaw = player.getYRot();
            float pitch = player.getXRot();
            Vec3 right = Vec3.directionFromRotation(0.0F, yaw + 90.0F).normalize();
            Vec3 forward = Vec3.directionFromRotation(pitch, yaw).normalize();
            double baseX = player.getX();
            double baseY = player.getEyeY() - 0.25 + (double)offsetY;
            double baseZ = player.getZ();
            Vec3 finalPosition = (new Vec3(baseX, baseY, baseZ)).add(forward.scale(offsetZ)).add(right.scale((offsetX * offhandFlip)));
            Vec3 stabFinalPosition = (new Vec3(finalPosition.x(), finalPosition.y(), finalPosition.z())).add(forward.scale((double)weaponRange - 1.5));
            double x = finalPosition.x();
            double y = finalPosition.y();
            double z = finalPosition.z();
            double xStab = stabFinalPosition.x();
            double yStab = stabFinalPosition.y();
            double zStab = stabFinalPosition.z();

            for (var trail: trails) {
                var posX = trail.stabPosition() ? xStab : x;
                var posY = trail.stabPosition() ? yStab : y;
                var posZ = trail.stabPosition() ? zStab : z;
                for (var layeredParticle: trail.particles()) {
                    if (appearance.primary != null) {
                        player.level().addParticle(new SlashParticleEffect(
                                layeredParticle.bottom(), weaponRange,
                                player.getXRot() + settings.pitch_addition(), player.getYRot(),
                                settings.local_yaw() * offhandFlip,
                                (settings.roll_set() + trail.rollOffset() + offhandRoll) * offhandFlip,
                                appearance.primary.glows(), appearance.primary.color_rgba()),
                                posX, posY, posZ, 0.0, 0.0, 0.0);
                    }

                    if (appearance.secondary != null) {
                        player.level().addParticle(new SlashParticleEffect(
                                layeredParticle.top(), weaponRange,
                                player.getXRot() + settings.pitch_addition(), player.getYRot(),
                                settings.local_yaw() * offhandFlip,
                                (settings.roll_set() + trail.rollOffset() + offhandRoll) * offhandFlip,
                                appearance.secondary.glows(), appearance.secondary.color_rgba()),
                                posX, posY, posZ, 0.0, 0.0, 0.0);
                    }
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

    public static TrailAppearance appearanceFor(Player attacker, ItemStack stack) {
        var defaults = BetterCombatMod.trailConfig.value.trail_appearance;
        var weaponAttributes = WeaponRegistry.getAttributes(stack);
        TrailAppearance resolved;
        if (weaponAttributes != null && weaponAttributes.trailAppearance() != null) {
            resolved = defaults.merge(weaponAttributes.trailAppearance()).resolve(stack);
        } else {
            resolved = defaults.resolve(stack);
        }
        return TrailAppearanceOverride.apply(attacker, stack, resolved);
    }
}
