package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.util.StringUtil;
import net.wheel.cutils.crack;

public final class StackSizeCommand extends Command {

    public StackSizeCommand() {
        super("StackSize", new String[] { "SS" },
                "Allows you to change your held item stack size while in creative mode", "StackSize <Amount>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 2)) {
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

        if (StringUtil.isInt(split[1])) {
            final int num = Integer.parseInt(split[1]);
            itemStack.setCount(num);
            itemStack.getItem().updateItemStackNBT(itemStack.getTagCompound());
            crack.INSTANCE.logChat("Set your stack size to " + num);
        } else {
            crack.INSTANCE.errorChat("Unknown number " + "\247f\"" + split[1] + "\"");
        }
    }
}
