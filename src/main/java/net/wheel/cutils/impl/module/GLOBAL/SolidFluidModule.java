package net.wheel.cutils.impl.module.GLOBAL;

import net.wheel.cutils.api.event.world.EventCanCollide;
import net.wheel.cutils.api.module.Module;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class SolidFluidModule extends Module {

    public SolidFluidModule() {
        super("SolidFluids", new String[] { "LiquidInt", "LiqInt" }, "Allows you to interact with liquids", "NONE", -1,
                ModuleType.GLOBAL);
    }

    @Listener
    public void canCollide(EventCanCollide event) {
        event.setCanceled(true);
    }

}
