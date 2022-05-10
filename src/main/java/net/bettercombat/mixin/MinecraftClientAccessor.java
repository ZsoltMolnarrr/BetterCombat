package net.bettercombat.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
    @Accessor
    int getAttackCooldown();

    @Accessor("attackCooldown")
    public void setAttackCooldown(int attackCooldown);
}