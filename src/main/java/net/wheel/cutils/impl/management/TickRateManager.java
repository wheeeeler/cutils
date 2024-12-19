package net.wheel.cutils.impl.management;

import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.MathHelper;

import lombok.Getter;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventReceivePacket;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class TickRateManager {

    private long prevTime;
    private final float[] ticks = new float[20];
    private int currentTick;

    @Getter
    private float lastTick = -1;

    public TickRateManager() {
        this.prevTime = -1;

        for (int i = 0, len = this.ticks.length; i < len; i++) {
            this.ticks[i] = 0.0f;
        }

        crack.INSTANCE.getEventManager().addEventListener(this);
    }

    public float getTickRate() {
        int tickCount = 0;
        float tickRate = 0.0f;

        for (int i = 0; i < this.ticks.length; i++) {
            final float tick = this.ticks[i];

            if (tick > 0.0f) {
                tickRate += tick;
                tickCount++;
            }
        }

        return MathHelper.clamp((tickRate / tickCount), 0.0f, 20.0f);
    }

    public void unload() {
        crack.INSTANCE.getEventManager().removeEventListener(this);
    }

    @Listener
    public void receivePacket(EventReceivePacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof SPacketTimeUpdate) {
                if (this.prevTime != -1) {
                    this.ticks[this.currentTick % this.ticks.length] = MathHelper.clamp(
                            (20.0f / ((float) (System.currentTimeMillis() - this.prevTime) / 1000.0f)), 0.0f, 20.0f);
                    this.lastTick = this.ticks[this.currentTick % this.ticks.length];
                    this.currentTick++;
                }

                this.prevTime = System.currentTimeMillis();
            }
        }
    }

}
