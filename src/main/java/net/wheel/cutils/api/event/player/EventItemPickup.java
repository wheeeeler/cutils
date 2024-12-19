package net.wheel.cutils.api.event.player;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;

import net.wheel.cutils.api.event.EventCancellable;

public class EventItemPickup extends EventCancellable {
    private final EntityItem item;
    private final EntityPlayer player;

    public EventItemPickup(EntityItem item, EntityPlayer player) {
        this.item = item;
        this.player = player;
    }

    public EntityItem getItem() {
        return item;
    }

    public EntityPlayer getPlayer() {
        return player;
    }
}
