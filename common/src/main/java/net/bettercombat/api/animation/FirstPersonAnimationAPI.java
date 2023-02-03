package net.bettercombat.api.animation;

import dev.kosmx.playerAnim.api.first_person.FirstPersonRenderState;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import net.minecraft.client.network.AbstractClientPlayerEntity;

public class FirstPersonAnimationAPI {
    // Adding the given layer will make it visible in first person while it is actively playing
    public static void addLayer(AbstractClientPlayerEntity player, ModifierLayer layer) {
        // ((FirstPersonAnimator)player).addFirstPersonAnimationLayer(layer);
    }

    public static boolean isRenderingAttackAnimationInFirstPerson() {
        return FirstPersonRenderState.isRenderCycleFirstPerson();
    }
}
