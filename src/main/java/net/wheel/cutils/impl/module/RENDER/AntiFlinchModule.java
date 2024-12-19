package net.wheel.cutils.impl.module.RENDER;

import net.wheel.cutils.api.event.render.EventHurtCamEffect;
import net.wheel.cutils.api.module.Module;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AntiFlinchModule extends Module {

    public AntiFlinchModule() {
        super("AntiFlinch", new String[] { "AntiHurtCam" }, "Removes hurt camera effects", "NONE", -1,
                ModuleType.RENDER);
    }

    @Listener
    public void hurtCamEffect(EventHurtCamEffect event) {
        event.setCanceled(true);
    }

}
