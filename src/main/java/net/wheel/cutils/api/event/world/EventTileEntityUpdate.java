package net.wheel.cutils.api.event.world;

import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import net.wheel.cutils.api.event.EventCancellable;

public class EventTileEntityUpdate extends EventCancellable {

    private final BlockPos pos;
    private final SPacketUpdateTileEntity packet;
    private final TileEntity tileEntity;

    public EventTileEntityUpdate(BlockPos pos, SPacketUpdateTileEntity packet, TileEntity tileEntity) {
        this.pos = pos;
        this.packet = packet;
        this.tileEntity = tileEntity;
    }

    public BlockPos getPos() {
        return pos;
    }

    public TileEntity getTileEntity() {
        return tileEntity;
    }

    public SPacketUpdateTileEntity getPacket() {
        return packet;
    }
}
