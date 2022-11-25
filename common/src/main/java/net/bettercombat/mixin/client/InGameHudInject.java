package net.bettercombat.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudInject {
    @Inject(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"))
    public void pre_renderCrosshair(MatrixStack matrices, CallbackInfo ci) {
        if (BetterCombatClient.config.isHighlightCrosshairEnabled) {
            setShaderForHighlighting();
        }
    }

    private void setShaderForHighlighting() {
        if(((MinecraftClient_BetterCombat)MinecraftClient.getInstance()).hasTargetsInReach()) {
            var color = BetterCombatClient.config.hudHighlightColor;
            float red = ((float) ((color >> 16) & 0xFF)) / 255F;
            float green = ((float) ((color >> 8) & 0xFF)) / 255F;
            float blue = ((float) (color & 0xFF)) / 255F;
            float alpha = 0.5F;
            RenderSystem.setShaderColor(red, green, blue, alpha);
        }
    }
}