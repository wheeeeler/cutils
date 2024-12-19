package net.wheel.cutils.api.event.module;

import net.wheel.cutils.api.module.Module;

public class EventModuleLoad {

    private Module mod;

    public EventModuleLoad(Module mod) {
        this.mod = mod;
    }

    public Module getMod() {
        return mod;
    }

    public void setMod(Module mod) {
        this.mod = mod;
    }
}
