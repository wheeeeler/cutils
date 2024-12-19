package net.wheel.cutils.api.gui.hud.component;

import java.awt.*;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.texture.Texture;
import net.wheel.cutils.api.util.ColorUtil;
import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.crack;

public class ColorComponent extends TextComponent {

    private static final int COLOR_SIZE = 7;
    private static final int GEAR_WIDTH = 8;
    private final Texture gearTexture;
    private final Texture gearTextureEnabled;
    @Setter
    @Getter
    private Color currentColor;
    @Setter
    @Getter
    private String customDisplayValue;

    public ColorComponent(String name, int defaultColor) {
        super(name, String.valueOf(defaultColor), false);
        this.currentColor = new Color(defaultColor);
        this.setText("#" + Integer.toHexString(this.currentColor.getRGB()).toLowerCase().substring(2));
        this.gearTexture = new Texture("gear_wheel.png");
        this.gearTextureEnabled = new Texture("gear_wheel-enabled.png");

        this.setH(9);
    }

    public ColorComponent(String name, int defaultColor, String customDisplayValue) {
        this(name, defaultColor);
        this.customDisplayValue = customDisplayValue;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {

        String displayedName = null;
        if (this.focused) {
            displayedName = "";
        } else if (customDisplayValue != null) {
            displayedName = customDisplayValue;
        } else if (this.getDisplayName() != null) {
            displayedName = this.getDisplayName();
        } else {
            displayedName = this.getName();
        }

        this.renderReserved(mouseX, mouseY, partialTicks, displayedName, this.focused, SPACING + COLOR_SIZE,
                SPACING + GEAR_WIDTH + SPACING);

        RenderUtil.drawRect(this.getX() + BORDER, this.getY() + BORDER, this.getX() + BORDER + COLOR_SIZE,
                this.getY() + BORDER + COLOR_SIZE, ColorUtil.changeAlpha(this.currentColor.getRGB(), 0xFF));

        final float gearOffset = this.getX() + this.getW() - BORDER - GEAR_WIDTH;
        if (this.focused) {
            RenderUtil.drawRect(gearOffset - SPACING, this.getY(), this.getX() + this.getW(), this.getY() + this.getH(),
                    0xFF101010);
            this.gearTextureEnabled.bind();
            this.gearTextureEnabled.render(gearOffset, this.getY() + ICON_V_OFFSET, GEAR_WIDTH, GEAR_WIDTH);
        } else {
            this.gearTexture.bind();
            this.gearTexture.render(gearOffset, this.getY() + ICON_V_OFFSET, GEAR_WIDTH, GEAR_WIDTH);
        }
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int button) {
        super.mouseRelease(mouseX, mouseY, button);

        if (!this.focused)
            return;

        if (button == 0) {

            final float right = this.getX() + this.getW() - BORDER - GEAR_WIDTH - SPACING;
            if (mouseX >= right - CHECK_WIDTH && mouseX <= right && mouseY >= this.getY()
                    && mouseY <= this.getY() + this.getH()) {
                this.enterPressed();
            }
        }
    }

    @Override
    protected void enterPressed() {
        try {
            int newColor = (int) Long.parseLong(this.getText().replaceAll("#", ""), 16);
            this.currentColor = new Color(newColor);
        } catch (NumberFormatException e) {
            crack.INSTANCE
                    .logChat(this.getName() + ": Invalid color format. Correct format example: \"ff0000\" for red.");
        } catch (Exception e) {
            crack.INSTANCE.logChat(
                    this.getName() + ": Something went terribly wrong while setting the color. Please try again.");
        }

        super.enterPressed();
    }

}
