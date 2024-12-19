package net.wheel.cutils.api.event.world;

import net.minecraft.block.Block;
import net.minecraft.util.BlockRenderLayer;

import net.wheel.cutils.api.event.EventCancellable;

public class EventGetBlockLayer extends EventCancellable {

    private Block block;
    private BlockRenderLayer layer;

    public EventGetBlockLayer(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public BlockRenderLayer getLayer() {
        return layer;
    }

    public void setLayer(BlockRenderLayer layer) {
        this.layer = layer;
    }
}
