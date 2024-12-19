package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;

public final class SkullCommand extends Command {

    public SkullCommand() {
        super("Skull", new String[] { "Skll" }, "Gives a player head item in creative mode", "Skull <Username>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 2)) {
            this.printUsage();
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();

        if (!mc.player.isCreative()) {
            crack.INSTANCE.errorChat("gm1 needed");
            return;
        }

        String username = input.split(" ")[1];
        ItemStack skullItem = createPlayerSkull(username);

        int slot = findEmptyHotbarSlot();
        mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(
                36 + (slot != -1 ? slot : mc.player.inventory.currentItem), skullItem));
        crack.INSTANCE.logChat("Gave you a skull of \u00A76" + username + "\u00A7f.");
    }

    private ItemStack createPlayerSkull(String username) {
        ItemStack skullItem = new ItemStack(Items.SKULL, 1, 3);
        NBTTagCompound skullTag = new NBTTagCompound();
        skullTag.setString("SkullOwner", username);
        skullItem.setTagCompound(skullTag);
        return skullItem;
    }

    private int findEmptyHotbarSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);
            if (stack.getItem() == Items.AIR) {
                return i;
            }
        }
        return -1;
    }
}
