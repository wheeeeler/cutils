package net.wheel.cutils.api.event.minecraft;

public class EventKeyPress {

    private int key;

    public EventKeyPress(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
}
