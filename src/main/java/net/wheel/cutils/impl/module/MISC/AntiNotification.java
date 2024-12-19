package net.wheel.cutils.impl.module.MISC;

import net.wheel.cutils.api.event.render.EventDrawToast;
import net.wheel.cutils.api.module.Module;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AntiNotification extends Module {

    public AntiNotification() {
        super("AntiNotification", new String[] { "Toast" }, "Toast. (Achievements, etc.)", "NONE", -1, ModuleType.MISC);
    }

    @Listener
    public void onToast(EventDrawToast event) {
        event.setCanceled(true);
    }
}
