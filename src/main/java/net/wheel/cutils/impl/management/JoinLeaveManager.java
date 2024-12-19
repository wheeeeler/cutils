package net.wheel.cutils.impl.management;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketPlayerListItem;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventReceivePacket;
import net.wheel.cutils.api.event.player.EventPlayerJoin;
import net.wheel.cutils.api.event.player.EventPlayerLeave;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class JoinLeaveManager {

    public JoinLeaveManager() {
        crack.INSTANCE.getEventManager().addEventListener(this);
    }

    @Listener
    public void receivePacket(EventReceivePacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof SPacketPlayerListItem) {
                final SPacketPlayerListItem packet = (SPacketPlayerListItem) event.getPacket();
                final Minecraft mc = Minecraft.getMinecraft();
                if (mc.player != null && mc.player.ticksExisted >= 1000) {
                    if (packet.getAction() == SPacketPlayerListItem.Action.ADD_PLAYER) {
                        for (SPacketPlayerListItem.AddPlayerData playerData : packet.getEntries()) {
                            if (playerData.getProfile().getId() != mc.session.getProfile().getId()) {
                                new Thread(() -> {
                                    final String name = crack.INSTANCE.getApiManager()
                                            .resolveName(playerData.getProfile().getId().toString());
                                    if (name != null) {
                                        crack.INSTANCE.getEventManager().dispatchEvent(
                                                new EventPlayerJoin(name, playerData.getProfile().getId().toString()));
                                    }
                                }).start();
                            }
                        }
                    }
                    if (packet.getAction() == SPacketPlayerListItem.Action.REMOVE_PLAYER) {
                        for (SPacketPlayerListItem.AddPlayerData playerData : packet.getEntries()) {
                            if (playerData.getProfile().getId() != mc.session.getProfile().getId()) {
                                new Thread(() -> {
                                    final String name = crack.INSTANCE.getApiManager()
                                            .resolveName(playerData.getProfile().getId().toString());
                                    if (name != null) {
                                        crack.INSTANCE.getEventManager().dispatchEvent(
                                                new EventPlayerLeave(name, playerData.getProfile().getId().toString()));
                                    }
                                }).start();
                            }
                        }
                    }
                }
            }
        }
    }

    public void unload() {
        crack.INSTANCE.getEventManager().removeEventListener(this);
    }
}
