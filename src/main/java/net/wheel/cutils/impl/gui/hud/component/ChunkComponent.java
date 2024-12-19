package net.wheel.cutils.impl.gui.hud.component;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

import net.wheel.cutils.api.gui.hud.component.DraggableHudComponent;

public final class ChunkComponent extends DraggableHudComponent {

    public ChunkComponent() {
        super("ChunkCoords");
        this.setH(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (mc.player != null && mc.world != null) {
            String displayText = getString();

            this.setW(Minecraft.getMinecraft().fontRenderer.getStringWidth(displayText));

            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(displayText, this.getX(), this.getY(), -1);
        } else {
            this.setW(Minecraft.getMinecraft().fontRenderer.getStringWidth("(chunk coords)"));
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("(chunk coords)", this.getX(), this.getY(),
                    0xFFAAAAAA);
        }
    }

    private String getString() {
        BlockPos playerPos = mc.player.getPosition();
        int chunkX = playerPos.getX() >> 4;
        int chunkZ = playerPos.getZ() >> 4;
        int dimensionId = mc.player.dimension;

        final String chunkCoords = ChatFormatting.GRAY + "Chunk: " + ChatFormatting.RESET
                + "X:" + chunkX + " Z:" + chunkZ;

        final String dimensionText = ChatFormatting.GRAY + " in " + ChatFormatting.RESET + "DIM" + dimensionId;

        return chunkCoords + dimensionText;
    }
}
