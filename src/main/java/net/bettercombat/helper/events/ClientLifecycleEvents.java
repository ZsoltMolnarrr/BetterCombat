package net.bettercombat.helper.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ClientLifecycleEvents {
    public static List<Consumer<MinecraftClient>> onClientStarted = new ArrayList<>();
    public static final ClientEvent CLIENT_STARTED = new ClientEvent();

    public static class ClientEvent {
        public void register(Consumer<MinecraftClient> listener){
            onClientStarted.add(listener);
        }
    }
}
