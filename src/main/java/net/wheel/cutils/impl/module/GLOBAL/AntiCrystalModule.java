package net.wheel.cutils.impl.module.GLOBAL;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemChorusFruit;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventSendPacket;
import net.wheel.cutils.api.event.player.EventUpdateWalkingPlayer;
import net.wheel.cutils.api.event.world.EventLoadWorld;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.task.hand.HandSwapContext;
import net.wheel.cutils.api.task.rotation.RotationTask;
import net.wheel.cutils.api.util.InventoryUtil;
import net.wheel.cutils.api.util.MathUtil;
import net.wheel.cutils.api.util.Timer;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.module.LOCAL.FreecamModule;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AntiCrystalModule extends Module {

    public final Value<Boolean> extended = new Value<Boolean>("Extended", new String[] { "extend", "e", "big" },
            "Enlarges the size of the fortress", false);
    public final Value<Boolean> visible = new Value<Boolean>("Visible", new String[] { "Visible", "v" },
            "Casts a ray to the placement position, forces the placement when disabled", true);
    public final Value<Boolean> rotate = new Value<Boolean>("Rotate", new String[] { "rotation", "r", "rotate" },
            "Rotate to place blocks", true);
    public final Value<Boolean> swing = new Value<Boolean>("Swing", new String[] { "Arm" },
            "Swing the player's arm while placing blocks", true);
    public final Value<Boolean> center = new Value<Boolean>("Center", new String[] { "centered", "c", "cen" },
            "Centers the player on their current block when beginning to place", true);
    public final Value<Boolean> disable = new Value<Boolean>("Disable",
            new String[] { "dis", "autodisable", "autodis", "d" }, "Disable after obsidian is placed", false);
    public final Value<Boolean> sneak = new Value<Boolean>("PlaceOnSneak",
            new String[] { "sneak", "s", "pos", "sneakPlace" },
            "When true, NoCrystal will only place while the player is sneaking", false);
    public final Value<Float> range = new Value<Float>("Range", new String[] { "MaxRange", "MaximumRange" },
            "The maximum block reaching range to continue building in", 6.0f, 1.0f, 10.0f, 0.5f);
    public final Value<Float> placeDelay = new Value<Float>("Delay", new String[] { "PlaceDelay", "PlaceDel" },
            "The delay(ms) between obsidian blocks being placed", 100.0f, 0.0f, 500.0f, 1.0f);
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Timer placeTimer = new Timer();
    private final Timer chorusTpTimer = new Timer();
    private final RotationTask rotationTask = new RotationTask("NoCrystalTask", 8);

    private FreecamModule freeCamModule = null;

    public AntiCrystalModule() {
        super("AntiCrystal", new String[] { "AntiCrystal", "FeetPlace", "Surround" },
                "Automatically places obsidian around you to reduce crystal damage", "NONE", -1, ModuleType.GLOBAL);
        this.placeTimer.reset();
        this.chorusTpTimer.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        crack.INSTANCE.getRotationManager().finishTask(this.rotationTask);
    }

    @Listener
    public void onPacketSend(EventSendPacket event) {
        if (event.getStage().equals(EventStageable.EventStage.PRE)) {
            if (event.getPacket() instanceof CPacketPlayerTryUseItem) {
                final CPacketPlayerTryUseItem packetPlayerTryUseItem = (CPacketPlayerTryUseItem) event.getPacket();
                if (packetPlayerTryUseItem.hand == EnumHand.MAIN_HAND) {
                    if (mc.player.getHeldItemMainhand().getItem() instanceof ItemChorusFruit) {
                        this.chorusTpTimer.reset();
                    }
                }
            }
        }
    }

    @Listener
    public void onWalkingUpdate(EventUpdateWalkingPlayer event) {
        if (event.getStage() != EventStageable.EventStage.PRE)
            return;

        if (freeCamModule != null && freeCamModule.isEnabled())
            return;

        final Vec3d pos = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
        final float playerSpeed = (float) MathUtil.getDistance(pos, mc.player.posX, mc.player.posY, mc.player.posZ);

        if (!mc.player.onGround || playerSpeed > 0.005f)
            return;

        final BlockPos interpolatedPos = new BlockPos(pos.x, pos.y, pos.z);
        final BlockPos north = interpolatedPos.north();
        final BlockPos south = interpolatedPos.south();
        final BlockPos east = interpolatedPos.east();
        final BlockPos west = interpolatedPos.west();

        BlockPos[] surroundBlocks;
        if (this.extended.getValue()) {

            surroundBlocks = new BlockPos[] { north.down(), south.down(), east.down(), west.down(),
                    north, south, east, west, north.east(), north.west(), south.east(), south.west(),
                    north.north(), south.south(), east.east(), west.west() };
        } else {

            surroundBlocks = new BlockPos[] { north.down(), south.down(), east.down(), west.down(),
                    north, south, east, west };
        }

        final List<BlockPos> blocksToPlace = Lists.newArrayListWithCapacity(16);

        for (int i = 0; i < surroundBlocks.length; i++) {
            BlockPos blockPos = surroundBlocks[i];
            if (!this.valid(blockPos))
                continue;

            blocksToPlace.add(blockPos);
        }

        if (!blocksToPlace.isEmpty()) {
            final HandSwapContext handSwapContext = new HandSwapContext(
                    mc.player.inventory.currentItem, InventoryUtil.findObsidianInHotbar(mc.player));

            if (handSwapContext.getNewSlot() == -1) {
                crack.INSTANCE.getRotationManager().finishTask(this.rotationTask);
                return;
            }

            if (!mc.player.isSneaking() && this.sneak.getValue()) {
                if (this.rotationTask.isOnline()) {
                    crack.INSTANCE.getRotationManager().finishTask(this.rotationTask);
                }
                return;
            }

            crack.INSTANCE.getRotationManager().startTask(this.rotationTask);
            if (this.rotationTask.isOnline()) {

                handSwapContext.handleHandSwap(false, mc);

                if (this.center.getValue() && this.chorusTpTimer.passed(1000)) {
                    final double[] newPos = { Math.floor(mc.player.posX) + 0.5d, mc.player.posY,
                            Math.floor(mc.player.posZ) + 0.5d };
                    final CPacketPlayer.Position middleOfPos = new CPacketPlayer.Position(newPos[0], newPos[1],
                            newPos[2], mc.player.onGround);
                    if (!mc.world.isAirBlock(new BlockPos(newPos[0], newPos[1], newPos[2]).down())) {
                        if (mc.player.posX != middleOfPos.x && mc.player.posZ != middleOfPos.z) {
                            mc.player.connection.sendPacket(middleOfPos);
                            mc.player.setPosition(newPos[0], newPos[1], newPos[2]);
                        }
                    }
                }

                for (BlockPos blockPos : blocksToPlace) {
                    if (!this.valid(blockPos))
                        continue;

                    if (this.placeDelay.getValue() <= 0.0f) {
                        this.place(blockPos);
                    } else if (placeTimer.passed(this.placeDelay.getValue())) {
                        this.place(blockPos);
                        this.placeTimer.reset();
                    }
                }

                handSwapContext.handleHandSwap(true, mc);
            }
        } else {
            crack.INSTANCE.getRotationManager().finishTask(this.rotationTask);
        }

        if (this.disable.getValue()) {
            if (blocksToPlace.isEmpty())
                this.toggle();
        }
    }

    @Listener
    public void onLoadWorld(EventLoadWorld event) {
        if (event.getWorld() != null) {
            freeCamModule = (FreecamModule) crack.INSTANCE.getModuleManager().find(FreecamModule.class);
        }
    }

    private boolean valid(BlockPos pos) {

        if (!mc.world.checkNoEntityCollision(new AxisAlignedBB(pos)))
            return false;

        if (mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ()) > this.range.getValue())
            return false;

        final Block block = mc.world.getBlockState(pos).getBlock();
        return block.isReplaceable(mc.world, pos) && !(block == Blocks.OBSIDIAN) && !(block == Blocks.BEDROCK);
    }

    private void place(BlockPos pos) {
        final Block block = mc.world.getBlockState(pos).getBlock();

        final EnumFacing direction = MathUtil.calcSide(pos);
        if (direction == null)
            return;

        final boolean activated = block.onBlockActivated(mc.world, pos, mc.world.getBlockState(pos), mc.player,
                EnumHand.MAIN_HAND, direction, 0, 0, 0);

        if (activated)
            mc.player.connection
                    .sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

        final EnumFacing otherSide = direction.getOpposite();
        final BlockPos sideOffset = pos.offset(direction);

        if (this.rotate.getValue()) {
            final float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()),
                    new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f));
            crack.INSTANCE.getRotationManager().setPlayerRotations(angle[0], angle[1]);
        }

        if (!this.visible.getValue()) {
            mc.player.connection.sendPacket(
                    new CPacketPlayerTryUseItemOnBlock(sideOffset, otherSide, EnumHand.MAIN_HAND, 0.5F, 0.5F, 0.5F));
            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, sideOffset, otherSide,
                    new Vec3d(0.5F, 0.5F, 0.5F), EnumHand.MAIN_HAND);

            if (this.swing.getValue()) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }
        }

        if (activated)
            mc.player.connection
                    .sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
    }
}
