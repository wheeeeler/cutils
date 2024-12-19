package net.wheel.cutils.impl.gui.hud.component;

import java.util.logging.Level;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import net.wheel.cutils.api.gui.hud.component.DraggableHudComponent;
import net.wheel.cutils.crack;

public final class ItemInfoComponent extends DraggableHudComponent {

    private static final int DEFAULT_HEIGHT = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * 4;

    public ItemInfoComponent() {
        super("Waila++");
        this.setW(200);
        this.setH(DEFAULT_HEIGHT);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (mc.player == null || mc.world == null)
            return;

        Object lookedAt = this.isRclicked() ? getLookingAt() : getHeldItem();

        if (lookedAt instanceof ItemStack) {
            ItemStack itemStack = (ItemStack) lookedAt;
            if (!itemStack.isEmpty()) {
                renderInfo(getItemInfo(itemStack));
            }
        } else if (lookedAt instanceof FluidStack) {
            FluidStack fluidStack = (FluidStack) lookedAt;
            renderInfo(getFluidInfo(fluidStack));
        } else {
            renderEmpty(this.isRclicked() ? "None" : "Empty");
        }
    }

    private ItemStack getHeldItem() {
        return mc.player.getHeldItemMainhand();
    }

    private Object getLookingAt() {
        RayTraceResult rayTraceResult = mc.objectMouseOver;

        if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos blockPos = rayTraceResult.getBlockPos();
            Block block = mc.world.getBlockState(blockPos).getBlock();

            try {
                if (mc.world.getBlockState(blockPos).getMaterial().isLiquid()) {
                    Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
                    if (fluid != null) {
                        return new FluidStack(fluid, 1000);
                    }
                }
                return block.getPickBlock(
                        mc.world.getBlockState(blockPos),
                        rayTraceResult,
                        mc.world,
                        blockPos,
                        mc.player);
            } catch (Exception e) {
                crack.INSTANCE.getLogger().log(Level.INFO, "Err: fetching block or fluid info: " + e.getMessage());
            }
        }

        return ItemStack.EMPTY;
    }

    private void renderInfo(String[] info) {
        int yOffset = 0;
        for (String line : info) {
            mc.fontRenderer.drawStringWithShadow(line, this.getX(), this.getY() + yOffset, -1);
            yOffset += mc.fontRenderer.FONT_HEIGHT;
        }
        this.setH(Math.max(yOffset, DEFAULT_HEIGHT));
    }

    private void renderEmpty(String message) {
        mc.fontRenderer.drawStringWithShadow(message, this.getX(), this.getY(), 0xFFAAAAAA);
        this.setH(DEFAULT_HEIGHT);
    }

    private String[] getItemInfo(ItemStack itemStack) {
        return formatInfo(
                itemStack.getDisplayName(),
                itemStack.getItem().getRegistryName());
    }

    private String[] getFluidInfo(FluidStack fluidStack) {
        Fluid fluid = fluidStack.getFluid();
        ResourceLocation fluidResourceLocation = FluidRegistry.getFluidName(fluid) != null
                ? new ResourceLocation(FluidRegistry.getFluidName(fluid))
                : null;

        return formatInfo(
                fluid.getLocalizedName(fluidStack),
                fluidResourceLocation);
    }

    private String[] formatInfo(String name, ResourceLocation resourceLocation) {
        String id = resourceLocation != null ? resourceLocation.toString() : "none";
        String modId = resourceLocation != null ? resourceLocation.getNamespace() : "none";
        String channel = getModChannel(modId);

        return new String[] {
                ChatFormatting.GRAY + "Name: " + ChatFormatting.AQUA + name,
                ChatFormatting.GRAY + "ID: " + ChatFormatting.AQUA + id,
                ChatFormatting.GRAY + "ModID: " + ChatFormatting.AQUA + modId,
                ChatFormatting.GRAY + "Channel: " + ChatFormatting.AQUA + channel
        };
    }

    private String getModChannel(String modId) {
        for (Side side : new Side[] { Side.CLIENT, Side.SERVER }) {
            for (String channel : NetworkRegistry.INSTANCE.channelNamesFor(side)) {
                if (channel.toLowerCase().contains(modId.toLowerCase())) {
                    return channel;
                }
            }
        }
        return "none";
    }
}
