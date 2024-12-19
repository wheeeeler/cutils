package net.wheel.cutils.api.event.network;

import net.minecraft.network.Packet;

import net.wheel.cutils.api.event.EventCancellable;

public class EventReceivePacket extends EventCancellable {

    private Packet packet;

    public EventReceivePacket(EventStage stage, Packet packet) {
        super(stage);
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}
