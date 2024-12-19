package net.wheel.cutils.impl.module.GLOBAL;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import net.wheel.cutils.api.event.player.EventUpdateWalkingPlayer;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.task.rotation.RotationTask;
import net.wheel.cutils.api.util.EntityUtil;
import net.wheel.cutils.api.util.MathUtil;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AutoFarmModule extends Module {

    public final Value<Mode> mode = new Value<Mode>("Mode", new String[] { "Mode", "m" }, "The current farming mode",
            Mode.HARVEST);
    public final Value<Boolean> modeHarvestRClick = new Value<Boolean>("ModeHarvestRClick",
            new String[] { "HarvestRightClick", "HarvestRClick", "hrc", "mhrc" },
            "Should we right click instead of left clicking when harvesting? (Modpacks)", false);

    public final Value<Float> range = new Value<Float>("Range", new String[] { "Range", "Reach", "r" },
            "The range in blocks your player should reach to farm", 4.0f, 1.0f, 9.0f, 0.1f);
    public final Value<Boolean> rotate = new Value<Boolean>("Rotate", new String[] { "rot" },
            "Should we rotate the player's head when Auto-Farming?", true);
    private final RotationTask rotationTask = new RotationTask("AutoFarmTask", 3);
    private BlockPos currentBlockPos;

    public AutoFarmModule() {
        super("AutoFarmer", new String[] { "AutoFarm", "Farm", "AutoHoe", "AutoBoneMeal", "AutoPlant" },
                "Good ol' farming, just change the \"Mode\" value", "NONE", -1, ModuleType.GLOBAL);
    }

    @Listener
    public void onMotionUpdate(EventUpdateWalkingPlayer event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null)
            return;

        switch (event.getStage()) {
            case PRE:
                double r = this.range.getValue();
                for (double y = mc.player.posY + r; y > mc.player.posY - r; y -= 1.0D) {
                    for (double x = mc.player.posX - r; x < mc.player.posX + r; x += 1.0D) {
                        for (double z = mc.player.posZ - r; z < mc.player.posZ + r; z += 1.0D) {
                            BlockPos blockPos = new BlockPos(x, y, z);
                            if (this.isBlockValid(blockPos, mc)) {
                                if (this.currentBlockPos == null) {
                                    if (this.rotate.getValue()) {
                                        crack.INSTANCE.getRotationManager().startTask(this.rotationTask);
                                        if (this.rotationTask.isOnline()) {
                                            final float[] angle = MathUtil.calcAngle(
                                                    mc.player.getPositionEyes(mc.getRenderPartialTicks()),
                                                    new Vec3d(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f,
                                                            blockPos.getZ() + 0.5f));
                                            crack.INSTANCE.getRotationManager().setPlayerRotations(angle[0], angle[1]);
                                            this.currentBlockPos = blockPos;
                                        }
                                    } else {
                                        this.currentBlockPos = blockPos;
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case POST:
                if (this.currentBlockPos != null) {
                    if (this.rotate.getValue()) {
                        if (this.rotationTask.isOnline()) {
                            this.doFarming(mc);
                        }
                    } else {
                        this.doFarming(mc);
                    }
                    this.currentBlockPos = null;
                } else {
                    if (this.rotationTask.isOnline()) {
                        crack.INSTANCE.getRotationManager().finishTask(this.rotationTask);
                    }
                }
                break;
        }

    }

    private void doFarming(final Minecraft mc) {
        switch (mode.getValue()) {
            case HARVEST:
                if (!this.modeHarvestRClick.getValue()) {
                    if (mc.playerController.onPlayerDamageBlock(currentBlockPos,
                            EntityUtil.getFacingDirectionToPosition(currentBlockPos))) {
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                    }
                    break;
                }
            case PLANT:
            case HOE:
            case BONEMEAL:
                mc.playerController.processRightClickBlock(mc.player, mc.world, currentBlockPos,
                        EntityUtil.getFacingDirectionToPosition(currentBlockPos), new Vec3d(currentBlockPos.getX() / 2F,
                                currentBlockPos.getY() / 2F, currentBlockPos.getZ() / 2F),
                        EnumHand.MAIN_HAND);
                break;
        }
    }

    private boolean isBlockValid(BlockPos position, final Minecraft mc) {
        boolean temp = false;
        Block block = mc.world.getBlockState(position).getBlock();
        switch (mode.getValue()) {
            case PLANT:
                if (mc.player.getHeldItemMainhand().getItem() == Items.NETHER_WART) {
                    if (block instanceof BlockSoulSand) {
                        if (mc.world.getBlockState(position.up()).getBlock() == Blocks.AIR) {
                            temp = true;
                        }
                    }
                }
                if (mc.player.getHeldItemMainhand().getItem() == Items.REEDS) {
                    if (block instanceof BlockGrass || block instanceof BlockDirt || block instanceof BlockSand) {
                        if (mc.world.getBlockState(position.up()).getBlock() == Blocks.AIR) {
                            for (EnumFacing side : EnumFacing.Plane.HORIZONTAL) {
                                IBlockState blockState = mc.world.getBlockState(position.offset(side));
                                if (blockState.getMaterial() == Material.WATER
                                        || blockState.getBlock() == Blocks.FROSTED_ICE) {
                                    temp = true;
                                }
                            }
                        }
                    }
                }
                if (mc.player.getHeldItemMainhand().getItem() instanceof ItemSeeds
                        || mc.player.getHeldItemMainhand().getItem() instanceof ItemSeedFood) {
                    if (block instanceof BlockFarmland) {
                        if (mc.world.getBlockState(position.up()).getBlock() == Blocks.AIR) {
                            temp = true;
                        }
                    }
                }
                break;
            case HARVEST:
                if ((block instanceof BlockTallGrass) || (block instanceof BlockFlower)
                        || (block instanceof BlockDoublePlant)) {
                    temp = true;
                } else if (block instanceof BlockCrops) {
                    BlockCrops crops = (BlockCrops) block;
                    if (crops.getMetaFromState(mc.world.getBlockState(position)) == 7) {
                        temp = true;
                    }
                } else if (block instanceof BlockNetherWart) {
                    BlockNetherWart netherWart = (BlockNetherWart) block;
                    if (netherWart.getMetaFromState(mc.world.getBlockState(position)) == 3) {
                        temp = true;
                    }
                } else if (block instanceof BlockReed) {
                    if (mc.world.getBlockState(position.down()).getBlock() instanceof BlockReed) {
                        temp = true;
                    }
                } else if (block instanceof BlockCactus) {
                    if (mc.world.getBlockState(position.down()).getBlock() instanceof BlockCactus) {
                        temp = true;
                    }
                } else if (block instanceof BlockPumpkin) {
                    temp = true;
                } else if (block instanceof BlockMelon) {
                    temp = true;
                } else if (block instanceof BlockChorusFlower) {
                    BlockChorusFlower chorusFlower = (BlockChorusFlower) block;
                    if (chorusFlower.getMetaFromState(mc.world.getBlockState(position)) == 5) {
                        temp = true;
                    }
                }
                break;
            case HOE:
                if (!mc.player.getHeldItemMainhand().isEmpty()) {
                    if (mc.player.getHeldItemMainhand().getItem() instanceof ItemHoe) {
                        if (block == Blocks.DIRT || block == Blocks.GRASS) {
                            if (mc.world.getBlockState(position.up()).getBlock() == Blocks.AIR) {
                                temp = true;
                            }
                        }
                    }
                }
                break;
            case BONEMEAL:
                if (mc.player.getHeldItemMainhand().getItem() instanceof ItemDye) {
                    EnumDyeColor enumdyecolor = EnumDyeColor.byDyeDamage(mc.player.getHeldItemMainhand().getMetadata());
                    if (enumdyecolor == EnumDyeColor.WHITE) {
                        if (block instanceof BlockCrops) {
                            BlockCrops crops = (BlockCrops) block;
                            if (crops.getMetaFromState(mc.world.getBlockState(position)) < 7) {
                                temp = true;
                            }
                        }
                    }
                }
                break;

        }

        return temp
                && mc.player.getDistance(position.getX(), position.getY(), position.getZ()) <= this.range.getValue();
    }

    private enum Mode {
        PLANT,
        HARVEST,
        HOE,
        BONEMEAL
    }
}
