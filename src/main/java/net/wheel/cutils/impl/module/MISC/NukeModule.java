package net.wheel.cutils.impl.module.MISC;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import lombok.Getter;

import net.wheel.cutils.api.event.player.EventUpdateWalkingPlayer;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.task.rotation.RotationTask;
import net.wheel.cutils.api.util.EntityUtil;
import net.wheel.cutils.api.util.MathUtil;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.module.GLOBAL.FastBreakModule;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class NukeModule extends Module {

    public final Value<Mode> mode = new Value<Mode>("Mode", new String[] { "M" }, "The way that nuker mines blocks",
            Mode.NORMAL);
    public final Value<Float> distance = new Value<Float>("Distance", new String[] { "Dist", "D", "Range", "R" },
            "Maximum distance in blocks the nuker will reach", 4.5f, 0.0f, 5.0f, 0.1f);
    public final Value<Boolean> fixed = new Value<Boolean>("FixedDistance", new String[] { "Fixed", "fdist", "F" },
            "Use vertical and horizontal distances in blocks instead of distances relative to the camera", false);
    public final Value<Boolean> flatten = new Value<Boolean>("Flatten", new String[] { "flat" },
            "Ensures nuker does not mine blocks below your feet", false);
    public final Value<Float> vDistance = new Value<Float>("VerticalDistance",
            new String[] { "Vertical", "vdist", "VD", "vrange", "VR" },
            "Maximum vertical distance in blocks the nuker will reach", 4.5f, 0.0f, 5.0f, 0.1f);
    public final Value<Float> hDistance = new Value<Float>("HorizontalDistance",
            new String[] { "Horizontal", "hist", "HD", "hrange", "HR" },
            "Maximum horizontal distance in blocks the nuker will reach", 3f, 0.0f, 5.0f, 0.1f);

    public final Value<Integer> timeout = new Value<Integer>("Timeout", new String[] { "TO, t" },
            "How long to wait (in ms) until trying to break a specific block again (PACKET Mode)", 1000, 0, 5000, 10);
    public final Value<Float> minMineSpeed = new Value<Float>("MinMineSpeed",
            new String[] { "Min", "Speed", "MineSpeed" },
            "How fast you should be able to mine a block for nuker to attempt to mine it (0-1, 0 to allow all blocks, 1 to only allow instantly minable blocks)",
            0.2f, 0f, 1.0f, 0.1f);

    public final Value<FilterMode> filterMode = new Value<FilterMode>("FilterMode", new String[] { "fm", "fmode" },
            "Controls how blocks should be checked against the filter", FilterMode.WHITE);
    @Getter
    public final Value<List<Block>> filter = new Value<List<Block>>("Filter", new String[] {},
            "Controls what block id's nuker will mine");

    private final RotationTask rotationTask = new RotationTask("NukerTask", 2);

    private BlockPos currentPos = null;
    private final Map<BlockPos, Long> attemptedBreaks = new HashMap<>();

    public NukeModule() {
        super("CrackNuker", new String[] { "Nuke" }, "Automatically mines blocks within reach", "NONE", -1,
                ModuleType.MISC);
    }

    @Override
    public void onToggle() {
        super.onToggle();
        this.filter.setValue(new ArrayList<>());
    }

    @Override
    public void onDisable() {
        super.onDisable();
        crack.INSTANCE.getRotationManager().finishTask(this.rotationTask);
    }

    @Override
    public String getMetaData() {
        return this.mode.getValue().name();
    }

    @Listener
    public void onWalkingUpdate(EventUpdateWalkingPlayer event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null)
            return;

        switch (event.getStage()) {
            case PRE:
                if (this.mode.getValue() == Mode.PACKET) {
                    List<BlockPos> blocks = getSortedBlocks();

                    for (BlockPos pos : blocks) {
                        if (shouldBreak(pos)) {
                            if (!this.attemptedBreaks.containsKey(pos)) {
                                mc.player.connection.sendPacket(new CPacketPlayerDigging(
                                        CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.NORTH));
                                mc.player.connection.sendPacket(new CPacketPlayerDigging(
                                        CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.NORTH));

                                this.attemptedBreaks.put(pos, System.currentTimeMillis());
                            }
                        }
                    }

                    List<BlockPos> toRemove = new ArrayList<>();
                    for (BlockPos pos : attemptedBreaks.keySet()) {
                        if (System.currentTimeMillis() - attemptedBreaks.get(pos) >= timeout.getValue()) {
                            toRemove.add(pos);
                        }
                    }

                    for (BlockPos pos : toRemove) {
                        attemptedBreaks.remove(pos);
                    }
                } else {
                    this.currentPos = this.getClosestBlock();

                    if (this.currentPos != null) {
                        crack.INSTANCE.getRotationManager().startTask(this.rotationTask);
                        if (this.rotationTask.isOnline()) {
                            final float[] angle = MathUtil.calcAngle(
                                    mc.player.getPositionEyes(mc.getRenderPartialTicks()),
                                    new Vec3d(this.currentPos.getX() + 0.5f, this.currentPos.getY() + 0.5f,
                                            this.currentPos.getZ() + 0.5f));
                            crack.INSTANCE.getRotationManager().setPlayerRotations(angle[0], angle[1]);
                        }
                    }
                }

                break;
            case POST:
                if (this.mode.getValue() == Mode.CREATIVE) {
                    if (mc.player.capabilities.isCreativeMode) {
                        for (BlockPos blockPos : getBoxIterable()) {
                            if (!shouldBreak(blockPos) || !mc.world.getBlockState(blockPos).isFullBlock())
                                continue;

                            final Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(),
                                    mc.player.posZ);
                            final Vec3d posVec = new Vec3d(blockPos).add(0.5f, 0.5f, 0.5f);
                            double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);

                            for (EnumFacing side : EnumFacing.values()) {
                                final Vec3d hitVec = posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5f));
                                double distanceSqHitVec = eyesPos.squareDistanceTo(hitVec);

                                if (distanceSqHitVec > 36)
                                    continue;

                                if (distanceSqHitVec >= distanceSqPosVec)
                                    continue;

                                final float[] rotations = EntityUtil.getRotations(hitVec.x, hitVec.y, hitVec.z);
                                crack.INSTANCE.getRotationManager().setPlayerRotations(rotations[0], rotations[1]);

                                if (mc.playerController.onPlayerDamageBlock(blockPos, side)) {
                                    mc.player.swingArm(EnumHand.MAIN_HAND);
                                }
                            }
                        }
                    }
                } else if (this.mode.getValue() == Mode.NORMAL) {
                    if (this.currentPos != null) {
                        if (this.rotationTask.isOnline()) {
                            if (FastBreakModule.autoPos != null) {
                                if (this.currentPos.equals(FastBreakModule.autoPos)) {
                                    return;
                                }
                            }

                            if (this.canBreak(this.currentPos)) {
                                mc.playerController.onPlayerDamageBlock(this.currentPos,
                                        mc.player.getHorizontalFacing());
                                mc.player.swingArm(EnumHand.MAIN_HAND);
                            }
                        }
                    } else {
                        crack.INSTANCE.getRotationManager().finishTask(this.rotationTask);
                    }
                }
                break;
        }
    }

    private boolean canBreak(BlockPos pos) {
        final IBlockState blockState = Minecraft.getMinecraft().world.getBlockState(pos);
        final Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, Minecraft.getMinecraft().world, pos) >= minMineSpeed.getValue();
    }

    private boolean shouldBreak(BlockPos pos) {
        final Minecraft mc = Minecraft.getMinecraft();

        boolean isFiltered = false;
        if (this.filterMode.getValue() != FilterMode.DISABLED) {

            isFiltered = this.filter.getValue().contains(mc.world.getBlockState(pos).getBlock())
                    ^ this.filterMode.getValue() == FilterMode.WHITE;
        }

        return mc.world.getBlockState(pos).getBlock() != Blocks.AIR
                && !(mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid)
                && this.canBreak(pos)
                && !pos.equals(FastBreakModule.autoPos)
                && !isFiltered;
    }

    private Iterable<BlockPos> getBoxIterable() {
        final Minecraft mc = Minecraft.getMinecraft();
        AxisAlignedBB bb;

        if (this.fixed.getValue()) {
            bb = new AxisAlignedBB(
                    (int) mc.player.posX - hDistance.getValue(),
                    (int) (this.flatten.getValue() ? mc.player.posY : mc.player.posY - vDistance.getValue()),
                    (int) mc.player.posZ - hDistance.getValue(),
                    (int) mc.player.posX + hDistance.getValue(),
                    (int) mc.player.posY + vDistance.getValue(),
                    (int) mc.player.posZ + hDistance.getValue());
        } else {
            bb = new AxisAlignedBB(
                    (int) mc.player.posX - distance.getValue(),
                    (int) (this.flatten.getValue() ? mc.player.posY : mc.player.posY - vDistance.getValue()),
                    (int) mc.player.posZ - distance.getValue(),
                    (int) mc.player.posX + distance.getValue(),
                    (int) mc.player.posY + distance.getValue(),
                    (int) mc.player.posZ + distance.getValue());
        }

        return BlockPos.getAllInBox((int) bb.minX, (int) bb.minY, (int) bb.minZ, (int) bb.maxX, (int) bb.maxY,
                (int) bb.maxZ);
    }

    private BlockPos getClosestBlock() {
        final Minecraft mc = Minecraft.getMinecraft();

        BlockPos closest = null;
        double closestDist = Double.POSITIVE_INFINITY;
        for (BlockPos pos : getBoxIterable()) {
            double dist = pos.distanceSqToCenter(mc.player.posX, mc.player.getEyeHeight(), mc.player.posZ);

            if (shouldBreak(pos)) {
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = pos;
                }
            }
        }

        return closest;
    }

    private List<BlockPos> getSortedBlocks() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        List<BlockPos> ret = new ArrayList<>();

        for (BlockPos pos : getBoxIterable()) {
            ret.add(new BlockPos(pos));
        }

        ret.sort(Comparator.comparingDouble(o -> o.distanceSqToCenter(player.posX, player.posY, player.posZ)));
        return ret;
    }

    public boolean contains(Block block) {
        return this.filter.getValue().contains(block);
    }

    public void add(int id) {
        final Block blockFromID = Block.getBlockById(id);
        if (!contains(blockFromID)) {
            this.filter.getValue().add(blockFromID);
        }
    }

    public void add(String name) {
        final Block blockFromName = Block.getBlockFromName(name);
        if (blockFromName != null) {
            if (!contains(blockFromName)) {
                this.filter.getValue().add(blockFromName);
            }
        }
    }

    public void remove(int id) {
        for (Block block : this.filter.getValue()) {
            final int blockID = Block.getIdFromBlock(block);
            if (blockID == id) {
                this.filter.getValue().remove(block);
                break;
            }
        }
    }

    public void remove(String name) {
        final Block blockFromName = Block.getBlockFromName(name);
        if (blockFromName != null) {
            if (contains(blockFromName)) {
                this.filter.getValue().remove(blockFromName);
            }
        }
    }

    public int clear() {
        final int count = this.filter.getValue().size();
        this.filter.getValue().clear();
        return count;
    }

    private enum Mode {
        NORMAL, PACKET, CREATIVE
    }

    private enum FilterMode {
        WHITE, BLACK, DISABLED
    }
}
