package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.util.ResourceLocation;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.util.StringUtil;
import net.wheel.cutils.crack;

public final class GiveCommand extends Command {

    public GiveCommand() {
        super("GiveR", new String[] { "Givr" }, "give", "GiveR <Item> <Amount> <Meta> [NBT]");
    }

    @Override
    public void exec(String input) {
        final String[] split = input.split(" ");

        if (!this.clamp(input, 2)) {
            this.printUsage();
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();

        if (!mc.player.isCreative()) {
            crack.INSTANCE.errorChat("gm1 needed");
            return;
        }

        final Item item = this.findItem(split[1]);

        if (item != null) {
            int amount = 1;
            int meta = 0;

            if (split.length >= 3 && StringUtil.isInt(split[2])) {
                amount = Integer.parseInt(split[2]);
            } else if (split.length >= 3) {
                crack.INSTANCE.errorChat("Unknown number \u00A7f\"" + split[2] + "\"");
            }

            if (split.length >= 4 && StringUtil.isInt(split[3])) {
                meta = Integer.parseInt(split[3]);
            } else if (split.length >= 4) {
                crack.INSTANCE.errorChat("Unknown number \u00A7f\"" + split[3] + "\"");
            }

            final ItemStack itemStack = new ItemStack(item, amount, meta);

            if (split.length >= 5) {
                final String s = this.buildString(split, 4);

                try {
                    itemStack.setTagCompound(JsonToNBT.getTagFromJson(s));
                } catch (NBTException e) {
                    crack.INSTANCE.errorChat("Invalid NBT data: " + e.getMessage());
                }
            }

            final int slot = this.findEmptyHotbarSlot();
            mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(
                    36 + (slot != -1 ? slot : mc.player.inventory.currentItem), itemStack));
            crack.INSTANCE.logChat("Cooked " + amount + " " + itemStack.getDisplayName());
        } else {
            final ResourceLocation similar = this.findSimilarItem(split[1]);

            if (similar != null) {
                crack.INSTANCE.errorChat("Unknown item \u00A7f\"" + split[1] + "\"");
            }
        }
    }

    private String buildString(String[] args, int startPos) {
        final StringBuilder sb = new StringBuilder();

        for (int i = startPos; i < args.length; ++i) {
            if (i > startPos) {
                sb.append(" ");
            }

            sb.append(args[i]);
        }

        return sb.toString();
    }

    private ResourceLocation findSimilarItem(String name) {
        ResourceLocation ret = null;
        double similarity = 0.0f;

        for (ResourceLocation res : Item.REGISTRY.getKeys()) {
            final double currentSimilarity = StringUtil.levenshteinDistance(name, res.getPath());

            if (currentSimilarity >= similarity) {
                similarity = currentSimilarity;
                ret = res;
            }
        }

        return ret;
    }

    private Item findItem(String name) {
        final ResourceLocation res = new ResourceLocation(name);
        return Item.REGISTRY.getObject(res);
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

    @Override
    public ObjectArrayList<String> getCommandArgs(String[] args) {
        ObjectArrayList<String> suggestions = new ObjectArrayList<>();

        if (args.length == 1) {
            for (ResourceLocation itemName : Item.REGISTRY.getKeys()) {
                suggestions.add("\u00A7a" + itemName.getPath());
            }
        } else if (args.length == 2) {
            suggestions.add("\u00A7a1");
            suggestions.add("\u00A7a64");
        } else if (args.length == 3) {
            suggestions.add("\u00A7a0");
        } else if (args.length == 4) {
            suggestions.add("\u00A7a{display:{Name:\"Custom Item\"}}");
        }

        return suggestions;
    }
}
