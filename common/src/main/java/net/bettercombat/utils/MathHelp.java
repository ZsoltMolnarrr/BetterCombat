package net.bettercombat.utils;

public class MathHelp {
    public static float clamp(float value, float min, float max) {
        return Math.max(Math.min(value, max), min);
    }
}
