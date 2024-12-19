package net.wheel.cutils.impl.module.WAR;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventSendPacket;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class CriticalsModule extends Module {

    public final Value<Mode> mode = new Value("Mode", new String[] { "Mode", "M" }, "c", Mode.PACKET);

    public CriticalsModule() {
        super("Criticals", new String[] { "Crits" }, "cr1t", "NONE", -1, ModuleType.WAR);
    }

    @Override
    public String getMetaData() {
        return this.mode.getValue().name();
    }

    @Listener
    public void sendPacket(EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof CPacketUseEntity) {
                final CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
                if (packet.getAction() == CPacketUseEntity.Action.ATTACK) {
                    final Minecraft mc = Minecraft.getMinecraft();

                    if (mc.player.onGround && !mc.gameSettings.keyBindJump.isKeyDown()
                            && packet.getEntityFromWorld(mc.world) instanceof EntityLivingBase) {
                        switch (this.mode.getValue()) {
                            case JUMP:
                                mc.player.jump();
                                break;
                            case PACKET:
                                sendCriticalPackets(mc);
                                break;
                        }
                    }
                }
            }
        }
    }

    private void sendCriticalPackets(Minecraft mc) {
        mc.player.connection
                .sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0625, mc.player.posZ, false));
        mc.player.connection
                .sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
        mc.player.connection
                .sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.1E-5, mc.player.posZ, false));
        mc.player.connection
                .sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));

        mc.player.connection
                .sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0625, mc.player.posZ, false));
        mc.player.connection
                .sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
    }

    private enum Mode {
        JUMP, PACKET
    }
}
