package net.bettercombat;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;

public class ServerConfig implements ConfigGroup {
    @ConfigEntry(comment = "Bypass damage receive throttling of LivingEntity from player attacks.")
    public boolean allow_fast_attacks = true;
    @ConfigEntry(comment = "Allows client-side target search and server-side attack request execution against currently mounted entity of the player")
    public boolean allow_attacking_mount = false;
    @ConfigEntry(comment = "Multiplier for `attack_range`, during target lookup on both sides. " +
            "Large sized entities may be colliding with weapon hitbox, but center of entities can have bigger distance than `attack_range`")
    public float target_search_range_multiplier = 2F;
    @ConfigEntry(comment = "Total multiplier, (examples: +30% = 1.3, -30% = 0.7)")
    public float dual_wielding_attack_speed_multiplier = 2F;
    @ConfigEntry(comment = "Total multiplier, (examples: +30% = 1.3, -30% = 0.7)")
    public float dual_wielding_main_hand_damage_multiplier = 0.7F;
    @ConfigEntry(comment = "Total multiplier, (examples: +30% = 1.3, -30% = 0.7)")
    public float dual_wielding_off_hand_damage_multiplier = 0.5F;
}
