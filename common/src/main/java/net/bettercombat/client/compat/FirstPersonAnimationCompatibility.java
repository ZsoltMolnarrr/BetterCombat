package net.bettercombat.client.compat;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import net.bettercombat.client.BetterCombatClient;
import net.fabricmc.loader.api.FabricLoader;

public class FirstPersonAnimationCompatibility {
    private static boolean isCameraModPresent = false;

    static void setup() {
        var cameraMods = new String[] {
                "firstperson", "realcamera"
        };
        for (var mod : cameraMods) {
            if (FabricLoader.getInstance().isModLoaded(mod)) {
                isCameraModPresent = true;
                break;
            }
        }
    }

    public static FirstPersonMode firstPersonMode() {
        switch (BetterCombatClient.config.firstPersonAnimations) {
            case YES:
                return FirstPersonMode.THIRD_PERSON_MODEL;
            case NO:
                return FirstPersonMode.NONE;
            default:
                return isCameraModPresent ? FirstPersonMode.NONE : FirstPersonMode.THIRD_PERSON_MODEL;
        }
    }
}
