package net.wheel.cutils.impl.module.GLOBAL;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventReceivePacket;
import net.wheel.cutils.api.module.Module;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AntiRotationModule extends Module {

    public AntiRotationModule() {
        super("AntiRotation", new String[] { "NoRot", "AntiRotate" }, "Prevents you from processing server rotations",
                "NONE", -1, ModuleType.GLOBAL);
    }

    @Listener
    public void receivePacket(EventReceivePacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof SPacketPlayerPosLook) {
                final SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
                if (Minecraft.getMinecraft().player != null) {
                    packet.yaw = Minecraft.getMinecraft().player.rotationYaw;
                    packet.pitch = Minecraft.getMinecraft().player.rotationPitch;
                }
            }
        }
    }

}
