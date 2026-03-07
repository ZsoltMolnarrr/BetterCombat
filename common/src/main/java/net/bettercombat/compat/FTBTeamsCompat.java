package net.bettercombat.compat;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.client.KnownClientPlayer;
import net.bettercombat.Platform;
import net.bettercombat.logic.TargetHelper;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

public class FTBTeamsCompat {
    public static void init() {
        if (Platform.isModLoaded("ftbteams")) {
            TargetHelper.registerTeamMatcher("ftb", (attack, target) -> {
                if (attack instanceof PlayerEntity attackerPlayer && target instanceof PlayerEntity targetPlayer) {
                    if (attackerPlayer.getEntityWorld().isClient()) {
                        return checkClientTeamRelation(attackerPlayer, targetPlayer);
                    } else {
                        return checkServerTeamRelation(attackerPlayer, targetPlayer);
                    }
                }
                return null;
            });
        }
    }

    private static TargetHelper.TeamRelation checkClientTeamRelation(PlayerEntity attackerPlayer, PlayerEntity targetPlayer) {
        if (!FTBTeamsAPI.api().isClientManagerLoaded()) {
            return null;
        }
        var manager = FTBTeamsAPI.api().getClientManager();

        Optional<KnownClientPlayer> attackerKnownPlayerOpt = manager.getKnownPlayer(attackerPlayer.getUuid());
        if (attackerKnownPlayerOpt.isEmpty()) {
            return null;
        }

        Optional<KnownClientPlayer> targetKnownPlayerOpt = manager.getKnownPlayer(targetPlayer.getUuid());
        if (targetKnownPlayerOpt.isEmpty()) {
            return null;
        }

        KnownClientPlayer attackerKnownPlayer = attackerKnownPlayerOpt.get();
        KnownClientPlayer targetKnownPlayer = targetKnownPlayerOpt.get();

        // 1. Check if players are in the same effective team (party).
        if (attackerKnownPlayer.teamId().equals(targetKnownPlayer.teamId())) {
            return new TargetHelper.TeamRelation(true, false);
        }

        // 2. Check for a mutual, two-way alliance to prevent abuse.
        Optional<Team> attackerTeamOpt = manager.getTeamByID(attackerKnownPlayer.teamId());
        Optional<Team> targetTeamOpt = manager.getTeamByID(targetKnownPlayer.teamId());

        if (attackerTeamOpt.isPresent() && targetTeamOpt.isPresent()) {
            boolean attackerSeesAlly = attackerTeamOpt.get().getRankForPlayer(targetPlayer.getUuid()).isAllyOrBetter();
            boolean targetSeesAlly = targetTeamOpt.get().getRankForPlayer(attackerPlayer.getUuid()).isAllyOrBetter();

            if (attackerSeesAlly && targetSeesAlly) {
                return new TargetHelper.TeamRelation(true, false);
            }
        }

        return null;
    }

    private static TargetHelper.TeamRelation checkServerTeamRelation(PlayerEntity attackerPlayer, PlayerEntity targetPlayer) {
        if (!FTBTeamsAPI.api().isManagerLoaded()) {
            return null;
        }
        var manager = FTBTeamsAPI.api().getManager();

        Optional<Team> attackerTeamOpt = manager.getTeamForPlayerID(attackerPlayer.getUuid());
        Optional<Team> targetTeamOpt = manager.getTeamForPlayerID(targetPlayer.getUuid());

        if (attackerTeamOpt.isPresent() && targetTeamOpt.isPresent()) {
            Team attackerTeam = attackerTeamOpt.get();
            Team targetTeam = targetTeamOpt.get();

            // 1. Check if players are in the same effective team (party).
            if (attackerTeam.getTeamId().equals(targetTeam.getTeamId())) {
                return new TargetHelper.TeamRelation(true, false);
            }

            // 2. Check for a mutual, two-way alliance to prevent abuse.
            boolean attackerSeesAlly = attackerTeam.getRankForPlayer(targetPlayer.getUuid()).isAllyOrBetter();
            boolean targetSeesAlly = targetTeam.getRankForPlayer(attackerPlayer.getUuid()).isAllyOrBetter();

            if (attackerSeesAlly && targetSeesAlly) {
                return new TargetHelper.TeamRelation(true, false);
            }
        }

        return null;
    }
}
