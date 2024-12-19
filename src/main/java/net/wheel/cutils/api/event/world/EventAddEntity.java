package net.wheel.cutils.api.event.world;

import net.minecraft.entity.Entity;

import net.wheel.cutils.api.event.EventCancellable;

public class EventAddEntity extends EventCancellable {

    private Entity entity;

    public EventAddEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
