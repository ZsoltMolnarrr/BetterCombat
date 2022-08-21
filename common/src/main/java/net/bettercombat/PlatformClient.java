package net.bettercombat;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.Entity;

public class PlatformClient {
    @ExpectPlatform
    public static float getEntityScale(Entity entity) {
        throw new AssertionError();
    }
}
