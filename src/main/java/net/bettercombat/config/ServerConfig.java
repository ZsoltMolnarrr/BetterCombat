package net.bettercombat.config;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.Config;
import net.bettercombat.BetterCombat;

public class ServerConfig extends Config {
    public ServerConfig() {
        super(BetterCombat.MODID, new String[]{"server"});
    }

    @ConfigEntry(comment = "Bypass damage receive throttling of LivingEntity from player attacks.")
    public boolean allow_fast_attacks = true;
    @ConfigEntry(comment = "Allows client-side target search and server-side attack request execution against currently mounted entity of the player")
    public boolean allow_attacking_mount = false;
    @ConfigEntry(comment = "Multiplier for `attack_range`, during target lookup on both sides. " +
            "Large sized entities may be colliding with weapon hitbox, but center of entities can have bigger distance than `attack_range`")
    public float target_search_range_multiplier = 2F;
    @ConfigEntry(comment = "Total multiplier, (examples: +30% = 1.3, -30% = 0.7)")
    public float dual_wielding_attack_speed_multiplier = 1.2F;
    @ConfigEntry(comment = "Total multiplier, (examples: +30% = 1.3, -30% = 0.7)")
    public float dual_wielding_main_hand_damage_multiplier = 1F;
    @ConfigEntry(comment = "Total multiplier, (examples: +30% = 1.3, -30% = 0.7)")
    public float dual_wielding_off_hand_damage_multiplier = 1F;
    @ConfigEntry(comment = "Try to guess and apply a preset for items without weapon attributes data file")
    public boolean fallback_compatibility_enabled = true;
    @ConfigEntry(comment = "Allow printing the content of weapon attributes registry")
    public boolean weapon_registry_logging = false;
}
