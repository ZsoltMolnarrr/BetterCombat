package net.bettercombat.logic;

import net.bettercombat.BetterCombatMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TargetHelper {
    public enum Relation {
        FRIENDLY,
        NEUTRAL,
        HOSTILE;

        public static Relation coalesce(Relation value, Relation fallback) {
            if (value != null) {
                return value;
            }
            return fallback;
        }
    }

    public static Relation getRelation(Player attacker, Entity target) {
        var config = BetterCombatMod.config;
        if (attacker == target) {
            return config.player_relation_to_self_and_pets; // Relation.NEUTRAL by default, to allow direct hits on pets
        }
        if (target instanceof OwnableEntity tameable) {
            var owner = tameable.getOwner();
            if (owner != null) {
                return getRelation(attacker, owner);
            }
        }
        if (target instanceof HangingEntity) {
            return Relation.NEUTRAL;
        }

        for (var matcher: TEAM_MATCHERS.values()) {
            var relation = matcher.getRelation(attacker, target);
            if (relation != null) {
                return relation.areTeammates()
                        ? (relation.friendlyFireAllowed() ? config.player_relation_to_teammates : Relation.FRIENDLY)  // FRIENDLY for friendly fire off, to protect team mate pets
                        : Relation.HOSTILE;
            }
        }

        var targetTypeEntry = BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(target.getType());
        var id = targetTypeEntry.unwrapKey().get().identifier();
        var mappedRelation = config.player_relations.get(id.toString());
        if (mappedRelation != null) {
            return mappedRelation;
        }
        for (var entry: getRelationTagsCache().entrySet()) {
            if (targetTypeEntry.is(entry.getKey())) {
                return entry.getValue();
            }
        }
        if (target instanceof AgeableMob) {
            return Relation.coalesce(config.player_relation_to_passives, Relation.HOSTILE);
        }
        if (target instanceof Monster) {
            return Relation.coalesce(config.player_relation_to_hostiles, Relation.HOSTILE);
        }
        return Relation.coalesce(config.player_relation_to_other, Relation.HOSTILE);
    }
    private static Map<TagKey<EntityType<?>>, Relation> RELATION_TAG_CACHE = null;
    private static Map<TagKey<EntityType<?>>, Relation> getRelationTagsCache() {
        if (RELATION_TAG_CACHE == null) {
            RELATION_TAG_CACHE = new HashMap<>();
            for (var entrySet: BetterCombatMod.config.player_relation_tags.entrySet()) {
                var tagString = entrySet.getKey();
                var relation = entrySet.getValue();
                var tag = TagKey.create(Registries.ENTITY_TYPE, Identifier.parse(tagString));
                RELATION_TAG_CACHE.put(tag, relation);
            }
        }
        return RELATION_TAG_CACHE;
    }

    public record TeamRelation(boolean areTeammates, boolean friendlyFireAllowed) { }
    public interface TeamMatcher { @Nullable TeamRelation getRelation(Entity attacker, Entity target); }
    private static final Map<String, TeamMatcher> TEAM_MATCHERS = new LinkedHashMap<>();
    public static void registerTeamMatcher(String name, TeamMatcher matcher) {
        TEAM_MATCHERS.put(name, matcher);
    }
    static {
        registerTeamMatcher("vanilla", (entity1, entity2) -> {
            var team1 = entity1.getTeam();
            var team2 = entity2.getTeam();
            if (team1 == null || team2 == null) {
                return null;
            }
            var friendlyFire = team1.isAllowFriendlyFire();
            return new TeamRelation(entity1.isAlliedTo(entity2), friendlyFire);
        });
    }

    public static boolean isAttackableMount(Entity entity) {
        if (entity instanceof Monster || isEntityHostileVehicle(entity.getName().getString())) {
            return true;
        }
        return BetterCombatMod.config.allow_attacking_mount;
    }

    public static boolean isEntityHostileVehicle(String entityName) {
        // An entity is a hostile vehicle via blacklist specifically
        var config = BetterCombatMod.config;
        return config.hostile_player_vehicles != null
                && config.hostile_player_vehicles.length > 0
                && Arrays.asList(config.hostile_player_vehicles).contains(entityName);
    }

    public static boolean isHitAllowed(boolean isDirect, Relation relation) {
        if (isDirect) {
            // Direct hit
            return relation != Relation.FRIENDLY;
        } else {
            // Sweeping hit
            return relation == Relation.HOSTILE;
        }
    }
}