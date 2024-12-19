package net.wheel.cutils.api.event.world;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import net.wheel.cutils.api.event.EventCancellable;

public class EventBlockInteract extends EventCancellable {

    private final BlockPos pos;
    private final EnumFacing face;
    private final EnumHand hand;

    public EventBlockInteract(BlockPos pos, EnumFacing face, EnumHand hand) {
        this.pos = pos;
        this.face = face;
        this.hand = hand;
    }

    public BlockPos getPos() {
        return pos;
    }

    public EnumFacing getFace() {
        return face;
    }

    public EnumHand getHand() {
        return hand;
    }
}
