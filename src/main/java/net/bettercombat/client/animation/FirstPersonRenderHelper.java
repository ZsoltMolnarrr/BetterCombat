package net.bettercombat.client.animation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;

public class FirstPersonRenderHelper {
    public static boolean isRenderingFirstPersonPlayerModel = false;
    public static boolean isRenderingThirdPersonPlayerModel = false;

    public static boolean isRenderingFirstPerson() {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        return !camera.isThirdPerson();
    }
}
