package net.wheel.cutils.impl.module.RENDER;

import java.awt.*;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import net.wheel.cutils.api.event.render.EventRender2D;
import net.wheel.cutils.api.event.render.EventRender3D;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.util.ColorUtil;
import net.wheel.cutils.api.util.GLUProjection;
import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class ContainerESPModule extends Module {

    public final Value<Mode> mode = new Value<Mode>("Mode", new String[] { "Mode", "M" }, "Rendering mode",
            Mode.THREE_D);
    public final Value<Boolean> nametag = new Value<Boolean>("Nametag",
            new String[] { "Nametag", "Tag", "Tags", "Ntag", "name", "names" },
            "Renders the name of the drawn storage object", false);
    public final Value<Integer> opacity = new Value<Integer>("Opacity",
            new String[] { "Opacity", "Transparency", "Alpha" }, "Opacity of the rendered esp", 128, 0, 255, 1);
    public final Value<Boolean> tracer = new Value<Boolean>("Tracer", new String[] { "TracerLine", "trace", "line" },
            "Display a tracer line to each storage object", false);
    public final Value<Color> tracerColor = new Value<Color>("TracerColor",
            new String[] { "TracerColor", "TColor", "TC" }, "Edit the storage object tracer color",
            new Color(0, 0, 255));
    public final Value<Boolean> tracerStorageColor = new Value<Boolean>("TracerStorageColor",
            new String[] { "TracerStorageColor", "TStorageColor", "TSColor", "TStorageC", "TSC" },
            "Use the storage object's color as the tracer color", false);
    public final Value<Float> tracerWidth = new Value<Float>("TracerWidth",
            new String[] { "TracerWidth", "TWidth", "TW" }, "Pixel width of each tracer-line", 0.5f, 0.1f, 5.0f, 0.1f);
    public final Value<Integer> tracerAlpha = new Value<Integer>("TracerAlpha",
            new String[] { "TracerAlpha", "TAlpha", "TA", "TracerOpacity", "TOpacity", "TO" },
            "Alpha value for each drawn line", 255, 1, 255, 1);
    private final ICamera camera = new Frustum();

    public ContainerESPModule() {
        super("ContainerESP", new String[] { "StorageESP", "ChestFinder", "ChestESP" },
                "Highlights different types of storage entities", "NONE", -1, ModuleType.RENDER);
    }

    @Listener
    public void render2D(EventRender2D event) {
        if (this.mode.getValue() == Mode.THREE_D && !this.nametag.getValue())
            return;

        final Minecraft mc = Minecraft.getMinecraft();
        for (TileEntity te : mc.world.loadedTileEntityList) {
            if (te != null) {
                if (this.isTileStorage(te)) {
                    final AxisAlignedBB bb = this.boundingBoxForEnt(te);
                    if (bb != null) {
                        final float[] bounds = this.convertBounds(bb, event.getScaledResolution().getScaledWidth(),
                                event.getScaledResolution().getScaledHeight());
                        if (bounds != null) {
                            if (this.mode.getValue() == Mode.TWO_D) {

                                RenderUtil.drawOutlineRect(bounds[0], bounds[1], bounds[2], bounds[3], 1.5f,
                                        ColorUtil.changeAlpha(0xAA000000, this.opacity.getValue()));
                                RenderUtil.drawOutlineRect(bounds[0] - 0.5f, bounds[1] - 0.5f, bounds[2] + 0.5f,
                                        bounds[3] + 0.5f, 0.5f, this.getBoxColor(te));
                            }

                            if (this.nametag.getValue()) {
                                final String name = te.getBlockType().getLocalizedName();
                                GL11.glEnable(GL11.GL_BLEND);
                                mc.fontRenderer.drawStringWithShadow(name,
                                        bounds[0] + (bounds[2] - bounds[0]) / 2
                                                - mc.fontRenderer.getStringWidth(name) / 2,
                                        bounds[1] + (bounds[3] - bounds[1]) - mc.fontRenderer.FONT_HEIGHT - 1,
                                        ColorUtil.changeAlpha(0xFFFFFFFF, this.opacity.getValue()));
                                GL11.glDisable(GL11.GL_BLEND);
                            }
                        }

                        if (this.tracer.getValue()) {
                            final GLUProjection.Projection projection = GLUProjection.getInstance().project(
                                    (bb.minX + bb.maxX) / 2, (bb.minY + bb.maxY) / 2, (bb.minZ + bb.maxZ) / 2,
                                    GLUProjection.ClampMode.NONE, true);
                            RenderUtil.drawLine((float) projection.getX(), (float) projection.getY(),
                                    event.getScaledResolution().getScaledWidth() / 2.0f,
                                    event.getScaledResolution().getScaledHeight() / 2.0f, this.tracerWidth.getValue(),
                                    this.getTracerColor(te));
                        }
                    }
                }
            }
        }
    }

    @Listener
    public void render3D(EventRender3D event) {
        if (this.mode.getValue() == Mode.THREE_D) {
            final Minecraft mc = Minecraft.getMinecraft();
            if (mc.getRenderViewEntity() == null)
                return;

            RenderUtil.begin3D();
            for (TileEntity te : mc.world.loadedTileEntityList) {
                if (te != null) {
                    if (this.isTileStorage(te)) {
                        final AxisAlignedBB bb = this.boundingBoxForEnt(te);
                        if (bb != null) {

                            if (this.tracer.getValue()) {

                                RenderUtil.updateModelViewProjectionMatrix();
                                final GLUProjection.Vector3D forward = GLUProjection.getInstance().getLookVector()
                                        .sadd(GLUProjection.getInstance().getCamPos());
                                RenderUtil.drawLine3D(forward.x, forward.y, forward.z, (bb.minX + bb.maxX) / 2,
                                        (bb.minY + bb.maxY) / 2, (bb.minZ + bb.maxZ) / 2, this.tracerWidth.getValue(),
                                        this.getTracerColor(te));
                            }

                            camera.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY,
                                    mc.getRenderViewEntity().posZ);

                            if (camera.isBoundingBoxInFrustum(
                                    new AxisAlignedBB(bb.minX + mc.getRenderManager().viewerPosX,
                                            bb.minY + mc.getRenderManager().viewerPosY,
                                            bb.minZ + mc.getRenderManager().viewerPosZ,
                                            bb.maxX + mc.getRenderManager().viewerPosX,
                                            bb.maxY + mc.getRenderManager().viewerPosY,
                                            bb.maxZ + mc.getRenderManager().viewerPosZ))) {
                                final int colorWithAlpha = this.getBoxColor(te);
                                RenderUtil.drawFilledBox(bb, colorWithAlpha);
                                RenderUtil.drawBoundingBox(bb, 1.5f, colorWithAlpha);
                            }
                        }
                    }
                }
            }
            RenderUtil.end3D();
        }
    }

    private boolean isTileStorage(TileEntity te) {
        if (te instanceof TileEntityChest) {
            return true;
        }
        if (te instanceof TileEntityDropper) {
            return true;
        }
        if (te instanceof TileEntityDispenser) {
            return true;
        }
        if (te instanceof TileEntityFurnace) {
            return true;
        }
        if (te instanceof TileEntityBrewingStand) {
            return true;
        }
        if (te instanceof TileEntityEnderChest) {
            return true;
        }
        if (te instanceof TileEntityHopper) {
            return true;
        }
        return te instanceof TileEntityShulkerBox;
    }

    private AxisAlignedBB boundingBoxForEnt(TileEntity te) {
        final Minecraft mc = Minecraft.getMinecraft();

        if (te != null) {
            if (te instanceof TileEntityChest) {
                TileEntityChest chest = (TileEntityChest) te;
                if (chest.adjacentChestXNeg != null) {
                    return new AxisAlignedBB(
                            te.getPos().getX() + 0.0625d - 1 - mc.getRenderManager().viewerPosX,
                            te.getPos().getY() - mc.getRenderManager().viewerPosY,
                            te.getPos().getZ() + 0.0625d - mc.getRenderManager().viewerPosZ,

                            te.getPos().getX() + 0.9375d - mc.getRenderManager().viewerPosX,
                            te.getPos().getY() + 0.875d - mc.getRenderManager().viewerPosY,
                            te.getPos().getZ() + 0.9375d - mc.getRenderManager().viewerPosZ);
                } else if (chest.adjacentChestZPos != null) {
                    return new AxisAlignedBB(
                            te.getPos().getX() + 0.0625d - mc.getRenderManager().viewerPosX,
                            te.getPos().getY() - mc.getRenderManager().viewerPosY,
                            te.getPos().getZ() + 0.0625d - mc.getRenderManager().viewerPosZ,

                            te.getPos().getX() + 0.9375d - mc.getRenderManager().viewerPosX,
                            te.getPos().getY() + 0.875d - mc.getRenderManager().viewerPosY,
                            te.getPos().getZ() + 0.9375d + 1 - mc.getRenderManager().viewerPosZ);
                } else if (chest.adjacentChestXPos == null && chest.adjacentChestZNeg == null) {
                    return new AxisAlignedBB(
                            te.getPos().getX() + 0.0625d - mc.getRenderManager().viewerPosX,
                            te.getPos().getY() - mc.getRenderManager().viewerPosY,
                            te.getPos().getZ() + 0.0625d - mc.getRenderManager().viewerPosZ,

                            te.getPos().getX() + 0.9375d - mc.getRenderManager().viewerPosX,
                            te.getPos().getY() + 0.875d - mc.getRenderManager().viewerPosY,
                            te.getPos().getZ() + 0.9375d - mc.getRenderManager().viewerPosZ);
                }
            } else if (te instanceof TileEntityEnderChest) {
                return new AxisAlignedBB(
                        te.getPos().getX() + 0.0625d - mc.getRenderManager().viewerPosX,
                        te.getPos().getY() - mc.getRenderManager().viewerPosY,
                        te.getPos().getZ() + 0.0625d - mc.getRenderManager().viewerPosZ,

                        te.getPos().getX() + 0.9375d - mc.getRenderManager().viewerPosX,
                        te.getPos().getY() + 0.875d - mc.getRenderManager().viewerPosY,
                        te.getPos().getZ() + 0.9375d - mc.getRenderManager().viewerPosZ);
            } else {
                return new AxisAlignedBB(
                        te.getPos().getX() - mc.getRenderManager().viewerPosX,
                        te.getPos().getY() - mc.getRenderManager().viewerPosY,
                        te.getPos().getZ() - mc.getRenderManager().viewerPosZ,

                        te.getPos().getX() + 1 - mc.getRenderManager().viewerPosX,
                        te.getPos().getY() + 1 - mc.getRenderManager().viewerPosY,
                        te.getPos().getZ() + 1 - mc.getRenderManager().viewerPosZ);
            }
        }

        return null;
    }

    private int getBaseColor(TileEntity te) {
        if (te instanceof TileEntityChest) {
            return 0xFFFFC417;
        }
        if (te instanceof TileEntityDropper) {
            return 0xFF4E4E4E;
        }
        if (te instanceof TileEntityDispenser) {
            return 0xFF4E4E4E;
        }
        if (te instanceof TileEntityHopper) {
            return 0xFF4E4E4E;
        }
        if (te instanceof TileEntityFurnace) {
            return 0xFF2D2D2D;
        }
        if (te instanceof TileEntityBrewingStand) {
            return 0xFF17B9D2;
        }
        if (te instanceof TileEntityEnderChest) {
            return 0xFF17A25C;
        }
        if (te instanceof TileEntityShulkerBox) {
            final TileEntityShulkerBox shulkerBox = (TileEntityShulkerBox) te;
            return (255 << 24) | shulkerBox.getColor().getColorValue();
        }
        return 0xFFFFFFFF;
    }

    private int getBoxColor(TileEntity te) {
        return ColorUtil.changeAlpha(this.getBaseColor(te), this.opacity.getValue());
    }

    private int getTracerColor(TileEntity te) {
        int baseColor;
        if (this.tracerStorageColor.getValue()) {
            baseColor = this.getBaseColor(te);
        } else {
            baseColor = this.tracerColor.getValue().getRGB();
        }

        return ColorUtil.changeAlpha(baseColor, this.tracerAlpha.getValue());
    }

    private float[] convertBounds(AxisAlignedBB bb, int width, int height) {
        float x = -1;
        float y = -1;
        float w = width + 1;
        float h = height + 1;

        camera.setPosition(Minecraft.getMinecraft().getRenderViewEntity().posX,
                Minecraft.getMinecraft().getRenderViewEntity().posY,
                Minecraft.getMinecraft().getRenderViewEntity().posZ);

        if (!camera.isBoundingBoxInFrustum(
                new AxisAlignedBB(bb.minX + Minecraft.getMinecraft().getRenderManager().viewerPosX,
                        bb.minY + Minecraft.getMinecraft().getRenderManager().viewerPosY,
                        bb.minZ + Minecraft.getMinecraft().getRenderManager().viewerPosZ,
                        bb.maxX + Minecraft.getMinecraft().getRenderManager().viewerPosX,
                        bb.maxY + Minecraft.getMinecraft().getRenderManager().viewerPosY,
                        bb.maxZ + Minecraft.getMinecraft().getRenderManager().viewerPosZ))) {
            return null;
        }

        final Vec3d[] corners = {
                new Vec3d(bb.minX, bb.minY, bb.minZ),
                new Vec3d(bb.maxX, bb.maxY, bb.maxZ),
                new Vec3d(bb.minX, bb.maxY, bb.maxZ),
                new Vec3d(bb.minX, bb.minY, bb.maxZ),
                new Vec3d(bb.maxX, bb.minY, bb.maxZ),
                new Vec3d(bb.maxX, bb.minY, bb.minZ),
                new Vec3d(bb.maxX, bb.maxY, bb.minZ),
                new Vec3d(bb.minX, bb.maxY, bb.minZ)
        };

        for (Vec3d vec : corners) {
            final GLUProjection.Projection projection = GLUProjection.getInstance().project(vec.x, vec.y, vec.z,
                    GLUProjection.ClampMode.NONE, true);

            x = Math.max(x, (float) projection.getX());
            y = Math.max(y, (float) projection.getY());

            w = Math.min(w, (float) projection.getX());
            h = Math.min(h, (float) projection.getY());
        }

        if (x != -1 && y != -1 && w != width + 1 && h != height + 1) {
            return new float[] { x, y, w, h };
        }

        return null;
    }

    private enum Mode {
        TWO_D, THREE_D
    }

}
