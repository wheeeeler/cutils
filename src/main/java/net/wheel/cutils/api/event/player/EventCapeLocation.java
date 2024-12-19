package net.wheel.cutils.api.event.player;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;

import net.wheel.cutils.api.event.EventCancellable;

public class EventCapeLocation extends EventCancellable {

    private AbstractClientPlayer player;
    private ResourceLocation location;

    public EventCapeLocation(AbstractClientPlayer player) {
        this.player = player;
    }

    public AbstractClientPlayer getPlayer() {
        return player;
    }

    public void setPlayer(AbstractClientPlayer player) {
        this.player = player;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public void setLocation(ResourceLocation location) {
        this.location = location;
    }
}
