package net.wheel.cutils.impl.gui.hud;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;

import net.wheel.cutils.api.gui.hud.component.DraggableHudComponent;
import net.wheel.cutils.api.gui.hud.component.HudComponent;
import net.wheel.cutils.api.gui.hud.component.ToolTipComponent;
import net.wheel.cutils.api.gui.hud.particle.ParticleSystem;
import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.gui.hud.anchor.AnchorPoint;
import net.wheel.cutils.impl.gui.hud.component.ParticlesComponent;
import net.wheel.cutils.impl.gui.hud.component.module.ModuleListComponent;
import net.wheel.cutils.impl.module.ui.CrackHudModule;

public final class GuiHudEditor extends GuiScreen {

    private ParticleSystem particleSystem;
    private ParticlesComponent particlesComponent;

    @Override
    public void initGui() {
        super.initGui();

        Keyboard.enableRepeatEvents(true);

        this.particlesComponent = (ParticlesComponent) crack.INSTANCE.getHudManager()
                .findComponent(ParticlesComponent.class);
        if (particlesComponent != null) {
            if (particlesComponent.isVisible()) {
                this.particleSystem = new ParticleSystem(new ScaledResolution(mc));
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        final CrackHudModule mod = (CrackHudModule) crack.INSTANCE.getModuleManager().find(CrackHudModule.class);

        if (mod != null) {
            if (keyCode == Keyboard.getKeyIndex(mod.getKey())) {
                if (mod.isOpen()) {
                    mod.setOpen(false);
                } else {
                    Minecraft.getMinecraft().displayGuiScreen(null);
                }
            }
        }

        for (HudComponent component : crack.INSTANCE.getHudManager().getComponentList()) {
            if (component.isVisible()) {
                component.keyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h) {
        super.onResize(mcIn, w, h);

        if (this.particleSystem != null)
            this.particleSystem = new ParticleSystem(new ScaledResolution(mcIn));

        final ScaledResolution sr = new ScaledResolution(mcIn);
        for (AnchorPoint anchorPoint : crack.INSTANCE.getHudManager().getAnchorPoints()) {
            anchorPoint.updatePosition(sr);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawDefaultBackground();

        final ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

        if (this.particleSystem != null)
            this.particleSystem.render(mouseX, mouseY);

        final float halfWidth = res.getScaledWidth() / 2.0f;
        final float halfHeight = res.getScaledHeight() / 2.0f;

        RenderUtil.drawLine(halfWidth, 0, halfWidth, res.getScaledHeight(), 1,
                crack.INSTANCE.getColorManager().getColor("GridLine").getRGB());
        RenderUtil.drawLine(0, halfHeight, res.getScaledWidth(), halfHeight, 1,
                crack.INSTANCE.getColorManager().getColor("GridLine").getRGB());

        for (AnchorPoint point : crack.INSTANCE.getHudManager().getAnchorPoints()) {
            RenderUtil.drawRect(point.getX() - 1, point.getY() - 1, point.getX() + 1, point.getY() + 1,
                    crack.INSTANCE.getColorManager().getColor("Anchor").getRGB());
        }

        ToolTipComponent toolTipComponent = null;

        for (HudComponent component : crack.INSTANCE.getHudManager().getComponentList()) {
            if (component.isVisible()) {
                component.render(mouseX, mouseY, partialTicks);

                if (component instanceof ModuleListComponent) {
                    final ModuleListComponent moduleListComponent = (ModuleListComponent) component;
                    if (moduleListComponent.getCurrentToolTip() != null) {
                        toolTipComponent = moduleListComponent.getCurrentToolTip();
                    }
                }

                if (component instanceof DraggableHudComponent) {
                    DraggableHudComponent draggable = (DraggableHudComponent) component;
                    if (draggable.isDragging()) {
                        int SIZE = 2;
                        if (draggable.getW() < 12 || draggable.getH() < 12)
                            SIZE = 1;
                        else if (draggable.getW() <= 0 || draggable.getH() <= 0)
                            SIZE = 0;

                        boolean colliding = false;

                        for (HudComponent other : crack.INSTANCE.getHudManager().getComponentList()) {
                            if (other instanceof DraggableHudComponent) {
                                DraggableHudComponent otherDraggable = (DraggableHudComponent) other;
                                if (other != draggable && draggable.collidesWith(otherDraggable)
                                        && otherDraggable.isVisible() && draggable.isSnappable()
                                        && otherDraggable.isSnappable()) {
                                    colliding = true;

                                    RenderUtil.drawBorderedRect(draggable.getX() - 1, draggable.getY() - 1,
                                            draggable.getX() + draggable.getW() + 1,
                                            draggable.getY() + draggable.getH() + 1,
                                            1, crack.INSTANCE.getColorManager().getColor("CollisionBorder").getRGB(),
                                            crack.INSTANCE.getColorManager().getColor("CollisionBackground").getRGB());
                                    RenderUtil.drawRect(draggable.getX(), draggable.getY(),
                                            draggable.getX() + draggable.getW(), draggable.getY() + draggable.getH(),
                                            crack.INSTANCE.getColorManager().getColor("CollisionBackground").getRGB());
                                    RenderUtil.drawBorderedRect(other.getX() - 1, other.getY() - 1,
                                            other.getX() + other.getW() + 1, other.getY() + other.getH() + 1, 1,
                                            crack.INSTANCE.getColorManager().getColor("CollisionBorder").getRGB(),
                                            crack.INSTANCE.getColorManager().getColor("CollisionBackground").getRGB());
                                    RenderUtil.drawRect(other.getX(), other.getY(), other.getX() + other.getW(),
                                            other.getY() + other.getH(),
                                            crack.INSTANCE.getColorManager().getColor("CollisionBackground").getRGB());
                                }
                            }
                        }

                        if (draggable.isSnappable() && !colliding) {
                            int snappableBackgroundColor = crack.INSTANCE.getColorManager()
                                    .getColor("SnapInactiveBackground").getRGB();
                            if (draggable.findClosest(mouseX, mouseY) != null) {
                                snappableBackgroundColor = crack.INSTANCE.getColorManager()
                                        .getColor("SnapActiveBackground").getRGB();
                            }

                            RenderUtil.drawBorderedRect(draggable.getX() - 1, draggable.getY() - 1,
                                    draggable.getX() + draggable.getW() + 1, draggable.getY() + draggable.getH() + 1, 1,
                                    snappableBackgroundColor,
                                    crack.INSTANCE.getColorManager().getColor("SnapBorder").getRGB());
                            RenderUtil.drawRect(draggable.getX(), draggable.getY(), draggable.getX() + SIZE,
                                    draggable.getY() + SIZE,
                                    crack.INSTANCE.getColorManager().getColor("SnapCorner").getRGB());
                            RenderUtil.drawRect(draggable.getX() + draggable.getW() - SIZE, draggable.getY(),
                                    draggable.getX() + draggable.getW(), draggable.getY() + SIZE,
                                    crack.INSTANCE.getColorManager().getColor("SnapCorner").getRGB());
                            RenderUtil.drawRect(draggable.getX(), (draggable.getY() + draggable.getH()) - SIZE,
                                    draggable.getX() + SIZE, draggable.getY() + draggable.getH(),
                                    crack.INSTANCE.getColorManager().getColor("SnapCorner").getRGB());
                            RenderUtil.drawRect(draggable.getX() + draggable.getW() - SIZE,
                                    (draggable.getY() + draggable.getH()) - SIZE, draggable.getX() + draggable.getW(),
                                    draggable.getY() + draggable.getH(),
                                    crack.INSTANCE.getColorManager().getColor("SnapCorner").getRGB());
                        }

                    }
                }
            }
        }

        if (toolTipComponent != null) {
            toolTipComponent.render(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        for (HudComponent component : crack.INSTANCE.getHudManager().getComponentList()) {
            if (component.isVisible()) {
                component.mouseClickMove(mouseX, mouseY, clickedMouseButton);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);

            for (HudComponent component : crack.INSTANCE.getHudManager().getComponentList()) {
                if (component.isVisible()) {
                    component.mouseClick(mouseX, mouseY, mouseButton);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        for (HudComponent component : crack.INSTANCE.getHudManager().getComponentList()) {
            if (component.isVisible()) {
                component.mouseRelease(mouseX, mouseY, state);
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {

        Keyboard.enableRepeatEvents(false);

        final CrackHudModule crackHudModule = (CrackHudModule) crack.INSTANCE.getModuleManager()
                .find(CrackHudModule.class);
        if (crackHudModule != null) {
            if (crackHudModule.blur.getValue()) {
                if (OpenGlHelper.shadersSupported) {
                    mc.entityRenderer.stopUseShader();
                }
            }
        }

        for (HudComponent component : crack.INSTANCE.getHudManager().getComponentList()) {
            if (component instanceof DraggableHudComponent) {
                if (component.isVisible()) {
                    final DraggableHudComponent draggable = (DraggableHudComponent) component;
                    if (draggable.isDragging()) {
                        draggable.setDragging(false);
                    }

                    component.onClosed();
                }
            }
        }

        super.onGuiClosed();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (this.particleSystem != null) {
            if (this.particlesComponent != null) {
                if (!this.particlesComponent.isVisible()) {
                    this.particleSystem = null;
                    return;
                }
            }

            this.particleSystem.update();
        } else {
            if (this.particlesComponent != null) {
                if (this.particlesComponent.isVisible()) {
                    this.particleSystem = new ParticleSystem(new ScaledResolution(mc));
                }
            }
        }
    }

    public void unload() {

    }
}
