package net.bettercombat.client.compat;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import net.bettercombat.client.BetterCombatClientMod;
import net.bettercombat.config.TriStateAuto;
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
        switch (BetterCombatClientMod.config.firstPersonAnimations) {
            case TriStateAuto.YES:
                return FirstPersonMode.THIRD_PERSON_MODEL;
            case TriStateAuto.NO:
                return FirstPersonMode.NONE;
            default:
                return isCameraModPresent ? FirstPersonMode.NONE : FirstPersonMode.THIRD_PERSON_MODEL;
        }
    }
}
