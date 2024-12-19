package net.wheel.cutils.api.gui.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.util.RenderUtil;

public abstract class MainMenuButton {

    @Setter
    @Getter
    private float x;
    @Setter
    @Getter
    private float y;
    @Setter
    @Getter
    private float w;
    @Setter
    @Getter
    private float h;

    @Setter
    @Getter
    private String text;

    private boolean clicked;

    public MainMenuButton(float x, float y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.w = 140;
        this.h = 18;
    }

    public MainMenuButton(float x, float y, float w, float h, String text) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.text = text;
    }

    public void render(int x, int y, float partialTicks) {
        if (this.clicked) {
            RenderUtil.drawRect(this.x, this.y, this.x + this.w, this.y + this.h, 0x66111111);
            RenderUtil.drawGradientRect(this.x + 1, this.y + 1, this.x + this.w - 1, this.y + this.h - 1, 0xAA232323,
                    0xAA303030);
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.text,
                    this.x + (this.w / 2) - (Minecraft.getMinecraft().fontRenderer.getStringWidth(this.text) / 2.0f),
                    this.y + (this.h / 2) - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2.0f), 0x3b0202);
        } else {
            if (this.inside(x, y)) {
                RenderUtil.drawRect(this.x, this.y, this.x + this.w, this.y + this.h, 0x66111111);
                RenderUtil.drawGradientRect(this.x + 1, this.y + 1, this.x + this.w - 1, this.y + this.h - 1,
                        0xAA303030, 0xAA232323);
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.text,
                        this.x + (this.w / 2)
                                - (Minecraft.getMinecraft().fontRenderer.getStringWidth(this.text) / 2.0f),
                        this.y + (this.h / 2) - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2.0f), -1);
            } else {
                RenderUtil.drawRect(this.x, this.y, this.x + this.w, this.y + this.h, 0x66111111);
                RenderUtil.drawGradientRect(this.x + 1, this.y + 1, this.x + this.w - 1, this.y + this.h - 1,
                        0xAA303030, 0xAA232323);
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.text,
                        this.x + (this.w / 2)
                                - (Minecraft.getMinecraft().fontRenderer.getStringWidth(this.text) / 2.0f),
                        this.y + (this.h / 2) - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2.0f), 0xFFAAAAAA);
            }
        }
    }

    public void mouseRelease(int x, int y, int button) {
        if (inside(x, y) && this.clicked && button == 0) {
            this.action();
            Minecraft.getMinecraft().getSoundHandler()
                    .playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }

        if (button == 0) {
            this.clicked = false;
        }
    }

    public void mouseClicked(int x, int y, int button) {
        if (inside(x, y) && button == 0) {
            this.clicked = true;
        }
    }

    public abstract void action();

    private boolean inside(int x, int y) {
        return x >= this.getX() && x <= this.getX() + this.getW() && y >= this.getY() && y <= this.getY() + this.getH();
    }

}
