package net.wheel.cutils.impl.module.GLOBAL;

import net.minecraft.network.play.server.SPacketChunkData;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventReceivePacket;
import net.wheel.cutils.api.module.Module;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AntiChunkModule extends Module {

    public AntiChunkModule() {
        super("AntiChunkPacket", new String[] { "AntiChunk" }, "Prevents processing of chunk data packets", "NONE", -1,
                ModuleType.GLOBAL);
    }

    @Listener
    public void onReceivePacket(EventReceivePacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof SPacketChunkData) {
                event.setCanceled(true);
            }
        }
    }

}
