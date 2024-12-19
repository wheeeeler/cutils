package net.wheel.cutils.api.event.player;

import net.wheel.cutils.api.event.EventCancellable;

public class EventPlayerReach extends EventCancellable {

    private float reach;

    public float getReach() {
        return reach;
    }

    public void setReach(float reach) {
        this.reach = reach;
    }
}
