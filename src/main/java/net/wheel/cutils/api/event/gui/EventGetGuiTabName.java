package net.wheel.cutils.api.event.gui;

public class EventGetGuiTabName {

    private String name;

    public EventGetGuiTabName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
