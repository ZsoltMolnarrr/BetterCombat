package net.fabricmc.fabric.api.event;

public abstract class Event<T> {
    public abstract void register(T listener);
}
