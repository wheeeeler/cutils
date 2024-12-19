package net.wheel.cutils.impl.module.CRACK;

import net.wheel.cutils.api.event.player.EventSwingArm;
import net.wheel.cutils.api.module.Module;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class NoSwingModule extends Module {

    public NoSwingModule() {
        super("NoSwing", new String[] { "AntiSwing" }, "no arm swinging server side", "NONE", -1, ModuleType.CRACK);
    }

    @Listener
    public void swingArm(EventSwingArm event) {
        event.setCanceled(true);
    }

}
