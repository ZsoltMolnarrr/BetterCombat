package net.bettercombat.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.client.BetterCombatClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudInject {
    @Inject(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
    private void pre_renderCrosshair(DrawContext context, CallbackInfo ci) {
        if (BetterCombatClient.config.isHighlightCrosshairEnabled) {
            setShaderForHighlighting();
        }
    }

    @Inject(method = "renderCrosshair", at = @At(value = "TAIL"))
    private void post_renderCrosshair(DrawContext context, CallbackInfo ci) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Unique
    private void setShaderForHighlighting() {
        if (((MinecraftClient_BetterCombat) MinecraftClient.getInstance()).hasTargetsInReach()) {
            var color = BetterCombatClient.config.hudHighlightColor;
            float red = ((float) ((color >> 16) & 0xFF)) / 255F;
            float green = ((float) ((color >> 8) & 0xFF)) / 255F;
            float blue = ((float) (color & 0xFF)) / 255F;
            float alpha = 0.5F;
            RenderSystem.setShaderColor(red, green, blue, alpha);
        }
    }
}