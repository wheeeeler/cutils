package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;

public final class EnchantCommand extends Command {

    public EnchantCommand() {
        super("Enchant", new String[] { "Ench" }, "Add enchants to held item",
                "Enchant <Enchantment / All> <Level / Max> ([true/false] Disable Curses)");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 3, 4)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        final String enchantToApply = split[1];
        final String levelToApply = split[2];

        if (enchantToApply != null && levelToApply != null) {
            final Minecraft mc = Minecraft.getMinecraft();

            if (!mc.player.isCreative()) {
                crack.INSTANCE.errorChat("gm1 needed");
                return;
            }

            final ItemStack itemStack = mc.player.getHeldItemMainhand();

            if (itemStack.isEmpty()) {
                crack.INSTANCE.errorChat("Hand empty");
                return;
            }

            NBTTagCompound tagCompound = itemStack.getTagCompound();

            if (tagCompound == null) {
                tagCompound = new NBTTagCompound();
                itemStack.setTagCompound(tagCompound);
            }

            if (!tagCompound.hasKey("ench", 9)) {
                tagCompound.setTag("ench", new NBTTagList());
            }

            NBTTagList enchantments = itemStack.getTagCompound().getTagList("ench", 10);

            boolean disableCurses = split.length > 3 && split[3].equalsIgnoreCase("true");

            boolean enchantmentApplied = false;
            for (Enchantment enchant : Enchantment.REGISTRY) {
                if (enchant == null)
                    continue;

                if (disableCurses && enchant.isCurse())
                    continue;

                final String enchantmentName = enchant.getTranslatedName(0).replaceAll(" ", "");

                if (enchantToApply.equalsIgnoreCase("all")
                        || enchantmentName.toLowerCase().startsWith(enchantToApply.toLowerCase())) {
                    final NBTTagCompound enchantmentCompound = new NBTTagCompound();
                    enchantmentCompound.setShort("id", (short) Enchantment.getEnchantmentID(enchant));
                    if (levelToApply.toLowerCase().startsWith("max")) {
                        enchantmentCompound.setShort("lvl", (short) enchant.getMaxLevel());
                    } else {
                        enchantmentCompound.setShort("lvl", Short.parseShort(levelToApply));
                    }
                    enchantments.appendTag(enchantmentCompound);
                    enchantmentApplied = true;
                }
            }

            if (!enchantmentApplied) {
                crack.INSTANCE.errorChat("No enchantment found for \"" + enchantToApply + "\".");
                return;
            }

            mc.player.connection
                    .sendPacket(new CPacketCreativeInventoryAction(mc.player.inventory.currentItem, itemStack));
            crack.INSTANCE.logChat("\u00A7aEnchantments have been set");
        }
    }

    @Override
    public ObjectArrayList<String> getCommandArgs(String[] args) {
        ObjectArrayList<String> suggestions = new ObjectArrayList<>();

        if (args.length == 1) {
            suggestions.add("all");
            for (Enchantment enchant : Enchantment.REGISTRY) {
                if (enchant != null) {
                    suggestions.add(enchant.getTranslatedName(0).replaceAll(" ", ""));
                }
            }
        } else if (args.length == 2) {
            suggestions.add("max");
            suggestions.add("1");
            suggestions.add("5");
            suggestions.add("10");
        } else if (args.length == 3) {
            suggestions.add("true");
            suggestions.add("false");
        }

        return suggestions;
    }
}
