package net.wheel.cutils.api.event.world;

import net.minecraft.entity.Entity;

public class EventRemoveEntity {

    private Entity entity;

    public EventRemoveEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
