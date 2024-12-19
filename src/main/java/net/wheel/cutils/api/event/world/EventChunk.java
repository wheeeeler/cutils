package net.wheel.cutils.api.event.world;

import net.minecraft.world.chunk.Chunk;

public class EventChunk {

    private final ChunkType type;

    private final Chunk chunk;

    public EventChunk(ChunkType type, Chunk chunk) {
        this.type = type;
        this.chunk = chunk;
    }

    public ChunkType getType() {
        return type;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public enum ChunkType {
        LOAD, UNLOAD;

        ChunkType() {
        }
    }

}
