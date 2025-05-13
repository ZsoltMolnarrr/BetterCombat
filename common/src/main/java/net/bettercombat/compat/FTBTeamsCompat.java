package net.bettercombat.compat;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.TeamManager;
import net.bettercombat.logic.TargetHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;

public class FTBTeamsCompat {
    public static void init() {
        if (FabricLoader.getInstance().isModLoaded("ftbteams")) {
            TargetHelper.registerTeamMatcher("ftb", (attack, target) -> {
                if (attack instanceof PlayerEntity attackerPlayer && target instanceof PlayerEntity targetPlayer) {
                    if (attackerPlayer.getWorld().isClient) {
//                        var managerAvailable = FTBTeamsAPI.api().isClientManagerLoaded();
//                        if (managerAvailable) {
//                            var manager = FTBTeamsAPI.api().getClientManager();
//                            if (manager.arePlayersInSameTeam(attackerPlayer.getUuid(), targetPlayer.getUuid())) {
//                                var friendlyFire = false;
//                                return new EntityRelations.TeamRelation(true, friendlyFire);
//                            }
//                        }
                    } else {
                        var managerAvailable = FTBTeamsAPI.api().isManagerLoaded();
                        if (managerAvailable) {
                            var manager = FTBTeamsAPI.api().getManager();
                            if (manager.arePlayersInSameTeam(attackerPlayer.getUuid(), targetPlayer.getUuid())) {
                                var friendlyFire = false;
                                return new TargetHelper.TeamRelation(true, friendlyFire);
                            }
                        }
                    }
                }
                return null;
            });
        }
    }
}
