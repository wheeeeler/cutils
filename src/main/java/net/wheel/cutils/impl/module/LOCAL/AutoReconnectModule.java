package net.wheel.cutils.impl.module.LOCAL;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.handshake.client.C00Handshake;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.minecraft.EventDisplayGui;
import net.wheel.cutils.api.event.minecraft.EventRunTick;
import net.wheel.cutils.api.event.network.EventSendPacket;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.util.Timer;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AutoReconnectModule extends Module {

    public final Value<Float> delay = new Value<Float>("Delay", new String[] { "Del" },
            "Delay in seconds between reconnect attempts", 160.0f, 0.1f, 500.0f, 0.5f);
    private String lastIp;
    private int lastPort;
    private boolean reconnect;
    private final Timer timer = new Timer();

    public AutoReconnectModule() {
        super("AutoReconnect", new String[] { "Rejoin", "Recon", "AutoReconnect" },
                "Automatically reconnects to the last server after being kicked", "NONE", -1, ModuleType.LOCAL);
    }

    @Listener
    public void sendPacket(EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof C00Handshake) {
                final C00Handshake packet = (C00Handshake) event.getPacket();
                if (packet.getRequestedState() == EnumConnectionState.LOGIN) {
                    this.lastIp = packet.ip;
                    this.lastPort = packet.port;
                }
            }
        }
    }

    @Listener
    public void runTick(EventRunTick event) {
        if (event.getStage() == EventStageable.EventStage.POST) {
            if (this.lastIp != null && this.lastPort > 0 && this.reconnect) {
                if (this.timer.passed(this.delay.getValue() * 1000)) {
                    Minecraft.getMinecraft().displayGuiScreen(
                            new GuiConnecting(null, Minecraft.getMinecraft(), this.lastIp, this.lastPort));
                    this.timer.reset();
                    this.reconnect = false;
                }
            }
        }
    }

    @Listener
    public void displayGui(EventDisplayGui event) {
        if (event.getScreen() != null) {
            if (event.getScreen() instanceof GuiDisconnected) {
                this.reconnect = true;
            }
        }
    }
}
