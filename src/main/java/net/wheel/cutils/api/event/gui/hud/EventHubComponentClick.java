package net.wheel.cutils.api.event.gui.hud;

public class EventHubComponentClick {

    public String hubComponentName;
    public boolean hubComponentVisible;

    public EventHubComponentClick(String hubComponentName, boolean hubComponentVisible) {
        this.hubComponentName = hubComponentName;
        this.hubComponentVisible = hubComponentVisible;
    }
}
