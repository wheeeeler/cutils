package net.wheel.cutils.api.event.minecraft;

import net.minecraft.client.gui.GuiScreen;

import net.wheel.cutils.api.event.EventCancellable;

public class EventDisplayGui extends EventCancellable {

    private GuiScreen screen;

    public EventDisplayGui(GuiScreen screen) {
        this.screen = screen;
    }

    public GuiScreen getScreen() {
        return screen;
    }

    public void setScreen(GuiScreen screen) {
        this.screen = screen;
    }
}
