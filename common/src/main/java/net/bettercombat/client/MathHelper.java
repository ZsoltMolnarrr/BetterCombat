package net.bettercombat.client;

public class MathHelper {
    public static double easeOutCubic(double number) {
        return 1.0 - Math.pow(1.0 - number, 3);
    }
}
