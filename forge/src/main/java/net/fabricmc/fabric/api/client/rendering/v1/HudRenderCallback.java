package net.fabricmc.fabric.api.client.rendering.v1;

import ca.lukegrahamlandry.bettercombat.events.ClientHelper;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.List;

public interface HudRenderCallback {
    Event<HudRenderCallback> EVENT = new ClientHelper.HudRenderEvent();

    void onHudRender(MatrixStack matrixStack, float tickDelta);
}
