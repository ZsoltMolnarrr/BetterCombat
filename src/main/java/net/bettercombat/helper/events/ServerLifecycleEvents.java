package net.bettercombat.helper.events;

import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ServerLifecycleEvents {
    public static List<Consumer<MinecraftServer>> onServerStarted = new ArrayList<>();
    public static final ServerEvent SERVER_STARTED = new ServerEvent();

    public static class ServerEvent {
        public void register(Consumer<MinecraftServer> listener){
            onServerStarted.add(listener);
        }
    }

}
