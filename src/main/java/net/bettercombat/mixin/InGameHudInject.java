package net.bettercombat.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.client.MinecraftClientExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public abstract class InGameHudInject {
    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"))
    public void pre_renderCrosshair(InGameHud instance, MatrixStack matrixStack, int x, int y, int u, int v, int width, int height) {
        if (u == 0 && v == 0) {
            if (BetterCombatClient.config.isHighlightCrosshairEnabled) {
                setShaderForHighlighting();
            }
        } else {
            if (BetterCombatClient.config.isHighlightAttackIndicatorEnabled) {
                setShaderForHighlighting();
            }
        }
        DrawableHelper.drawTexture(matrixStack, x, y, instance.getZOffset(), u, v, width, height, 256, 256);
    }

    private void setShaderForHighlighting() {
        if(((MinecraftClientExtension)MinecraftClient.getInstance()).hasTargetsInRange()) {
            var color = BetterCombatClient.config.hudHighlightColor;
            float red = (float)color.getRed() / 255F;
            float green = (float)color.getGreen() / 255F;
            float blue = (float)color.getBlue() / 255F;
            float alpha = (float)color.getAlpha() / 255F;
            RenderSystem.setShaderColor(red, green, blue, alpha);
        }
    }
}