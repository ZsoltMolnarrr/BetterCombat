package net.bettercombat.compat;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.client.KnownClientPlayer;
import net.bettercombat.Platform;
import net.bettercombat.logic.TargetHelper;
import net.minecraft.world.entity.player.Player;
import java.util.Optional;

public class FTBTeamsCompat {
    public static void init() {
        if (Platform.isModLoaded("ftbteams")) {
            TargetHelper.registerTeamMatcher("ftb", (attack, target) -> {
                if (attack instanceof Player attackerPlayer && target instanceof Player targetPlayer) {
                    if (attackerPlayer.level().isClientSide()) {
                        return checkClientTeamRelation(attackerPlayer, targetPlayer);
                    } else {
                        return checkServerTeamRelation(attackerPlayer, targetPlayer);
                    }
                }
                return null;
            });
        }
    }

    private static TargetHelper.TeamRelation checkClientTeamRelation(Player attackerPlayer, Player targetPlayer) {
        if (!FTBTeamsAPI.api().isClientManagerLoaded()) {
            return null;
        }
        var manager = FTBTeamsAPI.api().getClientManager();

        Optional<KnownClientPlayer> attackerKnownPlayerOpt = manager.getKnownPlayer(attackerPlayer.getUUID());
        if (attackerKnownPlayerOpt.isEmpty()) {
            return null;
        }

        Optional<KnownClientPlayer> targetKnownPlayerOpt = manager.getKnownPlayer(targetPlayer.getUUID());
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
            boolean attackerSeesAlly = attackerTeamOpt.get().getRankForPlayer(targetPlayer.getUUID()).isAllyOrBetter();
            boolean targetSeesAlly = targetTeamOpt.get().getRankForPlayer(attackerPlayer.getUUID()).isAllyOrBetter();

            if (attackerSeesAlly && targetSeesAlly) {
                return new TargetHelper.TeamRelation(true, false);
            }
        }

        return null;
    }

    private static TargetHelper.TeamRelation checkServerTeamRelation(Player attackerPlayer, Player targetPlayer) {
        if (!FTBTeamsAPI.api().isManagerLoaded()) {
            return null;
        }
        var manager = FTBTeamsAPI.api().getManager();

        Optional<Team> attackerTeamOpt = manager.getTeamForPlayerID(attackerPlayer.getUUID());
        Optional<Team> targetTeamOpt = manager.getTeamForPlayerID(targetPlayer.getUUID());

        if (attackerTeamOpt.isPresent() && targetTeamOpt.isPresent()) {
            Team attackerTeam = attackerTeamOpt.get();
            Team targetTeam = targetTeamOpt.get();

            // 1. Check if players are in the same effective team (party).
            if (attackerTeam.getTeamId().equals(targetTeam.getTeamId())) {
                return new TargetHelper.TeamRelation(true, false);
            }

            // 2. Check for a mutual, two-way alliance to prevent abuse.
            boolean attackerSeesAlly = attackerTeam.getRankForPlayer(targetPlayer.getUUID()).isAllyOrBetter();
            boolean targetSeesAlly = targetTeam.getRankForPlayer(attackerPlayer.getUUID()).isAllyOrBetter();

            if (attackerSeesAlly && targetSeesAlly) {
                return new TargetHelper.TeamRelation(true, false);
            }
        }

        return null;
    }
}
