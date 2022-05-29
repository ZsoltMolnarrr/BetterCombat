package net.bettercombat;

import com.google.gson.Gson;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.network.ServerNetwork;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.util.Identifier;

import java.io.File;

public class BetterCombat implements ModInitializer {

    public static final String MODID = "bettercombat";

    @Override
    public void onInitialize() {
        ServerNetwork.initializeHandlers();
        ServerLifecycleEvents.SERVER_STARTED.register((minecraftServer) -> {
            WeaponRegistry.loadAttributes();
        });
    }

//    public static WeaponAttributes CLAYMORE_ATTRBIUTES = new WeaponAttributes(
//            2.5,
//            WeaponAttributes.Held.SWORD_TWO_HANDED,
//            new WeaponAttributes.Attack[]{
//                    new WeaponAttributes.Attack(
//                            WeaponAttributes.SwingDirection.HORIZONTAL_RIGHT_TO_LEFT,
//                            -0.5,
//                            150,
//                            0.3,
//                            "slash",
//                            new WeaponAttributes.Sound[] {
//                                    new WeaponAttributes.Sound(
//                                            BetterCombat.MODID + ":" + "claymore-swing",
//                                            1F,
//                                            1F,
//                                            0.1F)
//                            },
//                            null),
//                    new WeaponAttributes.Attack(
//                            WeaponAttributes.SwingDirection.FORWARD,
//                            0,
//                            0,
//                            0.3,
//                            "slash",
//                            new WeaponAttributes.Sound[] {
//                                    new WeaponAttributes.Sound(
//                                            BetterCombat.MODID + ":" + "claymore-stab",
//                                            1F,
//                                            1F,
//                                            0.1F)
//                            },
//                            null),
//                    new WeaponAttributes.Attack(
//                            WeaponAttributes.SwingDirection.VERTICAL_TOP_TO_BOTTOM,
//                            0.5,
//                            150,
//                            0.3,
//                            "slash",
//                            new WeaponAttributes.Sound[] {
//                                    new WeaponAttributes.Sound(
//                                            BetterCombat.MODID + ":" + "claymore-slam",
//                                            1F,
//                                            1F,
//                                            0.1F)
//                            },
//                            null)
//            }
//    );
}
