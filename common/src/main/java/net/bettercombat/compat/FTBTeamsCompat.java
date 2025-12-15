package net.bettercombat.compat;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
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
        Optional<KnownClientPlayer> targetKnownPlayerOpt = manager.getKnownPlayer(targetPlayer.getUuid());

        if (attackerKnownPlayerOpt.isEmpty() || targetKnownPlayerOpt.isEmpty()) {
            return null;
        }

        KnownClientPlayer attackerKnownPlayer = attackerKnownPlayerOpt.get();
        KnownClientPlayer targetKnownPlayer = targetKnownPlayerOpt.get();

        if (attackerKnownPlayer.teamId().equals(targetKnownPlayer.teamId())) {
            return new TargetHelper.TeamRelation(true, false);
        }

        return null;
    }

    private static TargetHelper.TeamRelation checkServerTeamRelation(PlayerEntity attackerPlayer, PlayerEntity targetPlayer) {
        if (!FTBTeamsAPI.api().isManagerLoaded()) {
            return null;
        }
        var manager = FTBTeamsAPI.api().getManager();

        if (manager.arePlayersInSameTeam(attackerPlayer.getUuid(), targetPlayer.getUuid())) {
            return new TargetHelper.TeamRelation(true, false);
        }

        return null;
    }
}
