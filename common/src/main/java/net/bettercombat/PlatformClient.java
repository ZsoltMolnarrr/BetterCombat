package net.bettercombat;

import dev.architectury.injectables.annotations.ExpectPlatform;
import java.util.List;
import net.minecraft.world.entity.player.Player;

public class PlatformClient {
    @ExpectPlatform
    public static void onEmptyLeftClick(Player player) {
        throw new AssertionError();
    }
}
