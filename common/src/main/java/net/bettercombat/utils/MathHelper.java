package net.bettercombat.utils;

public class MathHelper {
    // Generic
    public static float clamp(float value, float min, float max) {
        return Math.max(Math.min(value, max), min);
    }

    // Easing

    public static double easeOutCubic(double number) {
        return 1.0 - Math.pow(1.0 - number, 3);
    }

    public double easeInExpo(double x) {
        return x == 0 ? 0 : Math.pow(2, 10 * x - 10);
    }

    public double easeOutExpo(double x) {
        return x == 1 ? 1 : 1 - Math.pow(2, -10 * x);
    }
}
