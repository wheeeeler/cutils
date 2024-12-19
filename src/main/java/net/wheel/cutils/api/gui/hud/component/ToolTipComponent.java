package net.wheel.cutils.api.gui.hud.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import net.wheel.cutils.api.util.ColorUtil;
import net.wheel.cutils.api.util.RenderUtil;

public class ToolTipComponent extends HudComponent {

    public String text;
    public int alpha;

    public ToolTipComponent(String text) {
        this.text = text;
        this.alpha = 0;

        final int tooltipWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
        final int tooltipHeight = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
        this.setW(tooltipWidth);
        this.setH(tooltipHeight);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (this.alpha < 0xFF) {
            this.alpha += 4;
        }

        if (this.alpha > 0x99) {
            this.setX(mouseX - (this.getW() / 2.0f));
            this.setY(mouseY - 18);

            this.clamp();

            RenderUtil.drawRect(this.getX() - 2, this.getY() - 2, this.getX() + this.getW() + 2,
                    this.getY() + this.getH() + 2, ColorUtil.changeAlpha(0x80101010, this.alpha / 2));
            RenderUtil.drawRect(this.getX() - 1, this.getY() - 1, this.getX() + this.getW() + 1,
                    this.getY() + this.getH() + 1, ColorUtil.changeAlpha(0xAD101010, this.alpha / 2));

            GlStateManager.enableBlend();
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.text, this.getX() + 1, this.getY() + 1,
                    ColorUtil.changeAlpha(0xFFFFFFFF, this.alpha));
            GlStateManager.disableBlend();
        }
    }

    private void clamp() {
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        if (this.getX() <= 0) {
            this.setX(2);
        }

        if (this.getY() <= 0) {
            this.setY(2);
        }

        if (this.getX() + this.getW() >= sr.getScaledWidth() - 2) {
            this.setX(sr.getScaledWidth() - 2 - this.getW());
        }

        if (this.getY() + this.getH() >= sr.getScaledHeight() - 2) {
            this.setY(sr.getScaledHeight() - 2 - this.getH());
        }
    }
}
