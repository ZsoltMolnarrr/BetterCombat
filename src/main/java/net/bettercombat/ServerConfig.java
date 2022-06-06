package net.bettercombat;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;

public class ServerConfig implements ConfigGroup {
    @ConfigEntry(comment = "Bypass damage receive throttling in LivingEntity from player attacks.")
    public boolean allow_fast_attacks = true;
    @ConfigEntry(comment = "Total multiplier, (examples: for +30% use 1.3, for -30% use 0.7)")
    public float dual_wielding_attack_speed_multiplier = 2F;
    @ConfigEntry(comment = "Total multiplier, (examples: for +30% use 1.3, for -30% use 0.7)")
    public float dual_wielding_main_hand_damage_multiplier = 0.7F;
    @ConfigEntry(comment = "Total multiplier, (examples: for +30% use 1.3, for -30% use 0.7)")
    public float dual_wielding_off_hand_damage_multiplier = 0.5F;
}
