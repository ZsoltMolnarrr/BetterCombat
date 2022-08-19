package net.fabricmc.fabric.api.client.event;

public abstract class Event<T> {
    public abstract void register(T listener);
}
