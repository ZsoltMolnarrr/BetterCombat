package net.bettercombat.mixin.player;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor
    int getLastAttackedTicks();
    @Accessor("lastAttackedTicks")
    void setLastAttackedTicks(int lastAttackedTicks);
    @Invoker("turnHead")
    void invokeTurnHead(float bodyRotation);
}
