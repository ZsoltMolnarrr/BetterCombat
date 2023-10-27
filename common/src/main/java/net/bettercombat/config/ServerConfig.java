package net.bettercombat.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.bettercombat.logic.TargetHelper;

import java.util.LinkedHashMap;

@Config(name = "server")
public class ServerConfig implements ConfigData {
    @Comment("""
            Upswing (aka windup) is the first phase of the attack (between clicking and performing the damage).
            Typical duration of upswing is `weapon cooldown * 0.5`. (Weapon specific upswing values can be defined in weapon attributes)
            This config allows you to change upswing duration.
            Example values:
            - `0.5` (default, fast paced attack initiation) upswing typically lasts 25% of the attack cooldown
            - `1.0` (classic setting, realistic attack initiation) upswing typically lasts 50% of the attack cooldown""")
    public float upswing_multiplier = 0.5F;
    @Comment("Bypass damage receive throttling of LivingEntity from player attacks.")
    public boolean allow_fast_attacks = true;
    @Comment("Allows client-side target search and server-side attack request execution against currently mounted entity of the player")
    public boolean allow_attacking_mount = false;
    @Comment("The minimum number of ticks between two attacks")
    public int attack_interval_cap = 2;
    @Comment("""
            Blacklist for entities that are acting as vehicle but should not be treated as protected mounts.
            Classical example is an alexsmobs:crocodile attempting a death spin.
            (Note all hostile mobs hittable by default, this config is to fix faulty mobs)""")
    public String[] hostile_player_vehicles = {"alexsmobs:crocodile"};
    @Comment("Allows vanilla sweeping mechanic to work and Sweeping Edge enchantment")
    public boolean allow_vanilla_sweeping = false;
    @Comment("Allows new sweeping mechanic (by Better Combat) to work, including Sweeping Edge enchantment")
    public boolean allow_reworked_sweeping = true;
    @Comment("""
            The more additional targets a weapon swing hits, the weaker it will get.
            Entities struck (+1) in a swing more than this, won't get weakened any further.
            """)
    public int reworked_sweeping_extra_target_count = 4;
    @Comment("""
            Determines how weak the attack becomes when striking `reworked_sweeping_extra_target_count + 1` targets.
            Example values:
            - `0.5` -50% damage
            """)
    public float reworked_sweeping_maximum_damage_penalty = 0.5F;
    @Comment("""
            The maximum level Sweeping Edge enchantment applied to the attackers weapon will restore this amount of penalty.
            Example values:
            - `0.5` restores 50% damage penalty when 3 levels are applied, so 16.66% when 1 level is applied
            """)
    public float reworked_sweeping_enchant_restores = 0.5F;
    public boolean reworked_sweeping_plays_sound = true;
    public boolean reworked_sweeping_emits_particles = true;
    public boolean reworked_sweeping_sound_and_particles_only_for_swords = true;
    @Comment("Allows client-side target search to ignore obstacles. WARNING! Setting this to `false` significantly increases the load on clients.")
    public boolean allow_attacking_thru_walls = false;
    @Comment("Applies movement speed multiplier while attacking. (Min: 0, Max: 1). Use `0` for a full stop while attacking. Use `1` for no movement speed penalty")
    public float movement_speed_while_attacking = 0.5F;
    @Comment("Determines if applying the movement speed multiplier while attacking is done smoothly or instantly")
    public boolean movement_speed_applied_smoothly = true;
    @Comment("Determines whether or not to apply movement speed reduction while attacking mounted")
    public boolean movement_speed_effected_while_mounting = false;
    @Comment("Attacks faster than a vanilla sword will do smaller knockback, proportionally.")
    public boolean knockback_reduced_for_fast_attacks = true;
    @Comment("Combo is reset after idling `combo_reset_rate * weapon_cooldown`")
    public float combo_reset_rate = 3F;
    @Comment("Multiplier for `attack_range`, during target lookup on both sides. " +
            "Large sized entities may be colliding with weapon hitbox, but center of entities can have bigger distance than `attack_range`")
    public float target_search_range_multiplier = 2F;
    @Comment("Total multiplier, (examples: +30% = 1.3, -30% = 0.7)")
    public float dual_wielding_attack_speed_multiplier = 1.2F;
    @Comment("Total multiplier, (examples: +30% = 1.3, -30% = 0.7)")
    public float dual_wielding_main_hand_damage_multiplier = 1F;
    @Comment("Total multiplier, (examples: +30% = 1.3, -30% = 0.7)")
    public float dual_wielding_off_hand_damage_multiplier = 1F;

    @Comment("""
            Relations determine when players' undirected weapon swings (cleaves) will hurt another entity (target).
            - `FRIENDLY` - The target can never be damaged by the player.
            - `NEUTRAL` - The target can be damaged only if the player is directly looking at it.
            - `HOSTILE` - The target can be damaged if located within the weapon swing area.         
            (NOTE: Vanilla sweeping can still hit targets, if not disabled via `allow_sweeping`)
            
            The various relation related configs are being checked in the following order:
            - `player_relations`
            - `player_relation_to_passives`
            - `player_relation_to_hostiles`
            - `player_relation_to_other`
            (The first relation to be found for the target will be applied.)
            """)
    public LinkedHashMap<String, TargetHelper.Relation> player_relations = new LinkedHashMap<>() {{
        put("minecraft:player", TargetHelper.Relation.NEUTRAL);
        put("minecraft:villager", TargetHelper.Relation.NEUTRAL);
        put("minecraft:iron_golem", TargetHelper.Relation.NEUTRAL);
        put("guardvillagers:guard", TargetHelper.Relation.NEUTRAL);
    }};

    @Comment("Relation to unspecified entities those are instance of PassiveEntity(Yarn)")
    public TargetHelper.Relation player_relation_to_passives = TargetHelper.Relation.HOSTILE;
    @Comment("Relation to unspecified entities those are instance of HostileEntity(Yarn)")
    public TargetHelper.Relation player_relation_to_hostiles = TargetHelper.Relation.HOSTILE;
    @Comment("Fallback relation")
    public TargetHelper.Relation player_relation_to_other = TargetHelper.Relation.HOSTILE;

    @Comment("Try to guess and apply a preset for items without weapon attributes data file")
    public boolean fallback_compatibility_enabled = true;
    @Comment("Allow printing the content of weapon attributes registry")
    public boolean weapon_registry_logging = false;

    public float getUpswingMultiplier() {
        return Math.max(0.2F, Math.min(1, upswing_multiplier));
    }
}
