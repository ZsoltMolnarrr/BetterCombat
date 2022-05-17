package net.bettercombat.client.collision;

import net.bettercombat.api.WeaponAttributes;
import net.minecraft.util.math.Vec3d;

public class WeaponHitBoxes {
    public static Vec3d createHitbox(WeaponAttributes.SwingDirection direction, double attackRange) {
        switch (direction) {
            case FORWARD -> {
                return new Vec3d(attackRange * 0.5, attackRange * 0.5 , attackRange * 1.2);
            }
            case VERTICAL_TOP_TO_BOTTOM -> {
                return new Vec3d(attackRange / 3.0, attackRange * 2.0 , attackRange);
            }
            case HORIZONTAL_RIGHT_TO_LEFT, HORIZONTAL_LEFT_TO_RIGHT -> {
                return new Vec3d(attackRange * 2.0, attackRange / 3.0, attackRange);
            }
        }
        return null;
    }
}
