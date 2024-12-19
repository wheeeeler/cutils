package net.wheel.cutils.impl.module.MVMT;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.player.EventUpdateWalkingPlayer;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class StepModule extends Module {

    public final Value<Mode> mode = new Value<>("Mode", new String[] { "Mode", "M" }, "mode", Mode.ONE);
    private final Minecraft mc = Minecraft.getMinecraft();
    private final double[] oneblockPositions = { 0.42D, 0.75D };
    private final double[] twoblockPositions = { 0.4D, 0.75D, 0.5D, 0.41D, 0.83D, 1.16D, 1.41D, 1.57D, 1.58D, 1.42D };
    private double[] selectedPositions = new double[0];

    public StepModule() {
        super("StepModule", new String[] { "stp" }, "steb", "NONE", -1, ModuleType.MVMT);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.player.stepHeight = 0.6F;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.selectedPositions = this.mode.getValue() == Mode.ONE ? this.oneblockPositions : this.twoblockPositions;
    }

    @Listener
    public void onWalkingUpdate(EventUpdateWalkingPlayer event) {
        if (event.getStage() == EventStageable.EventStage.PRE && nikeAirmax()) {
            this.selectedPositions = this.mode.getValue() == Mode.ONE ? this.oneblockPositions : this.twoblockPositions;

            double height = getStepHeight();
            if (height > 0 && height <= (this.mode.getValue() == Mode.ONE ? 1 : 2)) {
                bigSteppa(height);
            }
        }
    }

    private boolean nikeAirmax() {
        return mc.player.collidedHorizontally && mc.player.onGround && !mc.player.isInsideOfMaterial(Material.WATER)
                && !mc.player.isInsideOfMaterial(Material.LAVA) && !mc.player.isInWeb && mc.player.collidedVertically
                && mc.player.fallDistance == 0 && !mc.gameSettings.keyBindJump.isKeyDown() && !mc.player.isOnLadder();
    }

    private double getStepHeight() {
        AxisAlignedBB bb = mc.player.getEntityBoundingBox();
        double maxHeight = -1;

        for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0D); x++) {
            for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0D); z++) {
                BlockPos pos = new BlockPos(x, bb.maxY + 1, z);
                Block block = mc.world.getBlockState(pos).getBlock();

                if (!(block instanceof BlockAir)) {
                    double height = mc.world.getBlockState(pos).getBoundingBox(mc.world, pos).maxY - mc.player.posY;
                    if (height > maxHeight) {
                        maxHeight = height;
                    }
                }
            }
        }
        return maxHeight;
    }

    private void bigSteppa(double height) {
        if (height == 1.0) {
            for (double pos : this.oneblockPositions) {
                mc.player.connection.sendPacket(
                        new CPacketPlayer.Position(mc.player.posX, mc.player.posY + pos, mc.player.posZ, true));
            }
        } else if (height == 2.0) {
            for (double pos : this.twoblockPositions) {
                mc.player.connection.sendPacket(
                        new CPacketPlayer.Position(mc.player.posX, mc.player.posY + pos, mc.player.posZ, true));
            }
        }
        mc.player.setPosition(mc.player.posX, mc.player.posY + height, mc.player.posZ);
    }

    private enum Mode {
        ONE, TWO
    }
}
