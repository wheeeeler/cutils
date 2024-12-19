package net.wheel.cutils.impl.command;

import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;

public final class FindEntityCommand extends Command {

    public FindEntityCommand() {
        super("FindEntity", new String[] { "FindEnt" }, "Scans nearby chunks for entity spawns", "FindEntity <Entity>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 2)) {
            this.printUsage();
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();
        final BlockPos pos = mc.player.getPosition();
        final Chunk chunk = mc.world.getChunk(pos);
        final Biome biome = chunk.getBiome(pos, mc.world.getBiomeProvider());

        crack.INSTANCE.getLogger().log(Level.INFO, biome.getSpawnableList(EnumCreatureType.CREATURE).toString());
    }
}
