package net.wheel.cutils.api.event.player;

import net.wheel.cutils.api.event.EventCancellable;

public class EventFovModifier extends EventCancellable {

    private float fov;

    public EventFovModifier() {
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }
}
