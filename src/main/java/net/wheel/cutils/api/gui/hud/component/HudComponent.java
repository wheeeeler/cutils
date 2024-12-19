package net.wheel.cutils.api.gui.hud.component;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.value.Value;

public class HudComponent {

    public ComponentListener mouseClickListener, rightClickListener;
    public boolean rightClickEnabled;
    public int subComponents = 0;
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
    private float emptyH;

    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private String displayName;
    @Setter
    @Getter
    private String tooltipText = "";
    @Setter
    @Getter
    private boolean visible;
    @Setter
    @Getter
    private List<Value> valueList = new ArrayList<Value>();

    public HudComponent() {

    }

    public HudComponent(String name) {
        this.name = name;
    }

    public HudComponent(String name, String tooltipText) {
        this.name = name;
        this.tooltipText = tooltipText;
    }

    public HudComponent(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.emptyH = h;
    }

    public HudComponent(float x, float y, float w, float h, String name) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.name = name;
    }

    public HudComponent(float x, float y, float w, float h, String name, String tooltipText) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.name = name;
        this.tooltipText = tooltipText;
    }

    public void render(int mouseX, int mouseY, float partialTicks) {

    }

    public void mouseClickMove(int mouseX, int mouseY, int button) {

    }

    public void mouseClick(int mouseX, int mouseY, int button) {

    }

    public void mouseRelease(int mouseX, int mouseY, int button) {
        if (this.isMouseInside(mouseX, mouseY)) {
            if (button == 0) {
                if (this.mouseClickListener != null)
                    this.mouseClickListener.onComponentEvent();
            } else if (button == 1) {
                if (this.rightClickListener != null)
                    this.rightClickListener.onComponentEvent();

                this.rightClickEnabled = !this.rightClickEnabled;
            }
        }
    }

    public void keyTyped(char typedChar, int keyCode) {

    }

    public void onClosed() {

    }

    public boolean isMouseInside(int mouseX, int mouseY) {
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getW() && mouseY >= this.getY()
                && mouseY <= this.getY() + this.getH();
    }

    public boolean collidesWith(HudComponent other) {

        boolean collisionX = this.x + this.w > other.x &&
                other.x + other.w > this.x;

        boolean collisionY = this.y + this.h > other.y &&
                other.y + other.h > this.y;

        return collisionX && collisionY;
    }

}
