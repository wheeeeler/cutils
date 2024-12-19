package net.wheel.cutils.impl.module.RENDER;

import static org.lwjgl.opengl.GL11.*;

import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;

import lombok.Getter;

import net.wheel.cutils.api.event.render.EventRender3D;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.util.MathUtil;
import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class TrajectoryModule extends Module {

    public final Value<Float> width = new Value<Float>("Width", new String[] { "W", "Width" },
            "Pixel width of the projectile path", 1.0f, 0.1f, 5.0f, 0.1f);
    public final Value<Color> color = new Value<Color>("PathColor", new String[] { "color", "c", "pc" },
            "Change the color of the predicted path", new Color(255, 255, 255));
    public final Value<Integer> alpha = new Value<Integer>("PathAlpha",
            new String[] { "opacity", "a", "o", "pa", "po" }, "Alpha value for the predicted path", 255, 1, 255, 1);
    private final Queue<Vec3d> flightPoint = new ConcurrentLinkedQueue<>();

    public TrajectoryModule() {
        super("Trajectory", new String[] { "Proj" }, "Projects the possible path of an entity that was fired", "NONE",
                -1, ModuleType.RENDER);
    }

    @Listener
    public void onRender(EventRender3D event) {
        final Minecraft mc = Minecraft.getMinecraft();

        ThrowableType throwingType = this.getTypeFromCurrentItem(mc.player);

        if (throwingType == ThrowableType.NONE) {
            return;
        }

        FlightPath flightPath = new FlightPath(mc.player, throwingType);

        while (!flightPath.isCollided()) {
            flightPath.onUpdate();

            flightPoint.offer(new Vec3d(flightPath.position.x - mc.getRenderManager().viewerPosX,
                    flightPath.position.y - mc.getRenderManager().viewerPosY,
                    flightPath.position.z - mc.getRenderManager().viewerPosZ));
        }

        final boolean bobbing = mc.gameSettings.viewBobbing;
        mc.gameSettings.viewBobbing = false;
        mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();

        RenderUtil.begin3D();
        glLineWidth(width.getValue());
        glEnable(GL32.GL_DEPTH_CLAMP);
        while (!flightPoint.isEmpty()) {
            bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            Vec3d head = flightPoint.poll();
            if (head == null)
                continue;

            bufferbuilder.pos(head.x, head.y, head.z)
                    .color(this.color.getValue().getRed() / 255.0f, this.color.getValue().getGreen() / 255.0f,
                            this.color.getValue().getBlue() / 255.0f, this.alpha.getValue() / 255.0f)
                    .endVertex();

            if (flightPoint.peek() != null) {
                Vec3d point = flightPoint.peek();
                bufferbuilder.pos(point.x, point.y, point.z)
                        .color(this.color.getValue().getRed() / 255.0f, this.color.getValue().getGreen() / 255.0f,
                                this.color.getValue().getBlue() / 255.0f, this.alpha.getValue() / 255.0f)
                        .endVertex();
            }

            tessellator.draw();
        }
        glDisable(GL32.GL_DEPTH_CLAMP);

        mc.gameSettings.viewBobbing = bobbing;
        mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);

        if (flightPath.collided) {
            final RayTraceResult hit = flightPath.target;
            AxisAlignedBB bb = null;

            if (hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                final BlockPos blockpos = hit.getBlockPos();
                final IBlockState iblockstate = mc.world.getBlockState(blockpos);

                if (iblockstate.getMaterial() != Material.AIR && mc.world.getWorldBorder().contains(blockpos)) {
                    final Vec3d interp = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
                    bb = iblockstate.getSelectedBoundingBox(mc.world, blockpos).grow(0.0020000000949949026D)
                            .offset(-interp.x, -interp.y, -interp.z);
                }
            } else if (hit.typeOfHit == RayTraceResult.Type.ENTITY && hit.entityHit != null) {
                final AxisAlignedBB entityBB = hit.entityHit.getEntityBoundingBox();
                bb = new AxisAlignedBB(entityBB.minX - mc.getRenderManager().viewerPosX,
                        entityBB.minY - mc.getRenderManager().viewerPosY,
                        entityBB.minZ - mc.getRenderManager().viewerPosZ,
                        entityBB.maxX - mc.getRenderManager().viewerPosX,
                        entityBB.maxY - mc.getRenderManager().viewerPosY,
                        entityBB.maxZ - mc.getRenderManager().viewerPosZ);
            }

            if (bb != null) {
                RenderUtil.drawBoundingBox(bb, width.getValue(), this.color.getValue().getRed() / 255.0f,
                        this.color.getValue().getGreen() / 255.0f, this.color.getValue().getBlue() / 255.0f,
                        this.alpha.getValue() / 255.0f);
            }
        }
        RenderUtil.end3D();
    }

    private ThrowableType getTypeFromCurrentItem(EntityPlayerSP player) {

        if (player.getHeldItemMainhand().isEmpty()) {
            return ThrowableType.NONE;
        }

        final ItemStack itemStack = player.getHeldItem(EnumHand.MAIN_HAND);

        switch (Item.getIdFromItem(itemStack.getItem())) {
            case 261:
                if (player.isHandActive())
                    return ThrowableType.ARROW;
                break;
            case 346:
                return ThrowableType.FISHING_ROD;
            case 438:
            case 441:
                return ThrowableType.POTION;
            case 384:
                return ThrowableType.EXPERIENCE;
            case 332:
            case 344:
            case 368:
                return ThrowableType.NORMAL;
            default:
                break;
        }

        return ThrowableType.NONE;
    }

    @Getter
    enum ThrowableType {

        NONE(0.0f, 0.0f),

        ARROW(1.5f, 0.05f),

        POTION(0.5f, 0.05f),

        EXPERIENCE(0.7F, 0.07f),

        FISHING_ROD(1.5f, 0.04f),

        NORMAL(1.5f, 0.03f);

        private final float velocity;
        private final float gravity;

        ThrowableType(float velocity, float gravity) {
            this.velocity = velocity;
            this.gravity = gravity;
        }

    }

    private final class FlightPath {
        private final EntityPlayerSP shooter;
        private final ThrowableType throwableType;
        private Vec3d position;
        private Vec3d motion;
        private float yaw;
        private float pitch;
        private AxisAlignedBB boundingBox;
        @Getter
        private boolean collided;
        private RayTraceResult target;

        FlightPath(EntityPlayerSP player, ThrowableType throwableType) {
            this.shooter = player;
            this.throwableType = throwableType;

            this.setLocationAndAngles(this.shooter.posX, this.shooter.posY + this.shooter.getEyeHeight(),
                    this.shooter.posZ,
                    this.shooter.rotationYaw, this.shooter.rotationPitch);

            Vec3d startingOffset = new Vec3d(MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * 0.16F, 0.1d,
                    MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * 0.16F);

            this.position = this.position.subtract(startingOffset);

            this.setPosition(this.position);

            this.motion = new Vec3d(
                    -MathHelper.sin(this.yaw / 180.0F * (float) Math.PI)
                            * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI),
                    -MathHelper.sin(this.pitch / 180.0F * (float) Math.PI),
                    MathHelper.cos(this.yaw / 180.0F * (float) Math.PI)
                            * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI));

            this.setThrowableHeading(this.motion, this.getInitialVelocity());
        }

        public void onUpdate() {

            Vec3d prediction = this.position.add(this.motion);

            RayTraceResult blockCollision = this.shooter.getEntityWorld().rayTraceBlocks(this.position, prediction,
                    this.throwableType == ThrowableType.FISHING_ROD, !this.collidesWithNoBoundingBox(), false);

            if (blockCollision != null) {
                prediction = blockCollision.hitVec;
            }

            this.onCollideWithEntity(prediction, blockCollision);

            if (this.target != null) {
                this.collided = true;

                this.setPosition(this.target.hitVec);
                return;
            }

            if (this.position.y <= 0.0d) {

                this.collided = true;
                return;
            }

            this.position = this.position.add(this.motion);
            float motionModifier = 0.99F;

            if (this.shooter.getEntityWorld().isMaterialInBB(this.boundingBox, Material.WATER)) {

                motionModifier = this.throwableType == ThrowableType.ARROW ? 0.6F : 0.8F;
            }

            if (this.throwableType == ThrowableType.FISHING_ROD) {
                motionModifier = 0.92f;
            }

            this.motion = MathUtil.mult(this.motion, motionModifier);

            this.motion = this.motion.subtract(0.0d, this.getGravityVelocity(), 0.0d);

            this.setPosition(this.position);
        }

        private boolean collidesWithNoBoundingBox() {
            switch (this.throwableType) {
                case FISHING_ROD:
                case NORMAL:
                    return true;
                default:
                    return false;
            }
        }

        private void onCollideWithEntity(Vec3d prediction, RayTraceResult blockCollision) {
            Entity collidingEntity = null;
            RayTraceResult collidingPosition = null;

            double currentDistance = 0.0d;

            List<Entity> collisionEntities = Minecraft.getMinecraft().world.getEntitiesWithinAABBExcludingEntity(
                    this.shooter,
                    this.boundingBox.expand(this.motion.x, this.motion.y, this.motion.z).grow(1.0D, 1.0D, 1.0D));

            for (Entity entity : collisionEntities) {

                if (!entity.canBeCollidedWith()) {
                    continue;
                }

                float collisionSize = entity.getCollisionBorderSize();
                AxisAlignedBB expandedBox = entity.getEntityBoundingBox().expand(collisionSize, collisionSize,
                        collisionSize);
                RayTraceResult objectPosition = expandedBox.calculateIntercept(this.position, prediction);

                if (objectPosition != null) {
                    double distanceTo = this.position.distanceTo(objectPosition.hitVec);

                    if (distanceTo < currentDistance || currentDistance == 0.0D) {
                        collidingEntity = entity;
                        collidingPosition = objectPosition;
                        currentDistance = distanceTo;
                    }
                }
            }

            if (collidingEntity != null) {

                this.target = new RayTraceResult(collidingEntity, collidingPosition.hitVec);
            } else {

                this.target = blockCollision;
            }
        }

        private float getInitialVelocity() {
            switch (this.throwableType) {

                case ARROW:

                    int useDuration = this.shooter.getHeldItem(EnumHand.MAIN_HAND).getItem().getMaxItemUseDuration(
                            this.shooter.getHeldItem(EnumHand.MAIN_HAND)) - this.shooter.getItemInUseCount();
                    float velocity = (float) useDuration / 20.0F;
                    velocity = (velocity * velocity + velocity * 2.0F) / 3.0F;
                    if (velocity > 1.0F) {
                        velocity = 1.0F;
                    }

                    return (velocity * 2.0f) * throwableType.getVelocity();
                default:
                    return throwableType.getVelocity();
            }
        }

        private float getGravityVelocity() {
            return throwableType.getGravity();
        }

        private void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
            this.position = new Vec3d(x, y, z);
            this.yaw = yaw;
            this.pitch = pitch;
        }

        private void setPosition(Vec3d position) {
            this.position = new Vec3d(position.x, position.y, position.z);

            double entitySize = (this.throwableType == ThrowableType.ARROW ? 0.5d : 0.25d) / 2.0d;

            this.boundingBox = new AxisAlignedBB(position.x - entitySize,
                    position.y - entitySize,
                    position.z - entitySize,
                    position.x + entitySize,
                    position.y + entitySize,
                    position.z + entitySize);
        }

        private void setThrowableHeading(Vec3d motion, float velocity) {

            this.motion = MathUtil.div(motion, (float) motion.length());

            this.motion = MathUtil.mult(this.motion, velocity);
        }

        public RayTraceResult getCollidingTarget() {
            return target;
        }
    }

}
