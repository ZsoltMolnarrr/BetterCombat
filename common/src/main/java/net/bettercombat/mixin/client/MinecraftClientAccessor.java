package net.bettercombat.mixin.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftClientAccessor {
    @Accessor
    int getMissTime();
    @Accessor("missTime")
    void setAttackCooldown(int attackCooldown);
    @Accessor("rightClickDelay")
    void setItemUseCooldown(int itemUseCooldown);
}