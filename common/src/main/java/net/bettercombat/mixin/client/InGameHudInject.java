package net.bettercombat.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.client.BetterCombatClientMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Hud.class)   // 26.2: crosshair extraction moved from Gui to Hud
public abstract class InGameHudInject {
    @WrapOperation(
            method = "extractCrosshair",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private void renderCrosshair_WrapOperation(GuiGraphicsExtractor context, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original) {
        if (BetterCombatClientMod.config.isHighlightCrosshairEnabled
            && ((MinecraftClient_BetterCombat) Minecraft.getInstance()).hasTargetsInReach()) {
            float alpha = 0.5F;

            var color = BetterCombatClientMod.config.hudHighlightColor;
            float red = ((float) ((color >> 16) & 0xFF)) / 255F;
            float green = ((float) ((color >> 8) & 0xFF)) / 255F;
            float blue = ((float) (color & 0xFF)) / 255F;

            int colorARGB = ARGB.colorFromFloat(alpha, red, green, blue);

            context.blitSprite(pipeline, sprite, x, y, width, height, colorARGB);
        } else {
            original.call(context, pipeline, sprite, x, y, width, height);
        }
    }
}