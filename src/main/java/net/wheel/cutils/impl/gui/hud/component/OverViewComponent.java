package net.wheel.cutils.impl.gui.hud.component;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import net.wheel.cutils.api.camera.Camera;
import net.wheel.cutils.api.gui.hud.component.ResizableHudComponent;
import net.wheel.cutils.api.gui.hud.component.SliderComponent;
import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.gui.hud.GuiHudEditor;

public final class OverViewComponent extends ResizableHudComponent {

    private final Camera overviewCamera = new Camera();

    private final Value<Float> zoom = new Value<>("Zoom", new String[] { "Z" }, "The zoom distance", 50.0f, 0.0f,
            100.0f, 1.0f);
    private final SliderComponent zoomSlider;

    public OverViewComponent() {
        super("TopView", 120, 120, 400, 400);
        crack.INSTANCE.getCameraManager().addCamera(overviewCamera);
        this.setW(120);
        this.setH(120);
        zoomSlider = new SliderComponent("Zoom", zoom);
        zoomSlider.setX(this.getX());
        zoomSlider.setY(this.getY() + this.getH() - 10);
        zoomSlider.setW(this.getW());
        zoomSlider.setH(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        RenderUtil.drawRect(this.getX() - 1, this.getY() - 1, this.getX() + this.getW() + 1,
                this.getY() + this.getH() + 1, 0x99101010);
        RenderUtil.drawRect(this.getX(), this.getY(), this.getX() + this.getW(), this.getY() + this.getH(), 0xFF202020);
        mc.fontRenderer.drawStringWithShadow(this.getName(), this.getX() + 2, this.getY() + 2, 0xFFFFFFFF);

        if (mc.player != null && mc.world != null) {
            this.overviewCamera.setRendering(true);

            if (this.overviewCamera.isValid()) {

                final Vec3d ground = this.getGround(partialTicks);

                if (ground != null) {

                    this.overviewCamera.setPos(ground.add(0, this.getDist(partialTicks), 0));
                    this.overviewCamera.setYaw(mc.player.rotationYaw);
                    this.overviewCamera.setPitch(90.0f);
                    this.overviewCamera.render(this.getX() + 2, this.getY() + 12, this.getX() + this.getW() - 2,
                            this.getY()
                                    + (mc.currentScreen instanceof GuiHudEditor ? this.getH() - 12 : this.getH() - 2));

                    if (mc.currentScreen instanceof GuiHudEditor) {
                        this.zoomSlider.setX(this.getX());
                        this.zoomSlider.setY(this.getY() + this.getH() - 10);
                        this.zoomSlider.setW(this.getW());
                        this.zoomSlider.setH(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);
                        this.zoomSlider.render(mouseX, mouseY, partialTicks);
                    }
                }
            }
        }

        RenderUtil.drawTriangle(this.getX() + this.getW() / 2, this.getY() + this.getH() / 2 + 5, 4, 0, 0x70101010);
        RenderUtil.drawTriangle(this.getX() + this.getW() / 2, this.getY() + this.getH() / 2 + 5 + 0.5f, 2.5f, 0,
                0xAAFFFFFF);
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int button) {
        final boolean insideDragZone = mouseY <= this.getY() + mc.fontRenderer.FONT_HEIGHT + 3
                || mouseY >= ((this.getY() + this.getH()) - CLICK_ZONE);

        if (insideDragZone) {
            super.mouseClick(mouseX, mouseY, button);
        }

        this.zoomSlider.mouseClick(mouseX, mouseY, button);
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int button) {
        super.mouseClickMove(mouseX, mouseY, button);
        this.zoomSlider.mouseClickMove(mouseX, mouseY, button);
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int button) {
        super.mouseRelease(mouseX, mouseY, button);
        this.zoomSlider.mouseRelease(mouseX, mouseY, button);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
        this.zoomSlider.keyTyped(typedChar, keyCode);
    }

    private Vec3d getGround(float partialTicks) {
        final Vec3d eyes = mc.player.getPositionEyes(partialTicks);
        final RayTraceResult ray = mc.world.rayTraceBlocks(eyes, eyes.subtract(0, 3, 0), false);

        if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
            return ray.hitVec;
        }

        return eyes;
    }

    private double getDist(float partialTicks) {
        final Vec3d eyes = mc.player.getPositionEyes(partialTicks);
        final RayTraceResult ray = mc.world.rayTraceBlocks(eyes, eyes.add(0, this.zoom.getValue(), 0), false);

        if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
            return mc.player.getDistance(ray.hitVec.x, ray.hitVec.y, ray.hitVec.z) - 4;
        }

        return this.zoom.getValue();
    }
}
