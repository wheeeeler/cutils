package net.wheel.cutils.api.event.player;

import net.minecraft.util.math.BlockPos;

import net.wheel.cutils.api.event.EventCancellable;

public class EventHittingPosition extends EventCancellable {

    private BlockPos blockPos;

    public EventHittingPosition(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }
}
