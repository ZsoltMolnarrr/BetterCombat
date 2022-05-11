package net.bettercombat.client;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;
import net.minecraft.client.util.InputUtil;

@ConfigEntries
public class ClientConfig implements ConfigContainer {
    public boolean isHoldToAttackEnabled = true;
    public boolean isMiningWithWeaponsEnabled = true;
    public boolean isSwingThruGrassEnabled = true;
    public InputUtil.Key feintKey = InputUtil.fromKeyCode(InputUtil.GLFW_KEY_T, 0);
}
