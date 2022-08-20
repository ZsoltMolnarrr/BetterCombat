package net.fabricmc.fabric.api.client.rendering.v1;

import net.bettercombat.forge.events.ClientHelper;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.util.math.MatrixStack;

public interface HudRenderCallback {
    Event<HudRenderCallback> EVENT = new ClientHelper.HudRenderEvent();

    void onHudRender(MatrixStack matrixStack, float tickDelta);
}
