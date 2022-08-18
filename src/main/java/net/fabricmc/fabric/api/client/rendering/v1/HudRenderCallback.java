package net.fabricmc.fabric.api.client.rendering.v1;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.List;

public class HudRenderCallback {
    public static List<BiConsumer<MatrixStack, Float>> onRenderHud = new ArrayList<>();
    public static final HudRenderEvent EVENT = new HudRenderEvent();

    public static class HudRenderEvent {
        public void register(BiConsumer<MatrixStack, Float> listener){
            onRenderHud.add(listener);
        }
    }
}
