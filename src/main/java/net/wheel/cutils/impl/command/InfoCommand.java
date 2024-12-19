package net.wheel.cutils.impl.command;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;

public final class InfoCommand extends Command {

    public InfoCommand() {
        super("Info",
                new String[] { "info" },
                "Displays information about the held item or targeted block",
                ".info <hand | block>",
                new ObjectArrayList<>(new String[] { "hand", "block" }));
    }

    @Override
    public void exec(String input) {
        final Minecraft mc = Minecraft.getMinecraft();
        final ItemStack itemStack = mc.player.getHeldItemMainhand();

        if (input.trim().equalsIgnoreCase("info hand")) {
            if (!itemStack.isEmpty()) {
                displayItemInfo(itemStack);
            } else {
                crack.INSTANCE.errorChat("No item in hand to display info.");
            }
        } else if (input.trim().equalsIgnoreCase("info block")) {
            RayTraceResult rayTraceResult = mc.objectMouseOver;
            if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos blockPos = rayTraceResult.getBlockPos();
                Block block = mc.world.getBlockState(blockPos).getBlock();
                displayBlockInfo(block);
            } else {
                crack.INSTANCE.errorChat("No block in view to display info.");
            }
        } else {
            crack.INSTANCE.errorChat("Specify 'hand' or 'block' to get info.");
            this.printUsage();
        }
    }

    private void displayItemInfo(ItemStack itemStack) {
        ResourceLocation itemResourceLocation = itemStack.getItem().getRegistryName();
        if (itemResourceLocation == null) {
            crack.INSTANCE.errorChat("Error: No item info available");
            return;
        }

        String itemName = itemStack.getDisplayName();
        String itemId = itemResourceLocation.toString();
        String modId = itemResourceLocation.getNamespace();

        crack.INSTANCE.logChat("Item Name: \u00A7a" + itemName);
        crack.INSTANCE.logChat("Item ID: \u00A7a" + itemId);
        crack.INSTANCE.logChat("Mod ID: \u00A7a" + modId);
        crack.INSTANCE.logChat("Channel: \u00A7a" + getModChannel(modId));
    }

    private void displayBlockInfo(Block block) {
        ResourceLocation blockResourceLocation = block.getRegistryName();
        if (blockResourceLocation == null) {
            crack.INSTANCE.errorChat("Error: No block info available");
            return;
        }

        String blockName = block.getLocalizedName();
        String blockId = blockResourceLocation.toString();
        String modId = blockResourceLocation.getNamespace();

        crack.INSTANCE.logChat("Block Name: \u00A7a" + blockName);
        crack.INSTANCE.logChat("Block ID: \u00A7a" + blockId);
        crack.INSTANCE.logChat("Mod ID: \u00A7a" + modId);
        crack.INSTANCE.logChat("Channel: \u00A7a" + getModChannel(modId));
    }

    private String getModChannel(String modId) {
        String foundChannel = checkChannelForModId(modId, Side.CLIENT);
        if (foundChannel == null) {
            foundChannel = checkChannelForModId(modId, Side.SERVER);
        }
        return foundChannel != null ? foundChannel : "None";
    }

    private String checkChannelForModId(String modId, Side side) {
        Set<String> channels = NetworkRegistry.INSTANCE.channelNamesFor(side);

        for (String channel : channels) {
            if (channel.toLowerCase().contains(modId.toLowerCase())) {
                return channel;
            }
        }
        return null;
    }

    @Override
    public ObjectArrayList<String> getCommandArgs(String[] args) {
        ObjectArrayList<String> suggestions = new ObjectArrayList<>();
        if (args.length == 1) {
            suggestions.add("hand");
            suggestions.add("block");
        }
        return suggestions;
    }
}
