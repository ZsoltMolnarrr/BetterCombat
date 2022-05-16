package net.bettercombat.api;

public class MeleeWeaponAttributes {
    public double attackAngle;
    public double attackRange;
    public AttackStyle attackStyle;

    public MeleeWeaponAttributes(double attackAngle, double attackRange, AttackStyle attackStyle) {
        this.attackAngle = attackAngle;
        this.attackRange = attackRange;
        this.attackStyle = attackStyle;
    }

//    public enum Held {
//        2H_SWORD, 1H_SWORD;
//    }

    public enum SwingDirection {
        FORWARD,
        VERTICAL_TOP_TO_BOTTOM,
        HORIZONTAL_RIGHT_TO_LEFT,
        HORIZONTAL_LEFT_TO_RIGHT,
    }
    public record Attack(
        SwingDirection direction,
        double damageMultiplier,
        double angle,
        String swingSoundId,
        String impactSoundId
    ) { }
}