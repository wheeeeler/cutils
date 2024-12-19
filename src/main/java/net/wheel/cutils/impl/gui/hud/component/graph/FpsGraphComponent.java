package net.wheel.cutils.impl.gui.hud.component.graph;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import net.wheel.cutils.api.gui.hud.component.ResizableHudComponent;
import net.wheel.cutils.api.util.MathUtil;
import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.api.util.Timer;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.impl.gui.hud.GuiHudEditor;

public final class FpsGraphComponent extends ResizableHudComponent {

    public final Value<Float> delay = new Value<Float>("Delay", new String[] { "Del" },
            "The amount of delay(ms) between updates.", 500.0f, 0.0f, 2500.0f, 100.0f);

    private final ArrayList<FpsNode> fpsNodes = new ArrayList<FpsNode>();
    private final Timer timer = new Timer();

    public FpsGraphComponent() {
        super("FpsGraph", 60, 27, 600, 400);
        this.setW(60);
        this.setH(27);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (mc.player != null && mc.world != null) {
            final ScaledResolution sr = new ScaledResolution(mc);
            final DecimalFormat decimalFormat = new DecimalFormat("###.##");

            if (this.fpsNodes.size() > (this.getW() / 2)) {
                this.fpsNodes.clear();
            }

            if (this.timer.passed(this.delay.getValue())) {
                if (this.fpsNodes.size() > (this.getW() / 2 - 1)) {
                    this.fpsNodes.remove(0);
                }

                final float fps = Minecraft.getDebugFPS();
                this.fpsNodes.add(new FpsNode(fps));

                this.timer.reset();
            }

            if (mc.currentScreen instanceof GuiHudEditor) {
                for (float j = this.getX() + this.getW(); j > this.getX(); j -= 20) {
                    if (j <= this.getX())
                        continue;

                    if (j >= this.getX() + this.getW())
                        continue;

                    RenderUtil.drawLine(j, this.getY(), j, this.getY() + this.getH(), 2.0f, 0x75101010);
                }
            } else {

                RenderUtil.drawRect(this.getX(), this.getY(), this.getX() + this.getW(), this.getY() + this.getH(),
                        0x75101010);
            }

            String hoveredData = "";

            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.glScissor(this.getX(), this.getY(), this.getX() + this.getW(), this.getY() + this.getH(), sr);

            FpsNode lastNode = null;
            for (int i = 0; i < this.fpsNodes.size(); i++) {
                final FpsNode fpsNode = this.fpsNodes.get(i);

                final float mappedX = (float) MathUtil.map((this.getW() / 2 - 1) - i, 0, (this.getW() / 2 - 1),
                        this.getX() + this.getW() - 1, this.getX() + 1);
                final float mappedY = (float) MathUtil.map(fpsNode.speed, -2.0f, this.getAverageHeight(),
                        this.getY() + this.getH() - 1, this.getY() + 1) + this.getH() / 2;

                fpsNode.mappedX = mappedX;
                fpsNode.mappedY = mappedY;

                if (lastNode != null) {
                    RenderUtil.drawLine(fpsNode.mappedX, fpsNode.mappedY, lastNode.mappedX, lastNode.mappedY, 1.0f, -1);
                }

                RenderUtil.drawRect(fpsNode.mappedX - fpsNode.size, fpsNode.mappedY - fpsNode.size,
                        fpsNode.mappedX + fpsNode.size, fpsNode.mappedY + fpsNode.size, fpsNode.color.getRGB());

                if (i == this.fpsNodes.size() - 1) {
                    final String textToDraw = decimalFormat.format(fpsNode.speed) + "fps";
                    mc.fontRenderer.drawStringWithShadow(textToDraw,
                            fpsNode.mappedX - mc.fontRenderer.getStringWidth(textToDraw), fpsNode.mappedY + 3,
                            0xFFAAAAAA);
                }

                if (mouseX >= fpsNode.mappedX && mouseX <= fpsNode.mappedX + fpsNode.size && mouseY >= this.getY()
                        && mouseY <= this.getY() + this.getH()) {

                    RenderUtil.drawRect(fpsNode.mappedX - fpsNode.size, this.getY(), fpsNode.mappedX + fpsNode.size,
                            this.getY() + this.getH(), 0x40101010);

                    RenderUtil.drawRect(fpsNode.mappedX - fpsNode.size, fpsNode.mappedY - fpsNode.size,
                            fpsNode.mappedX + fpsNode.size, fpsNode.mappedY + fpsNode.size, 0xFFFF0000);

                    hoveredData = String.format("FPS: %s", decimalFormat.format(fpsNode.speed));
                }

                lastNode = fpsNode;
            }

            if (this.isMouseInside(mouseX, mouseY)) {

                mc.fontRenderer.drawStringWithShadow(this.delay.getValue() + "ms", this.getX() + 2,
                        this.getY() + this.getH() - mc.fontRenderer.FONT_HEIGHT - 1, 0xFFAAAAAA);
            }

            if (!hoveredData.equals("")) {
                mc.fontRenderer.drawStringWithShadow(hoveredData, this.getX() + 2,
                        this.getY() + this.getH() - mc.fontRenderer.FONT_HEIGHT * 2 - 1, 0xFFAAAAAA);
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);

            RenderUtil.drawBorderedRectBlurred(this.getX(), this.getY(), this.getX() + this.getW(),
                    this.getY() + this.getH(), 2.0f, 0x00000000, 0x90101010);
        } else {
            mc.fontRenderer.drawStringWithShadow("(fps graph)", this.getX(), this.getY(), 0xFFAAAAAA);
        }
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int button) {
        super.mouseRelease(mouseX, mouseY, button);
        if (this.isMouseInside(mouseX, mouseY) && button == 1) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                this.delay.setValue(this.delay.getValue() + this.delay.getInc());
            } else if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                this.delay.setValue(this.delay.getValue() - 1.0f);
            } else {
                this.delay.setValue(this.delay.getValue() - this.delay.getInc());
            }

            if (this.delay.getValue() <= this.delay.getMin() || this.delay.getValue() > this.delay.getMax())
                this.delay.setValue(500.0f);
        }
    }

    public float getAverageHeight() {
        float totalSpeed = 0;

        for (int i = this.fpsNodes.size() - 1; i > 0; i--) {
            final FpsNode fpsNode = this.fpsNodes.get(i);
            if (this.fpsNodes.size() > 11) {
                if (fpsNode != null && (i > this.fpsNodes.size() - 10)) {
                    totalSpeed += fpsNode.speed;
                }
            }
        }

        return totalSpeed / 10;
    }

    static class FpsNode {

        public float size = 0.5f;
        public float speed = 0.0f;
        public Color color;

        public float mappedX, mappedY;

        public FpsNode(float speed) {
            this.speed = speed;
            this.color = new Color(255, 255, 255);
        }

        public FpsNode() {

        }
    }
}
