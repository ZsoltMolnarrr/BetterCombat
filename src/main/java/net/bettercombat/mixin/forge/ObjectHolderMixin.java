package net.bettercombat.mixin.forge;

import net.minecraftforge.registries.ObjectHolderRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// without this i get. "Cannot find vanilla class, this should not be possible. ClassDefNotFoundError net.minecraft.world.level.block.Blocks"
// forge seems to hardcode that package in ObjectHolderRegistry#VANILLA_OBJECT_HOLDERS which doesnt work because loom has a different package name
// perhaps this breaks anything that tries to use object holder annotations
@Mixin(ObjectHolderRegistry.class)
public class ObjectHolderMixin {
    @Inject(method = "findObjectHolders", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private static void goFuckYourselfMappings(CallbackInfo ci) {
        ci.cancel();
    }
}
