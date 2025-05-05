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
                    boolean managerAvailable = attackerPlayer.getWorld().isClient ?
                            FTBTeamsAPI.api().isClientManagerLoaded() :
                            FTBTeamsAPI.api().isManagerLoaded();
                    if (managerAvailable) {
                        TeamManager manager = FTBTeamsAPI.api().getManager();
                        if (manager.arePlayersInSameTeam(attackerPlayer.getUuid(), targetPlayer.getUuid())) {
                            var friendlyFire = false;
                            return new TargetHelper.TeamRelation(true, friendlyFire);
                        }
                    }
                }
                return null;
            });
        }
    }
}
