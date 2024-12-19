package net.wheel.cutils.api.gui.hud.component;

import net.minecraft.client.Minecraft;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.crack;

@Setter
@Getter
public class HudComponentOptions extends HudComponent {

    private HudComponent parent;

    public HudComponentOptions(HudComponent parent) {
        this.parent = parent;
        this.setVisible(false);
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int button) {
        if (this.isMouseInside(mouseX, mouseY) && button == 0) {
            crack.INSTANCE.getHudManager().moveToTop(this);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (parent == null)
            return;

        this.setX(parent.getX() + parent.getW());
        this.setY(parent.getY());

        final int parentNameWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(parent.getName());
        final int visibleStringWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth("Visible");
        int yOffset = 0;

        RenderUtil.drawRect(this.getX(), this.getY(), this.getX() + parentNameWidth,
                this.getY() + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT,
                crack.INSTANCE.getColorManager().getColor("Background").getRGB());

        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(parent.getName(), this.getX(), this.getY(),
                crack.INSTANCE.getColorManager().getColor("TitleText").getRGB());

        yOffset += Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;

        RenderUtil.drawRect(this.getX(), this.getY() + yOffset, this.getX() + visibleStringWidth,
                this.getY() + yOffset + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT,
                parent.isVisible() ? crack.INSTANCE.getColorManager().getColor("ModuleEnabled").getRGB()
                        : crack.INSTANCE.getColorManager().getColor("ModuleDisabled").getRGB());

        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Visible", this.getX(), this.getY() + yOffset,
                crack.INSTANCE.getColorManager().getColor("ModuleEnabledText").getRGB());

        yOffset += Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;

        this.setW(Math.max(parentNameWidth, visibleStringWidth));
        this.setH(yOffset);
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int button) {
        if (button == 0) {
            if ((mouseX > this.getX())
                    && (mouseX < this.getX() + Minecraft.getMinecraft().fontRenderer.getStringWidth("Visible"))) {
                if (mouseY > (this.getY() + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT)) {
                    if (mouseY < (this.getY() + this.getH())) {
                        parent.setVisible(!parent.isVisible());
                    }
                }
            }
        }

    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int button) {
        super.mouseClickMove(mouseX, mouseY, button);
    }

}
