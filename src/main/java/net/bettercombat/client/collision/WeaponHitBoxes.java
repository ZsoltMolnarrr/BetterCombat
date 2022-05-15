package net.bettercombat.client.collision;

import net.bettercombat.api.AttackStyle;
import net.minecraft.util.math.Vec3d;

public class WeaponHitBoxes {
    public static Vec3d createHitbox(AttackStyle attackStyle, double attackRange) {
        switch (attackStyle) {
            case SLASH_VERTICAL_TOP_TO_BOTTOM -> {
                return new Vec3d(attackRange / 3.0, attackRange * 2.0 , attackRange);
            }
            case SLASH_HORIZONTAL_RIGHT_TO_LEFT -> {
                return new Vec3d(attackRange * 2.0, attackRange / 3.0, attackRange);
            }
        }
        return null;
    }
}
