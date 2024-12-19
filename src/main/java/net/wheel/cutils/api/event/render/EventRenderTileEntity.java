package net.wheel.cutils.api.event.render;

import net.minecraft.tileentity.TileEntity;

import net.wheel.cutils.api.event.EventCancellable;

public class EventRenderTileEntity extends EventCancellable {
    private final TileEntity tileEntity;

    public EventRenderTileEntity(TileEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    public TileEntity getTileEntity() {
        return tileEntity;
    }
}
