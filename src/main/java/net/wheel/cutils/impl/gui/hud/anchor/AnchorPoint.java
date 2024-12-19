package net.wheel.cutils.impl.gui.hud.anchor;

import net.minecraft.client.gui.ScaledResolution;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnchorPoint {

    private float x;
    private float y;

    private Point point;

    public AnchorPoint(Point point) {
        this.point = point;
    }

    public AnchorPoint(float x, float y, Point point) {
        this.x = x;
        this.y = y;
        this.point = point;
    }

    public void updatePosition(final ScaledResolution sr) {
        switch (this.getPoint()) {
            case TOP_LEFT:
                this.x = 2;
                this.y = 2;
                break;
            case TOP_RIGHT:
                this.x = sr.getScaledWidth() - 2;
                this.y = 2;
                break;
            case BOTTOM_LEFT:
                this.x = 2;
                this.y = sr.getScaledHeight() - 2;
                break;
            case BOTTOM_RIGHT:
                this.x = sr.getScaledWidth() - 2;
                this.y = sr.getScaledHeight() - 2;
                break;
            case TOP_CENTER:
                this.x = sr.getScaledWidth() / 2.0f;
                this.y = 2;
                break;
            case BOTTOM_CENTER:
                this.x = sr.getScaledWidth() / 2.0f;
                this.y = sr.getScaledHeight() - 2;
                break;
        }
    }

    public enum Point {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, TOP_CENTER, BOTTOM_CENTER
    }
}
