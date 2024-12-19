package net.wheel.cutils.api.event.player;

import net.wheel.cutils.api.event.EventCancellable;

public class EventSendChatMessage extends EventCancellable {

    private String message;

    public EventSendChatMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
