package net.wheel.cutils.api.gui.hud.component;

import net.minecraft.client.Minecraft;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.gui.hud.GuiHudEditor;

public class ResizableHudComponent extends DraggableHudComponent {

    protected final float CLICK_ZONE = 2;
    @Getter
    @Setter
    private boolean resizeDragging;
    @Getter
    @Setter
    private float resizeDeltaX;
    @Getter
    @Setter
    private float resizeDeltaY;
    @Getter
    @Setter
    private float initialWidth;
    @Getter
    @Setter
    private float initialHeight;
    @Getter
    @Setter
    private float maxWidth;
    @Getter
    @Setter
    private float maxHeight;

    public ResizableHudComponent(String name, float initialWidth, float initialHeight, float maxWidth,
            float maxHeight) {
        super(name);
        this.initialWidth = initialWidth;
        this.initialHeight = initialHeight;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int button) {
        super.mouseClick(mouseX, mouseY, button);
        final boolean inside = mouseX >= this.getX() + this.getW() - CLICK_ZONE
                && mouseX <= this.getX() + this.getW() + CLICK_ZONE && mouseY >= this.getY() + this.getH() - CLICK_ZONE
                && mouseY <= this.getY() + this.getH() + CLICK_ZONE;

        if (inside) {
            if (button == 0) {
                this.setResizeDragging(true);
                this.setDragging(false);
                this.setResizeDeltaX(mouseX - this.getW());
                this.setResizeDeltaY(mouseY - this.getH());
                crack.INSTANCE.getHudManager().moveToTop(this);
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (this.isResizeDragging()) {
            this.setW(mouseX - this.getResizeDeltaX());
            this.setH(mouseY - this.getResizeDeltaY());
            this.clampMaxs();
        }

        if (Minecraft.getMinecraft().currentScreen instanceof GuiHudEditor) {
            RenderUtil.drawRect(this.getX() + this.getW() - CLICK_ZONE, this.getY() + this.getH() - CLICK_ZONE,
                    this.getX() + this.getW() + CLICK_ZONE, this.getY() + this.getH() + CLICK_ZONE, 0x90CCCCCC);
        }

        final boolean insideClickZone = mouseX >= this.getX() + this.getW() - CLICK_ZONE
                && mouseX <= this.getX() + this.getW() + CLICK_ZONE && mouseY >= this.getY() + this.getH() - CLICK_ZONE
                && mouseY <= this.getY() + this.getH() + CLICK_ZONE;
        if (insideClickZone) {
            RenderUtil.drawRect(this.getX() + this.getW() - CLICK_ZONE, this.getY() + this.getH() - CLICK_ZONE,
                    this.getX() + this.getW() + CLICK_ZONE, this.getY() + this.getH() + CLICK_ZONE, 0x45FFFFFF);
        }

        this.clampMaxs();
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int button) {
        super.mouseRelease(mouseX, mouseY, button);

        if (button == 0) {
            if (this.isResizeDragging()) {
                this.setResizeDragging(false);
            }
        }
    }

    public void clampMaxs() {
        if (this.getW() <= this.getInitialWidth()) {
            this.setW(this.getInitialWidth());
        }

        if (this.getH() <= this.getInitialHeight()) {
            this.setH(this.getInitialHeight());
        }

        if (this.getW() >= this.getMaxWidth()) {
            this.setW(this.getMaxWidth());
        }

        if (this.getH() >= this.getMaxHeight()) {
            this.setH(this.getMaxHeight());
        }
    }

}
