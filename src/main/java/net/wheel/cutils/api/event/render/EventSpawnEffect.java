package net.wheel.cutils.api.event.render;

import net.wheel.cutils.api.event.EventCancellable;

public class EventSpawnEffect extends EventCancellable {

    private int particleID;

    public EventSpawnEffect(int particleID) {
        this.particleID = particleID;
    }

    public int getParticleID() {
        return particleID;
    }

    public void setParticleID(int particleID) {
        this.particleID = particleID;
    }
}
