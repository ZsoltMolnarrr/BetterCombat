package net.bettercombat.client.collision;

import net.bettercombat.api.WeaponAttributes;
import net.minecraft.world.phys.Vec3;

public class WeaponHitBoxes {
    public static Vec3 createHitbox(WeaponAttributes.HitBoxShape direction, double attackRange, boolean isSpinAttack) {
        switch (direction) {
            case FORWARD_BOX -> {
                return new Vec3(attackRange * 0.5, attackRange * 0.5 , attackRange);
            }
            case VERTICAL_PLANE -> {
                float zMultiplier = isSpinAttack ? 2 : 1;
                return new Vec3(attackRange / 3.0, attackRange * 2.0 , attackRange * zMultiplier);
            }
            case HORIZONTAL_PLANE -> {
                float zMultiplier = isSpinAttack ? 2 : 1;
                return new Vec3(attackRange * 2.0, attackRange / 3.0, attackRange * zMultiplier);
            }
        }
        return null;
    }
}
