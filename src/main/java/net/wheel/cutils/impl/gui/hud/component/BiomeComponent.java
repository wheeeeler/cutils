package net.wheel.cutils.impl.gui.hud.component;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import net.wheel.cutils.api.gui.hud.component.DraggableHudComponent;

public final class BiomeComponent extends DraggableHudComponent {

    public BiomeComponent() {
        super("CurrentBiome");
        this.setH(mc.fontRenderer.FONT_HEIGHT);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (mc.world != null) {
            final BlockPos pos = mc.player.getPosition();
            final Chunk chunk = mc.world.getChunk(pos);
            final Biome biome = chunk.getBiome(pos, mc.world.getBiomeProvider());
            final String text = ChatFormatting.GRAY + "Biome" + ChatFormatting.RESET + " " + biome.getBiomeName();
            this.setW(mc.fontRenderer.getStringWidth(text));
            mc.fontRenderer.drawStringWithShadow(text, this.getX(), this.getY(), -1);
        } else {
            this.setW(mc.fontRenderer.getStringWidth("(biome)"));
            mc.fontRenderer.drawStringWithShadow("(biome)", this.getX(), this.getY(), 0xFFAAAAAA);
        }
    }

}
