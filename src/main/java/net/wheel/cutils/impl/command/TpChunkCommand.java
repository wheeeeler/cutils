package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.util.StringUtil;
import net.wheel.cutils.crack;

public final class TpChunkCommand extends Command {

    public TpChunkCommand() {
        super("tpchunk", new String[] { "TpC" }, "Teleports to the center of a specified chunk",
                "TpChunk <ChunkX> <Y> <ChunkZ>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 4, 4)) {
            this.printUsage();
            return;
        }

        String[] split = input.split(" ");
        if (isValidChunkCoordinates(split[1], split[2], split[3])) {
            int chunkX = Integer.parseInt(split[1]);
            int y = Integer.parseInt(split[2]);
            int chunkZ = Integer.parseInt(split[3]);

            teleportToChunk(chunkX, y, chunkZ);
        } else {
            crack.INSTANCE.errorChat("Invalid input. Expected integers for ChunkX, Y, and ChunkZ.");
        }
    }

    private boolean isValidChunkCoordinates(String chunkX, String y, String chunkZ) {
        return StringUtil.isInt(chunkX) && StringUtil.isInt(y) && StringUtil.isInt(chunkZ);
    }

    private void teleportToChunk(int chunkX, int y, int chunkZ) {
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
        int worldX = chunkPos.getXStart() + 8;
        int worldZ = chunkPos.getZStart() + 8;
        String dimensionName = getDimensionName(Minecraft.getMinecraft().player.dimension);

        String command = String.format("/tp %d %d %d", worldX, y, worldZ);
        sendTeleportCommand(command);
        crack.INSTANCE
                .logChat("Teleported to chunk \u00A76(" + chunkX + ", " + chunkZ + ")\u00A7f in " + dimensionName);
    }

    private void sendTeleportCommand(String command) {
        CPacketChatMessage packet = new CPacketChatMessage(command);
        if (Minecraft.getMinecraft().getConnection() != null) {
            Minecraft.getMinecraft().getConnection().sendPacket(packet);
        }
    }

    private String getDimensionName(int dimension) {
        DimensionType dimensionType = DimensionType.getById(dimension);
        return dimensionType.getName();
    }
}
