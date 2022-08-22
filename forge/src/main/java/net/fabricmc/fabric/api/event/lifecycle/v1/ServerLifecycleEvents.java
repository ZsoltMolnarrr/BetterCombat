package net.fabricmc.fabric.api.event.lifecycle.v1;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

public class ServerLifecycleEvents {
    public static List<ServerStarted> onServerStarted = new ArrayList<>();
    public static final Event<ServerStarted> SERVER_STARTED = new ServerEvent();

    public static class ServerEvent extends Event<ServerStarted>{
        public void register(ServerStarted listener){
            onServerStarted.add(listener);
        }
    }

    public interface ServerStarted {
        void onServerStarted(MinecraftServer server);
    }
}
