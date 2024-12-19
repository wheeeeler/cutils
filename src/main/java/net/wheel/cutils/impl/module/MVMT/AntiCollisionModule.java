package net.wheel.cutils.impl.module.MVMT;

import net.wheel.cutils.api.event.player.EventApplyCollision;
import net.wheel.cutils.api.event.player.EventPushOutOfBlocks;
import net.wheel.cutils.api.event.player.EventPushedByWater;
import net.wheel.cutils.api.module.Module;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AntiCollisionModule extends Module {

    public AntiCollisionModule() {
        super("AntiCollision", new String[] { "AntiPush" }, "Disable collision with entities, blocks and water", "NONE",
                -1, ModuleType.MVMT);
    }

    @Listener
    public void pushOutOfBlocks(EventPushOutOfBlocks event) {
        event.setCanceled(true);
    }

    @Listener
    public void pushedByWater(EventPushedByWater event) {
        event.setCanceled(true);
    }

    @Listener
    public void applyCollision(EventApplyCollision event) {
        event.setCanceled(true);
    }

}
