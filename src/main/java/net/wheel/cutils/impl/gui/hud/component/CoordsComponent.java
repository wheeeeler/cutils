package net.wheel.cutils.impl.gui.hud.component;

import java.text.DecimalFormat;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;

import net.wheel.cutils.api.gui.hud.component.DraggableHudComponent;

public final class CoordsComponent extends DraggableHudComponent {

    public CoordsComponent() {
        super("CurrentCoords");
        this.setH(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (mc.player != null && mc.world != null) {
            final DecimalFormat df = new DecimalFormat("0.0");

            final String nether = this.isRclicked() && mc.player.dimension == -1
                    ? ChatFormatting.GRAY + " [" + ChatFormatting.RESET
                            + df.format(mc.player.posX * 8) + ", "
                            + df.format(mc.player.posZ * 8) + ChatFormatting.GRAY + "]"
                    : this.isRclicked() && mc.player.dimension != -1 ? ChatFormatting.GRAY + " [" + ChatFormatting.RESET
                            + df.format(mc.player.posX / 8) + ", "
                            + df.format(mc.player.posZ / 8) + ChatFormatting.GRAY + "]"
                            : "";

            final String coords = ChatFormatting.GRAY + "XYZ " + ChatFormatting.RESET
                    + df.format(mc.player.posX) + ", "
                    + df.format(mc.player.posY) + ", "
                    + df.format(mc.player.posZ) + nether;

            this.setW(Minecraft.getMinecraft().fontRenderer.getStringWidth(coords));
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(coords, this.getX(), this.getY(), -1);
        } else {
            this.setW(Minecraft.getMinecraft().fontRenderer.getStringWidth("(coords)"));
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("(coords)", this.getX(), this.getY(),
                    0xFFAAAAAA);
        }
    }

}
