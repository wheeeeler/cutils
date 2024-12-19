package net.wheel.cutils.api.event.render;

import net.wheel.cutils.api.event.EventCancellable;

public class EventRenderOverlay extends EventCancellable {

    private OverlayType type;

    public EventRenderOverlay(OverlayType type) {
        this.type = type;
    }

    public OverlayType getType() {
        return type;
    }

    public void setType(OverlayType type) {
        this.type = type;
    }

    public enum OverlayType {
        BLOCK,
        LIQUID,
        FIRE
    }

}
