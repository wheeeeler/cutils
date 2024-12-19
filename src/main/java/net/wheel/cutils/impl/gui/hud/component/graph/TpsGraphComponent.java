package net.wheel.cutils.impl.gui.hud.component.graph;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import net.wheel.cutils.api.gui.hud.component.ResizableHudComponent;
import net.wheel.cutils.api.util.MathUtil;
import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.api.util.Timer;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.gui.hud.GuiHudEditor;

public final class TpsGraphComponent extends ResizableHudComponent {

    public final Value<Float> delay = new Value<Float>("Delay", new String[] { "Del" },
            "The amount of delay(ms) between updates.", 500.0f, 0.0f, 2500.0f, 100.0f);

    private final List<TpsNode> tpsNodes = new CopyOnWriteArrayList<TpsNode>();
    private final Timer timer = new Timer();

    public TpsGraphComponent() {
        super("TpsGraph", 60, 27, 600, 400);
        this.setW(60);
        this.setH(27);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (mc.world != null) {
            if (this.tpsNodes.size() > (this.getW() / 2)) {
                this.tpsNodes.clear();
            }

            if (this.timer.passed(this.delay.getValue())) {
                if (this.tpsNodes.size() > (this.getW() / 2 - 1)) {
                    this.tpsNodes.remove(0);
                }

                this.tpsNodes.add(new TpsNode(crack.INSTANCE.getTickRateManager().getTickRate()));
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

            for (int i = 0; i < this.tpsNodes.size(); i++) {
                final TpsNode tpsNode = this.tpsNodes.get(i);
                final float mappedX = (float) MathUtil.map((this.getW() / 2 - 1) - i, 0, (this.getW() / 2 - 1),
                        this.getX() + this.getW() - 1, this.getX() + 1);
                final float mappedY = (float) MathUtil.map(tpsNode.tps, 0, 20, this.getY() + this.getH() - 1,
                        this.getY() + 1);

                RenderUtil.drawGradientRect(mappedX - tpsNode.size, mappedY, mappedX + tpsNode.size,
                        this.getY() + this.getH(), tpsNode.color.getRGB(), 0x00FF0000);

                RenderUtil.drawRect(mappedX - tpsNode.size, mappedY, mappedX + tpsNode.size, mappedY + tpsNode.size,
                        tpsNode.color.getRGB());

                if (mouseX >= mappedX && mouseX <= mappedX + tpsNode.size && mouseY >= mappedY
                        && mouseY <= this.getY() + this.getH()) {

                    RenderUtil.drawRect(mappedX - tpsNode.size, mappedY, mappedX + tpsNode.size,
                            this.getY() + this.getH(), 0x40101010);

                    final DecimalFormat decimalFormat = new DecimalFormat("###.##");
                    hoveredData = String.format("TPS: %s", decimalFormat.format(tpsNode.tps));
                }
            }

            if (this.isMouseInside(mouseX, mouseY)) {

                mc.fontRenderer.drawStringWithShadow(this.delay.getValue() + "ms", this.getX() + 2,
                        this.getY() + this.getH() - mc.fontRenderer.FONT_HEIGHT - 1, 0xFFAAAAAA);
            }

            if (!hoveredData.equals("")) {
                mc.fontRenderer.drawStringWithShadow(hoveredData, this.getX() + 2,
                        this.getY() + this.getH() - mc.fontRenderer.FONT_HEIGHT * 2 - 1, 0xFFAAAAAA);
            }

            RenderUtil.drawBorderedRectBlurred(this.getX(), this.getY(), this.getX() + this.getW(),
                    this.getY() + this.getH(), 2.0f, 0x00000000, 0x90101010);
        } else {
            mc.fontRenderer.drawStringWithShadow("(tps graph)", this.getX(), this.getY(), 0xFFAAAAAA);
        }
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int button) {
        super.mouseRelease(mouseX, mouseY, button);
        if (this.isMouseInside(mouseX, mouseY) && button == 1) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                this.delay.setValue(this.delay.getValue() + this.delay.getInc());
            } else if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                this.delay.setValue(this.delay.getValue() - 10.0f);
            } else {
                this.delay.setValue(this.delay.getValue() - this.delay.getInc());
            }

            if (this.delay.getValue() <= this.delay.getMin() || this.delay.getValue() > this.delay.getMax())
                this.delay.setValue(1000.0f);
        }
    }

    private static class TpsNode {

        public float size = 1.0f;
        public float tps = 0.0f;
        public Color color;

        public TpsNode(float tps) {
            this.tps = tps;

            int colorR = (int) MathUtil.map(tps, 0, 20, 255, 0);
            int colorG = (int) MathUtil.map(tps, 0, 20, 0, 255);
            int colorB = 0;
            this.color = new Color(colorR, colorG, colorB);
        }
    }
}
