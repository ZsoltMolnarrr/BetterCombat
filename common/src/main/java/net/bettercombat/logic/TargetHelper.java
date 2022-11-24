package net.bettercombat.logic;

import net.bettercombat.BetterCombat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;

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

        var config = BetterCombat.config;
        var casterTeam = attacker.getScoreboardTeam();
        var targetTeam = target.getScoreboardTeam();
        if (casterTeam == null || targetTeam == null) {
            if (target instanceof PlayerEntity) {
                return Relation.coalesce(config.player_relation_to_teamless_players, Relation.NEUTRAL);
            }
            if (target instanceof VillagerEntity) {
                return Relation.coalesce(config.player_relation_to_villagers, Relation.NEUTRAL);
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
}
