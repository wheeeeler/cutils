package net.wheel.cutils.api.gui.hud.component;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.gui.hud.GuiHudEditor;
import net.wheel.cutils.impl.gui.hud.anchor.AnchorPoint;

public class DraggableHudComponent extends HudComponent {

    private static final double ANCHOR_THRESHOLD = 80;
    protected final Minecraft mc = Minecraft.getMinecraft();
    @Setter
    @Getter
    private boolean rclicked;
    @Setter
    @Getter
    private boolean snappable;
    @Setter
    @Getter
    private boolean dragging;
    @Setter
    @Getter
    private boolean locked;
    @Setter
    @Getter
    private float deltaX;
    @Setter
    @Getter
    private float deltaY;
    @Setter
    @Getter
    private int color;
    @Setter
    @Getter
    private AnchorPoint anchorPoint;
    @Setter
    private DraggableHudComponent glued;
    @Setter
    @Getter
    private GlueSide glueSide;
    @Setter
    @Getter
    private boolean parent;

    @Setter
    @Getter
    private boolean enabled;

    public DraggableHudComponent(String name) {
        this.setName(name);
        this.setVisible(false);
        this.setSnappable(true);
        this.setLocked(false);
        this.setRclicked(false);
        this.setX(Minecraft.getMinecraft().displayWidth / 2.0f);
        this.setY(Minecraft.getMinecraft().displayHeight / 2.0f);
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int button) {
        if (this.isMouseInside(mouseX, mouseY)) {
            if (button == 0) {
                this.setDragging(true);
                this.setDeltaX(mouseX - this.getX());
                this.setDeltaY(mouseY - this.getY());
                crack.INSTANCE.getHudManager().moveToTop(this);
                this.anchorPoint = null;
                this.glued = null;
                this.glueSide = null;
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        boolean isHudEditor = Minecraft.getMinecraft().currentScreen instanceof GuiHudEditor;

        if (this.isDragging()) {
            this.setX(mouseX - this.getDeltaX());
            this.setY(mouseY - this.getDeltaY());
            this.clamp();
        } else if (this.isMouseInside(mouseX, mouseY)) {
            RenderUtil.drawRect(this.getX(), this.getY(), this.getX() + this.getW(), this.getY() + this.getH(),
                    0x45FFFFFF);
        }

        if (isHudEditor) {
            RenderUtil.drawRect(this.getX(), this.getY(), this.getX() + this.getW(), this.getY() + this.getH(),
                    0x75101010);
            if (this.isLocked()) {
                RenderUtil.drawBorderedRect(this.getX() - 1, this.getY() - 1, this.getX() + this.getW() + 1,
                        this.getY() + this.getH() + 1, 0.5f, 0x00000000, 0x75FFFFFF);
            }
        }

        if (this.glued != null) {
            if (this.glued.getAnchorPoint() == null) {
                if (this.anchorPoint != null) {
                    this.anchorPoint = null;
                }
            } else {
                this.anchorPoint = this.glued.getAnchorPoint();
            }
        }

        if (this.anchorPoint == null && this.glued != null) {
            this.setX(this.glued.getX());
            if (this.glueSide != null) {
                switch (this.glueSide) {
                    case TOP:

                        if (!isHudEditor && this.glued.getH() <= 0 && this.getH() <= 0) {
                            this.setY((this.glued.getY() - this.getEmptyH()) + this.glued.getEmptyH());
                        } else if (!isHudEditor && this.glued.getH() <= 0 && this.getH() > 0) {
                            this.setY((this.glued.getY() + this.glued.getEmptyH()) - this.getH());
                        } else if (!isHudEditor && this.glued.getH() > 0 && this.getH() <= 0) {
                            this.setY(this.glued.getY() - this.getEmptyH());
                        } else {
                            this.setY(this.glued.getY() - this.getH());
                        }
                        break;
                    case BOTTOM:
                        this.setY(this.glued.getY() + this.glued.getH());
                        break;
                }
            }
        }

        if (!this.isDragging()) {
            if (this.anchorPoint != null && this.glued != null) {
                switch (this.anchorPoint.getPoint()) {
                    case TOP_LEFT:
                        this.setX(this.anchorPoint.getX());
                        break;
                    case BOTTOM_LEFT:
                        this.setX(this.anchorPoint.getX());
                        break;
                    case TOP_RIGHT:
                        this.setX(this.anchorPoint.getX() - this.getW());
                        break;
                    case BOTTOM_RIGHT:
                        this.setX(this.anchorPoint.getX() - this.getW());
                        break;
                    case TOP_CENTER:
                        this.setX(this.anchorPoint.getX() - (this.getW() / 2.0f));
                        break;
                    case BOTTOM_CENTER:
                        this.setX(this.anchorPoint.getX() - (this.getW() / 2.0f));
                        break;
                }
                if (this.glueSide != null) {
                    switch (this.glueSide) {
                        case TOP:
                            this.setY(this.glued.getY() - this.getH());
                            break;
                        case BOTTOM:
                            this.setY(this.glued.getY() + this.glued.getH());
                            break;
                    }
                }
            } else if (this.anchorPoint != null) {
                switch (this.anchorPoint.getPoint()) {
                    case TOP_LEFT:
                        this.setX(this.anchorPoint.getX());
                        this.setY(this.anchorPoint.getY());
                        break;
                    case BOTTOM_LEFT:
                        this.setX(this.anchorPoint.getX());
                        this.setY(this.anchorPoint.getY() - this.getH());
                        break;
                    case TOP_RIGHT:
                        this.setX(this.anchorPoint.getX() - this.getW());
                        this.setY(this.anchorPoint.getY());
                        break;
                    case BOTTOM_RIGHT:
                        this.setX(this.anchorPoint.getX() - this.getW());
                        this.setY(this.anchorPoint.getY() - this.getH());
                        break;
                    case TOP_CENTER:
                        this.setX(this.anchorPoint.getX() - (this.getW() / 2.0f));
                        this.setY(this.anchorPoint.getY());
                        break;
                    case BOTTOM_CENTER:
                        this.setX(this.anchorPoint.getX() - (this.getW() / 2.0f));
                        this.setY(this.anchorPoint.getY() - this.getH());
                        break;
                }
            }
        }

        this.clamp();
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int button) {
        super.mouseRelease(mouseX, mouseY, button);

        if (button == 0) {
            if (this.isDragging()) {
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)
                        || !this.isSnappable()) {
                    this.setDragging(false);
                    return;
                }

                this.anchorPoint = this.findClosest(mouseX, mouseY);

                for (HudComponent component : crack.INSTANCE.getHudManager().getComponentList()) {
                    if (component instanceof DraggableHudComponent) {
                        DraggableHudComponent draggable = (DraggableHudComponent) component;
                        if (draggable != this && draggable.isVisible() && draggable.isSnappable()) {
                            if (this.collidesWith(draggable)) {
                                if ((this.getY() + (this.getH() / 2.0f)) < (draggable.getY()
                                        + (draggable.getH() / 2.0f))) {
                                    this.setY(draggable.getY() - this.getH());
                                    this.glueSide = GlueSide.TOP;
                                    this.glued = draggable;
                                    draggable.setParent(true);
                                    if (draggable.getAnchorPoint() != null) {
                                        this.anchorPoint = draggable.getAnchorPoint();
                                    }
                                } else if ((this.getY() + (this.getH() / 2.0f)) > (draggable.getY()
                                        + (draggable.getH() / 2.0f))) {
                                    this.setY(draggable.getY() + draggable.getH());
                                    this.glueSide = GlueSide.BOTTOM;
                                    this.glued = draggable;
                                    draggable.setParent(true);
                                    if (draggable.getAnchorPoint() != null) {
                                        this.anchorPoint = draggable.getAnchorPoint();
                                    }
                                }
                            } else {
                                AnchorPoint draggableClosest = draggable.getAnchorPoint();
                                AnchorPoint myClosest = this.findClosest(mouseX, mouseY);
                                if (draggableClosest != null && myClosest != null) {
                                    boolean sameAnchor = draggableClosest.getPoint().equals(myClosest.getPoint());
                                    if (sameAnchor) {
                                        this.anchorPoint = null;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            this.setDragging(false);
        } else if (button == 2) {
            if (this.isMouseInside(mouseX, mouseY)) {
                this.setLocked(!this.isLocked());
                this.setSnappable(!this.isSnappable());
            }
        } else if (button == 1) {
            if (this.isMouseInside(mouseX, mouseY)) {
                this.setRclicked(!this.isRclicked());
            }
        }
    }

    public AnchorPoint findClosest(float x, float y) {
        AnchorPoint ret = null;
        double max = ANCHOR_THRESHOLD;
        for (AnchorPoint point : crack.INSTANCE.getHudManager().getAnchorPoints()) {
            final double deltaX = x - point.getX();
            final double deltaY = y - point.getY();

            final double dist = MathHelper.sqrt(deltaX * deltaX + deltaY * deltaY);
            if (dist <= max) {
                max = dist;
                ret = point;
            }
        }

        return ret;
    }

    public AnchorPoint findClosest() {
        return findClosest(this.getX(), this.getY());
    }

    public void clamp() {
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        if (this.isLocked()) {
            return;

        }

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

    public void onEnable() {
        crack.INSTANCE.getEventManager().addEventListener(this);
    }

    public void onDisable() {
        crack.INSTANCE.getEventManager().removeEventListener(this);
    }

    public void onToggle() {

    }

    public void toggle() {
        this.setEnabled(!this.isEnabled());
        if (this.isEnabled()) {
            this.onEnable();
        } else {
            this.onDisable();
        }
        this.onToggle();
    }

    public Value findValue(String alias) {
        for (Value v : this.getValueList()) {
            for (String s : v.getAlias()) {
                if (alias.equalsIgnoreCase(s)) {
                    return v;
                }
            }

            if (v.getName().equalsIgnoreCase(alias)) {
                return v;
            }
        }
        return null;
    }

    public HudComponent getGlued() {
        return glued;
    }

    public enum GlueSide {
        TOP, BOTTOM
    }
}
