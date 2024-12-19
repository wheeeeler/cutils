package net.wheel.cutils.api.event.world;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import net.wheel.cutils.api.event.EventCancellable;

public class EventAddCollisionBox extends EventCancellable {

    private BlockPos pos;
    private Entity entity;

    public EventAddCollisionBox(BlockPos pos, Entity entity) {
        this.pos = pos;
        this.entity = entity;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
