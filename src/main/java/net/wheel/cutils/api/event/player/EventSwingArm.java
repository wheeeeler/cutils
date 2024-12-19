package net.wheel.cutils.api.event.player;

import net.minecraft.util.EnumHand;

import net.wheel.cutils.api.event.EventCancellable;

public class EventSwingArm extends EventCancellable {

    private EnumHand hand;

    public EventSwingArm(EnumHand hand) {
        this.hand = hand;
    }

    public EnumHand getHand() {
        return hand;
    }

    public void setHand(EnumHand hand) {
        this.hand = hand;
    }
}
