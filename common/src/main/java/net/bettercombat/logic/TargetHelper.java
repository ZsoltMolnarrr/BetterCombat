package net.bettercombat.logic;

import net.bettercombat.BetterCombat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;

import java.util.Arrays;

public class TargetHelper {
    public enum Relation {
        FRIENDLY, NEUTRAL, HOSTILE;

        public static Relation coalesce(Relation value, Relation fallback) {
            if (value != null) {
                return value;
            }
            return fallback;
        }
    }

    public static Relation getRelation(PlayerEntity attacker, Entity target) {
        if (attacker == target) {
            return Relation.FRIENDLY;
        }
        if (target instanceof Tameable tameable) {
            var owner = tameable.getOwner();
            if (owner != null) {
                return getRelation(attacker, owner);
            }
        }
        if (target instanceof AbstractDecorationEntity) {
            return Relation.NEUTRAL;
        }
        var config = BetterCombat.config;
        var casterTeam = attacker.getScoreboardTeam();
        var targetTeam = target.getScoreboardTeam();
        if (casterTeam == null || targetTeam == null) {
            var id = Registries.ENTITY_TYPE.getId(target.getType());
            var mappedRelation = config.player_relations.get(id.toString());
            if (mappedRelation != null) {
                return mappedRelation;
            }
            if (target instanceof PassiveEntity) {
                return Relation.coalesce(config.player_relation_to_passives, Relation.HOSTILE);
            }
            if (target instanceof HostileEntity) {
                return Relation.coalesce(config.player_relation_to_hostiles, Relation.HOSTILE);
            }
            return Relation.coalesce(config.player_relation_to_other, Relation.HOSTILE);
        } else {
            return attacker.isTeammate(target) ? Relation.FRIENDLY : Relation.HOSTILE;
        }
    }

    public static boolean isAttackableMount(Entity entity) {
        if (entity instanceof HostileEntity || isEntityHostileVehicle(entity.getName().getString())) {
            return true;
        }
        return BetterCombat.config.allow_attacking_mount;
    }

    public static boolean isEntityHostileVehicle(String entityName) {
        // An entity is a hostile vehicle via blacklist specifically
        var config = BetterCombat.config;
        return config.hostile_player_vehicles != null
                && config.hostile_player_vehicles.length > 0
                && Arrays.asList(config.hostile_player_vehicles).contains(entityName);
    }
}
