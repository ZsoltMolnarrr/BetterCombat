package net.bettercombat.forge.mixin;

import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ObjectHolderRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// without this i get. "Cannot find vanilla class, this should not be possible. ClassDefNotFoundError net.minecraft.world.level.block.Blocks"
// forge seems to hardcode that package in ObjectHolderRegistry#VANILLA_OBJECT_HOLDERS which doesnt work because loom has a different package name
// if you remove this running the forge client in the dev environment won't work
@Mixin(ObjectHolderRegistry.class)
public class ForgeObjectHolderMixin {
    @Inject(method = "findObjectHolders", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private static void cancel(CallbackInfo ci) {
        if (!FMLEnvironment.production) ci.cancel();
    }
}