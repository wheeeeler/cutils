package net.wheel.cutils.api.gui.hud.component;

import net.minecraft.client.Minecraft;

import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.crack;

public class ButtonComponent extends HudComponent {

    public boolean enabled;

    public ButtonComponent(String name) {
        super(name);
        this.enabled = false;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (isMouseInside(mouseX, mouseY)) {
            RenderUtil.drawGradientRect(this.getX(), this.getY(), this.getX() + this.getW(), this.getY() + this.getH(),
                    crack.INSTANCE.getColorManager().getColor("HoverGradientStart").getRGB(),
                    crack.INSTANCE.getColorManager().getColor("HoverGradientEnd").getRGB());
        }

        RenderUtil.drawRect(this.getX(), this.getY(),
                this.getX() + (this.rightClickListener != null ? this.getW() - 8 : this.getW()),
                this.getY() + this.getH(), crack.INSTANCE.getColorManager().getColor("Background").getRGB());

        if (this.subComponents > 0) {

            final boolean isMousingHoveringDropdown = mouseX >= this.getX() + this.getW() - 8
                    && mouseX <= this.getX() + this.getW() && mouseY >= this.getY()
                    && mouseY <= this.getY() + this.getH();

            RenderUtil.drawRect(this.getX() + this.getW() - 8, this.getY(), this.getX() + this.getW(),
                    this.getY() + this.getH(), crack.INSTANCE.getColorManager().getColor("Background").getRGB());

            int triangleDirection = this.rightClickEnabled ? 180 : -90;
            int triangleColor = crack.INSTANCE.getColorManager().getColor("ModuleEnabledText").getRGB();
            int hoverTriangleColor = crack.INSTANCE.getColorManager().getColor("ModuleEnabledText").getRGB();

            RenderUtil.drawTriangle(this.getX() + this.getW() - 4, this.getY() + 4, 3, triangleDirection,
                    triangleColor);
            if (isMousingHoveringDropdown) {
                RenderUtil.drawTriangle(this.getX() + this.getW() - 4, this.getY() + 4, 3, triangleDirection,
                        hoverTriangleColor);
            }
        }

        String renderName = this.getDisplayName() != null ? this.getDisplayName() : this.getName();
        Minecraft.getMinecraft().fontRenderer.drawString(renderName, (int) this.getX() + 1, (int) this.getY() + 1,
                this.enabled ? crack.INSTANCE.getColorManager().getColor("ModuleEnabledText").getRGB()
                        : crack.INSTANCE.getColorManager().getColor("ModuleDisabledText").getRGB());
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int button) {
        if (!this.isMouseInside(mouseX, mouseY))
            return;

        if (button == 0) {

            if (this.subComponents > 0) {

                if (mouseX >= this.getX() + this.getW() - 8 && mouseX <= this.getX() + this.getW()
                        && mouseY >= this.getY() && mouseY <= this.getY() + this.getH()) {
                    this.rightClickEnabled = !this.rightClickEnabled;
                    return;
                }
            }

            this.enabled = !this.enabled;
        }

        super.mouseRelease(mouseX, mouseY, button);
    }
}
