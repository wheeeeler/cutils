package net.wheel.cutils.impl.module.CRACK;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import net.wheel.cutils.api.event.player.EventUpdateWalkingPlayer;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.util.Timer;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AutoPeripheralModule extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final Value<Double> peripheralRadius = new Value<>("Range", new String[] { "PeripheralRadius" }, "Range",
            5.0, 0.0, 10.0, 0.01);
    private final Value<Double> peripheralCooldown = new Value<>("Cooldown", new String[] { "PeripheralCooldown" },
            "Cooldown", 5.0, 0.01, 10.0, 0.01);
    private final Timer timer = new Timer();

    public AutoPeripheralModule() {
        super("AutoPeripheral", new String[] { "Peripheral" }, "Peripheral module", "NONE", -1, ModuleType.CRACK);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        timer.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Listener
    public void onWalkingUpdate(EventUpdateWalkingPlayer event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        if (timer.passed(peripheralCooldown.getValue() * 1000)) {
            BlockPos playerPos = mc.player.getPosition();
            double radius = peripheralRadius.getValue();
            List<BlockPos> targetBlocks = findTargetBlocks(playerPos, (int) radius);

            if (!targetBlocks.isEmpty()) {
                for (BlockPos targetBlock : targetBlocks) {
                    sendRightClickPacket(targetBlock);
                }
                timer.reset();
            }
        }
    }

    private List<BlockPos> findTargetBlocks(BlockPos center, int radius) {
        List<BlockPos> targetBlocks = new ArrayList<>();
        int r = MathHelper.floor(radius);

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = center.add(x, y, z);
                    IBlockState state = mc.world.getBlockState(pos);
                    Block block = state.getBlock();
                    String blockName = Objects.requireNonNull(block.getRegistryName()).toString();

                    if ("computercraft:wired_modem_full".equals(blockName) || "computercraft:cable".equals(blockName)) {
                        TileEntity tileEntity = mc.world.getTileEntity(pos);
                        if (tileEntity != null) {
                            NBTTagCompound tileData = tileEntity.getUpdateTag();
                            if (tileData.hasKey("state") && tileData.getInteger("state") == 0) {
                                targetBlocks.add(pos);
                            }
                        }
                    }
                }
            }
        }
        return targetBlocks;
    }

    private void sendRightClickPacket(BlockPos pos) {
        if (mc.world != null && mc.player != null) {
            CPacketPlayerTryUseItemOnBlock packet = new CPacketPlayerTryUseItemOnBlock(
                    pos,
                    EnumFacing.UP,
                    EnumHand.MAIN_HAND,
                    0.5F,
                    0.5F,
                    0.5F);
            Objects.requireNonNull(mc.getConnection()).sendPacket(packet);
        }
    }
}
