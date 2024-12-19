package net.wheel.cutils.api.event.render;

import net.minecraft.entity.EntityLivingBase;

import net.wheel.cutils.api.event.EventCancellable;

public class EventRenderName extends EventCancellable {

    private EntityLivingBase entity;

    public EventRenderName(EntityLivingBase entity) {
        this.entity = entity;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }

    public void setEntity(EntityLivingBase entity) {
        this.entity = entity;
    }
}
