package net.bettercombat.mixin.player;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.EntityPlayer_BetterCombat;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.entity.EquipmentSlot.OFFHAND;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerAttackProperties, EntityPlayer_BetterCombat {
    private int comboCount = 0;
    public int getComboCount() {
        return comboCount;
    }
    public void setComboCount(int comboCount) {
        this.comboCount = comboCount;
    }

    private static final TrackedData<String> BETTER_COMBAT_MAIN_IDLE_ANIMATION = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<String> BETTER_COMBAT_OFF_IDLE_ANIMATION = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.STRING);

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initDataTracker_TAIL_SpellEngine_SyncEffects(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(BETTER_COMBAT_MAIN_IDLE_ANIMATION, "");
        builder.add(BETTER_COMBAT_OFF_IDLE_ANIMATION, "");
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void post_Tick(CallbackInfo ci) {
        var instance = (Object)this;
        var player = ((PlayerEntity)instance);

        if (player.getWorld().isClient()) {
            ((PlayerAttackAnimatable) this).updateAnimationsOnTick();
        } else {
            var pose = PlayerAttackHelper.poseForPlayer(player);
            player.getDataTracker().set(BETTER_COMBAT_MAIN_IDLE_ANIMATION, pose.base());
            player.getDataTracker().set(BETTER_COMBAT_OFF_IDLE_ANIMATION, pose.offHand());
        }
        updateDualWieldingSpeedBoost();
    }

    public String getMainHandIdleAnimation() {
        return ((PlayerEntity) ((Object)this)).getDataTracker().get(BETTER_COMBAT_MAIN_IDLE_ANIMATION);
    }

    public String getOffHandIdleAnimation() {
        return ((PlayerEntity) ((Object)this)).getDataTracker().get(BETTER_COMBAT_OFF_IDLE_ANIMATION);
    }

    // FEATURE: Disable sweeping for attributed weapons

    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 3)
    private boolean disableSweeping(boolean value) {
        if (BetterCombatMod.config.allow_vanilla_sweeping) {
            return value;
        }

        var player = ((PlayerEntity) ((Object)this));
        var currentHand = PlayerAttackHelper.getCurrentAttack(player, comboCount);
        if (currentHand != null) {
            // Disable sweeping
            return false;
        }
        return value;
    }

    // FEATURE: Two-handed wielding

    @Inject(method = "getEquippedStack", at = @At("HEAD"), cancellable = true)
    public void getEquippedStack_Pre(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
        var mainHandHasTwoHanded = false;
        var mainHandStack = ((PlayerEntityAccessor) this).getInventory().getMainHandStack();
        var mainHandAttributes = WeaponRegistry.getAttributes(mainHandStack);
        if (mainHandAttributes != null && mainHandAttributes.isTwoHanded()) {
            mainHandHasTwoHanded = true;
        }

        var offHandHasTwoHanded = false;
        var offHandStack = ((PlayerEntityAccessor)this).getInventory().offHand.get(0);
        var offHandAttributes = WeaponRegistry.getAttributes(offHandStack);
        if(offHandAttributes != null && offHandAttributes.isTwoHanded()) {
            offHandHasTwoHanded = true;
        }

        if (slot == OFFHAND) {
            if (mainHandHasTwoHanded || offHandHasTwoHanded) {
                cir.setReturnValue(ItemStack.EMPTY);
                cir.cancel();
                return;
            }
        }
    }

    // FEATURE: Dual wielding

    private Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> dualWieldingAttributeMap;
    private static final Identifier dualWieldingSpeedModifierId = Identifier.of(BetterCombatMod.ID, "dual_wield");


    // FIXME: Replace with high level multiplied Mixin, WrapOperation player.getAttributes(...)
    private void updateDualWieldingSpeedBoost() {
        var player = ((PlayerEntity) ((Object)this));
        var newState = PlayerAttackHelper.isDualWielding(player);
        var currentState = dualWieldingAttributeMap != null;
        if (newState != currentState) {
            if(newState) {
                // Just started dual wielding
                // Adding speed boost modifier
                this.dualWieldingAttributeMap = HashMultimap.create();
                double multiplier = BetterCombatMod.config.dual_wielding_attack_speed_multiplier - 1;
                dualWieldingAttributeMap.put(
                        EntityAttributes.ATTACK_SPEED,
                        new EntityAttributeModifier(
                                dualWieldingSpeedModifierId,
                                multiplier,
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                player.getAttributes().addTemporaryModifiers(dualWieldingAttributeMap);
            } else {
                // Just stopped dual wielding
                // Removing speed boost modifier
                if (dualWieldingAttributeMap != null) { // Safety first... Who knows...
                    player.getAttributes().removeModifiers(dualWieldingAttributeMap);
                    dualWieldingAttributeMap = null;
                }
            }
        }
    }

    @ModifyArg(method = "attack", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"),
            index = 0)
    public Hand getHand(Hand hand) {
        var player = ((PlayerEntity) ((Object)this) );
        var currentHand = PlayerAttackHelper.getCurrentAttack(player, comboCount);
        if (currentHand != null) {
            return currentHand.isOffHand() ? Hand.OFF_HAND : Hand.MAIN_HAND;
        } else {
            return Hand.MAIN_HAND;
        }
    }

    private AttackHand lastAttack;

    @Redirect(method = "attack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getMainHandStack()Lnet/minecraft/item/ItemStack;"))
    public ItemStack getMainHandStack_Redirect(PlayerEntity instance) {
        // DUAL WIELDING LOGIC
        // Here we return the off-hand stack as fake main-hand, purpose:
        // - Getting enchants
        // - Getting itemstack to be damaged
        if (comboCount < 0) {
            // Vanilla behaviour
            return instance.getMainHandStack();
        }
        var hand = PlayerAttackHelper.getCurrentAttack(instance, comboCount);
        if (hand == null) {
            var isOffHand = PlayerAttackHelper.shouldAttackWithOffHand(instance, comboCount);
            if (isOffHand) {
                return ItemStack.EMPTY;
            } else {
                return instance.getMainHandStack();
            }
        }
        lastAttack = hand;
        return hand.itemStack();
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;setStackInHand(Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)V"))
    public void setStackInHand_Redirect(PlayerEntity instance, Hand handArg, ItemStack itemStack) {
        // DUAL WIELDING LOGIC
        // In case item got destroyed due to durability loss
        // We empty the correct hand
        if (comboCount < 0) {
            // Vanilla behaviour
            instance.setStackInHand(handArg, itemStack);
        }
        // `handArg` argument is always `MAIN`, we can ignore it
        AttackHand hand = lastAttack;
        if (hand == null) {
            hand = PlayerAttackHelper.getCurrentAttack(instance, comboCount);
        }
        if (hand == null) {
            instance.setStackInHand(handArg, itemStack);
            return;
        }
        var redirectedHand = hand.isOffHand() ? Hand.OFF_HAND : Hand.MAIN_HAND;
        instance.setStackInHand(redirectedHand, itemStack);
    }

    // SECTION: BetterCombatPlayer

    @Nullable
    public AttackHand getCurrentAttack() {
        if (comboCount < 0) {
            return null;
        }
        var player = ((PlayerEntity) ((Object)this));
        return PlayerAttackHelper.getCurrentAttack(player, comboCount);
    }
}
