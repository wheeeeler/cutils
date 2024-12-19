package net.wheel.cutils.impl.module.RENDER;

import net.wheel.cutils.api.event.render.EventOrientCamera;
import net.wheel.cutils.api.module.Module;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class ViewClipModule extends Module {

    public ViewClipModule() {
        super("ViewClip", new String[] { "ViewC" }, "Prevents the third person camera from ray-tracing", "NONE", -1,
                ModuleType.RENDER);
    }

    @Listener
    public void orientCamera(EventOrientCamera event) {
        event.setCanceled(true);
    }

}
