package net.wheel.cutils.api.event.world;

import net.minecraft.client.multiplayer.WorldClient;

import net.wheel.cutils.api.event.EventCancellable;

public class EventLoadWorld extends EventCancellable {

    private final WorldClient world;

    public EventLoadWorld(WorldClient world) {
        this.world = world;
    }

    public WorldClient getWorld() {
        return world;
    }
}
