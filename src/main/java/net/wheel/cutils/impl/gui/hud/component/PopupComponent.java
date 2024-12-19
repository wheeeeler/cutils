package net.wheel.cutils.impl.gui.hud.component;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.gui.hud.component.DraggableHudComponent;
import net.wheel.cutils.api.util.RenderUtil;

@Getter
@Setter
public class PopupComponent extends DraggableHudComponent {

    private static final int CLOSE_BUTTON_SIZE = 12;
    private static final int CLOSE_X_PADDING = 2;

    private String textData;

    public PopupComponent(String name, String textData) {
        super(name);
        this.textData = textData;
        this.setW(100);
        this.setH(50);
        this.setX((mc.displayWidth / 2.0f) - (this.getW() / 2));
        this.setY((mc.displayHeight / 2.0f) - (this.getH() / 2));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        RenderUtil.drawRect(this.getX(), this.getY(), this.getX() + this.getW(), this.getY() + this.getH(), 0xFF202020);

        mc.fontRenderer.drawSplitString(this.textData, (int) this.getX() + 2, (int) this.getY() + 2, 200, 0xFFFFFFFF);

        RenderUtil.drawRect(this.getX() + this.getW() - CLOSE_BUTTON_SIZE, this.getY(), this.getX() + this.getW(),
                this.getY() + CLOSE_BUTTON_SIZE, 0x75101010);
        RenderUtil.drawLine(this.getX() + this.getW() - CLOSE_BUTTON_SIZE + CLOSE_X_PADDING,
                this.getY() + CLOSE_X_PADDING, this.getX() + this.getW() - CLOSE_X_PADDING,
                this.getY() + CLOSE_BUTTON_SIZE - CLOSE_X_PADDING, 1, 0xFFFFFFFF);
        RenderUtil.drawLine(this.getX() + this.getW() - CLOSE_BUTTON_SIZE + CLOSE_X_PADDING,
                this.getY() + CLOSE_BUTTON_SIZE - CLOSE_X_PADDING, this.getX() + this.getW() - CLOSE_X_PADDING,
                this.getY() + CLOSE_X_PADDING, 1, 0xFFFFFFFF);
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int button) {
        super.mouseRelease(mouseX, mouseY, button);

        final boolean insideCloseButton = mouseX >= this.getX() + (this.getW() - CLOSE_BUTTON_SIZE) &&
                mouseX <= this.getX() + this.getW() &&
                mouseY >= this.getY() &&
                mouseY <= this.getY() + CLOSE_BUTTON_SIZE;

        if (insideCloseButton && button == 0) {
            this.onCloseButton();
        }
    }

    public void onCloseButton() {
        this.setVisible(false);
    }
}
