package net.wheel.cutils.api.event.gui.hud;

import net.wheel.cutils.api.value.Value;

public class EventUIValueChanged {

    private Value value;

    public EventUIValueChanged(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }
}
