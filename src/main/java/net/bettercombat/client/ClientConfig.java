package net.bettercombat.client;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

import java.util.function.Function;

public class ClientConfig implements ConfigGroup {
    @ConfigEntry
    public boolean isHoldToAttackEnabled = true;
    @ConfigEntry
    public boolean isMiningWithWeaponsEnabled = true;
    @ConfigEntry
    public boolean isSwingThruGrassEnabled = true;

//    public Listener listener;
//    public interface Listener {
//        void feintKeyUpdated();
//    }
//
//    public void setFeintKey(Key feintKey) {
//        this.feintKey = feintKey;
//        if (listener != null) {
//            listener.feintKeyUpdated();
//        }
//    }
}
