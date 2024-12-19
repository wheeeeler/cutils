package net.wheel.cutils.impl.gui.hud.component;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

import net.wheel.cutils.api.gui.hud.component.DraggableHudComponent;

public final class RegionComponent extends DraggableHudComponent {

    public RegionComponent() {
        super("RegionView");
        this.setH(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (mc.player != null && mc.world != null) {
            String displayText = getRgName();

            this.setW(Minecraft.getMinecraft().fontRenderer.getStringWidth(displayText));

            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(displayText, this.getX(), this.getY(), -1);
        } else {
            this.setW(Minecraft.getMinecraft().fontRenderer.getStringWidth("(region name)"));
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("(region name)", this.getX(), this.getY(),
                    0xFFAAAAAA);
        }
    }

    private String getRgName() {
        BlockPos playerPos = mc.player.getPosition();
        int regionX = playerPos.getX() >> 9;
        int regionZ = playerPos.getZ() >> 9;

        return ChatFormatting.GRAY + "Region: " + ChatFormatting.RESET + "r." + regionX + "." + regionZ + ".mca";
    }
}
