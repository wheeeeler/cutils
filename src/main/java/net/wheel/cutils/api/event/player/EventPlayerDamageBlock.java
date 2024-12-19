package net.wheel.cutils.api.event.player;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import net.wheel.cutils.api.event.EventCancellable;

public class EventPlayerDamageBlock extends EventCancellable {

    private BlockPos pos;
    private EnumFacing face;

    public EventPlayerDamageBlock(BlockPos pos, EnumFacing face) {
        this.pos = pos;
        this.face = face;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public EnumFacing getFace() {
        return face;
    }

    public void setFace(EnumFacing face) {
        this.face = face;
    }
}
