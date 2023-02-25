package net.bettercombat.mixin;

import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class BetterCombatMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }


    private Supplier<Boolean> playerAnimatorPresent = () -> {
        boolean result;
        try {
            Class.forName("dev.kosmx.playerAnim.api.layered.IAnimation").getName();
            result = true;
        } catch(ClassNotFoundException e) {
            result = false;
        }

        boolean finalResult = result;
        playerAnimatorPresent = () -> { return finalResult; };

        return result;
    };

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!playerAnimatorPresent.get()) {
            return false;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void postApply(String targetClassName, org.objectweb.asm.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void preApply(String targetClassName, org.objectweb.asm.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}