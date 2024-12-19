package net.wheel.cutils.api.event.world;

import net.wheel.cutils.api.event.EventCancellable;

public class EventFoliageColor extends EventCancellable {

    private int color;

    public EventFoliageColor() {
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
