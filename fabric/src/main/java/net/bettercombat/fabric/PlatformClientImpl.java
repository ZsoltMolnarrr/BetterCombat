package net.bettercombat.fabric;

import net.bettercombat.fabric.client.PekhuiIntegration;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class PlatformClientImpl {
    public static float getEntityScale(Entity entity) {
        return PekhuiIntegration.getScale(entity, new Identifier("pekhui", "entity_reach"));
    }
}
