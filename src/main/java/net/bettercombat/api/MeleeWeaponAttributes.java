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
}