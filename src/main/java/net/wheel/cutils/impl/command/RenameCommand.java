package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;

public final class RenameCommand extends Command {

    public RenameCommand() {
        super("Rename", new String[] { "Ren" },
                "Allows you to rename your held item while in creative mode (supports color codes)", "Rename <Name>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2)) {
            this.printUsage();
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();

        if (!mc.player.isCreative()) {
            crack.INSTANCE.errorChat("Creative mode is required to use this command");
            return;
        }

        final ItemStack itemStack = mc.player.getHeldItemMainhand();

        if (itemStack.isEmpty()) {
            crack.INSTANCE.errorChat("Please hold an item in your main hand to enchant");
            return;
        }

        final String[] split = input.split(" ");

        final StringBuilder sb = new StringBuilder();

        final int size = split.length;

        for (int i = 1; i < size; i++) {
            final String arg = split[i];
            sb.append(arg).append((i == size - 1) ? "" : " ");
        }

        final String name = sb.toString().replace("&", "\247");

        NBTTagCompound tagCompound = itemStack.getTagCompound();

        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
            itemStack.setTagCompound(tagCompound);
        }

        itemStack.getOrCreateSubCompound("display").setString("Name", name);

        mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(mc.player.inventory.currentItem, itemStack));
        crack.INSTANCE.logChat("Renamed your item to " + name);
    }
}
