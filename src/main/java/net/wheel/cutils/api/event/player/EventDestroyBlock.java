package net.wheel.cutils.api.event.player;

import net.minecraft.util.math.BlockPos;

import net.wheel.cutils.api.event.EventCancellable;

public class EventDestroyBlock extends EventCancellable {

    private BlockPos pos;

    public EventDestroyBlock(BlockPos pos) {
        this.pos = pos;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }
}
