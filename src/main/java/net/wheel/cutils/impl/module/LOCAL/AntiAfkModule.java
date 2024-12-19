package net.wheel.cutils.impl.module.LOCAL;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventSendPacket;
import net.wheel.cutils.api.event.player.EventUpdateWalkingPlayer;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.task.rotation.RotationTask;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AntiAfkModule extends Module {

    public final Value<Integer> yawOffset = new Value<Integer>("Yaw", new String[] { "yaw", "y" },
            "The yaw to alternate each tick", 1, 0, 180, 1);

    private final RotationTask rotationTask = new RotationTask("NoAFKTask", 1);

    public AntiAfkModule() {
        super("AntiAfk", new String[] { "AntiAFK" }, "Prevents you from being kicked while idle", "NONE", -1,
                ModuleType.LOCAL);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        crack.INSTANCE.getRotationManager().finishTask(this.rotationTask);
    }

    @Listener
    public void onWalkingUpdate(EventUpdateWalkingPlayer event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null)
            return;

        switch (event.getStage()) {
            case PRE:
                float yaw = mc.player.rotationYaw;
                float pitch = mc.player.rotationPitch;
                yaw += (this.yawOffset.getValue() * Math.sin(mc.player.ticksExisted / Math.PI));

                crack.INSTANCE.getRotationManager().startTask(this.rotationTask);
                if (this.rotationTask.isOnline()) {
                    crack.INSTANCE.getRotationManager().setPlayerRotations(yaw, pitch);
                }
                break;
            case POST:
                if (this.rotationTask.isOnline())
                    crack.INSTANCE.getRotationManager().finishTask(this.rotationTask);
                break;
        }
    }

    @Listener
    public void sendPacket(EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof CPacketPlayer.Rotation) {
                if (Minecraft.getMinecraft().player.getRidingEntity() != null) {
                    final CPacketPlayer.Rotation packet = (CPacketPlayer.Rotation) event.getPacket();
                    packet.yaw += (this.yawOffset.getValue()
                            * Math.sin(Minecraft.getMinecraft().player.ticksExisted / Math.PI));
                }
            }
        }
    }

}
