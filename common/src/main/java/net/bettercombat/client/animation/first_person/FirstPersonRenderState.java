package net.bettercombat.client.animation.first_person;


public class FirstPersonRenderState {
    private static FirstPersonAnimation renderCycleData;
    public static boolean isRenderCycleFirstPerson() {
        return renderCycleData != null;
    }

    public static FirstPersonAnimation getRenderCycleData() {
        return renderCycleData;
    }

    public static void setFirstPersonRenderCycle(FirstPersonAnimation animation) {
        renderCycleData = animation;
    }

    public static void clearFirstPersonRenderCycle() {
        renderCycleData = null;
    }
}
