package net.wheel.cutils.api.event.world;

import net.wheel.cutils.api.event.EventCancellable;

public class EventGrassColor extends EventCancellable {

    private int color;

    public EventGrassColor() {
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
