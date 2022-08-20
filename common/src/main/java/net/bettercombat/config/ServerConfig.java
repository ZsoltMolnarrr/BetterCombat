package net.bettercombat.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "server")
public class ServerConfig implements ConfigData {
    @Comment("Bypass damage receive throttling of LivingEntity from player attacks.")
    public boolean allow_fast_attacks = true;
    @Comment("Allows client-side target search and server-side attack request execution against currently mounted entity of the player")
    public boolean allow_attacking_mount = false;
    @Comment("Multiplier for `attack_range`, during target lookup on both sides. " +
            "Large sized entities may be colliding with weapon hitbox, but center of entities can have bigger distance than `attack_range`")
    public float target_search_range_multiplier = 2F;
    @Comment("Total multiplier, (examples: +30% = 1.3, -30% = 0.7)")
    public float dual_wielding_attack_speed_multiplier = 1.2F;
    @Comment("Total multiplier, (examples: +30% = 1.3, -30% = 0.7)")
    public float dual_wielding_main_hand_damage_multiplier = 1F;
    @Comment("Total multiplier, (examples: +30% = 1.3, -30% = 0.7)")
    public float dual_wielding_off_hand_damage_multiplier = 1F;
    @Comment("Try to guess and apply a preset for items without weapon attributes data file")
    public boolean fallback_compatibility_enabled = true;
    @Comment("Allow printing the content of weapon attributes registry")
    public boolean weapon_registry_logging = false;
}
