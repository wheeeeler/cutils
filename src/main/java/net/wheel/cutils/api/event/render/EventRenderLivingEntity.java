package net.wheel.cutils.api.event.render;

import net.minecraft.entity.EntityLivingBase;

import net.wheel.cutils.api.event.EventCancellable;

public class EventRenderLivingEntity extends EventCancellable {

    private EntityLivingBase entity;

    public EventRenderLivingEntity(EventStage stage, EntityLivingBase entity) {
        super(stage);
        this.entity = entity;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }

    public void setEntity(EntityLivingBase entity) {
        this.entity = entity;
    }
}
