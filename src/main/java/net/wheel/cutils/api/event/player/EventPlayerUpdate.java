package net.wheel.cutils.api.event.player;

import net.wheel.cutils.api.event.EventCancellable;

public class EventPlayerUpdate extends EventCancellable {

    public EventPlayerUpdate(EventStage stage) {
        super(stage);
    }

}
