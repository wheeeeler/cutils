package net.wheel.cutils.impl.command;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.util.StringUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.ItemFindConfig;
import net.wheel.cutils.impl.module.GLOBAL.ItemFinderModule;

public final class ItemFindCommand extends Command {

    private final String[] addAlias = new String[] { "Add" };
    private final String[] removeAlias = new String[] { "Remove", "Rem", "Delete", "Del" };
    private final String[] listAlias = new String[] { "List", "Lst" };
    private final String[] clearAlias = new String[] { "Clear", "clr" };

    public ItemFindCommand() {
        super("Itemfind", new String[] { "find", "locate" }, "Allows you to change what blocks are visible on search",
                "Itemfind Add <Block_Name>\n" +
                        "Itemfind Add <ID>\n" +
                        "Itemfind Add hand\n" +
                        "Itemfind Remove <Block_Name>\n" +
                        "Itemfind Remove <ID>\n" +
                        "Itemfind Remove hand\n" +
                        "Itemfind List\n" +
                        "Itemfind Clear");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 3)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        final ItemFinderModule itemFinderModule = (ItemFinderModule) crack.INSTANCE.getModuleManager()
                .find(ItemFinderModule.class);

        if (itemFinderModule != null) {
            if (equals(addAlias, split[1])) {
                if (!this.clamp(input, 3, 3)) {
                    this.printUsage();
                    return;
                }

                Block block = getBlockFromInput(split[2]);
                if (block != null) {
                    if (itemFinderModule.contains(block)) {
                        crack.INSTANCE.logChat("Itemfind already contains " + block.getLocalizedName());
                    } else {
                        itemFinderModule.add(Block.getIdFromBlock(block));
                        if (itemFinderModule.isEnabled()) {
                            itemFinderModule.clearBlocks();
                            itemFinderModule.updateRenders();
                        }
                        crack.INSTANCE.getConfigManager().save(ItemFindConfig.class);
                        crack.INSTANCE.logChat("Added " + block.getLocalizedName() + " to search");
                    }
                }
            } else if (equals(removeAlias, split[1])) {
                if (!this.clamp(input, 3, 3)) {
                    this.printUsage();
                    return;
                }

                Block block = getBlockFromInput(split[2]);
                if (block != null) {
                    if (itemFinderModule.contains(block)) {
                        itemFinderModule.remove(Block.getIdFromBlock(block));
                        if (itemFinderModule.isEnabled()) {
                            itemFinderModule.clearBlocks();
                            itemFinderModule.updateRenders();
                        }
                        crack.INSTANCE.getConfigManager().save(ItemFindConfig.class);
                        crack.INSTANCE.logChat("Removed " + block.getLocalizedName() + " from Itemfind");
                    } else {
                        crack.INSTANCE.logChat("Itemfind doesn't contain " + block.getLocalizedName());
                    }
                }
            } else if (equals(listAlias, split[1])) {
                if (!this.clamp(input, 2, 2)) {
                    this.printUsage();
                    return;
                }

                if (!itemFinderModule.getBlockIds().getValue().isEmpty()) {
                    final TextComponentString msg = new TextComponentString("\2477Itemfind IDs: ");

                    for (Block block : itemFinderModule.getBlockIds().getValue()) {
                        msg.appendSibling(
                                new TextComponentString("\2477[\247a" + Block.getIdFromBlock(block) + "\2477] ")
                                        .setStyle(new Style()
                                                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                        new TextComponentString(block.getLocalizedName())))));
                    }

                    crack.INSTANCE.logcChat(msg);
                } else {
                    crack.INSTANCE.logChat("You don't have any Itemfind ids");
                }
            } else if (equals(clearAlias, split[1])) {
                if (!this.clamp(input, 2, 2)) {
                    this.printUsage();
                    return;
                }
                itemFinderModule.clear();
                if (itemFinderModule.isEnabled()) {
                    itemFinderModule.clearBlocks();
                    itemFinderModule.updateRenders();
                }
                crack.INSTANCE.getConfigManager().save(ItemFindConfig.class);
                crack.INSTANCE.logChat("Cleared all blocks from Itemfind");
            } else {
                crack.INSTANCE.errorChat("Unknown input " + "\247f\"" + input + "\"");
                this.printUsage();
            }
        } else {
            crack.INSTANCE.errorChat("Itemfind not present");
        }
    }

    private Block getBlockFromInput(String input) {
        if (input.equalsIgnoreCase("hand")) {
            ItemStack itemStack = Minecraft.getMinecraft().player.getHeldItemMainhand();
            if (itemStack.getItem() instanceof ItemBlock) {
                return ((ItemBlock) itemStack.getItem()).getBlock();
            } else {
                crack.INSTANCE.errorChat("You are not holding a block");
                return null;
            }
        } else if (StringUtil.isInt(input)) {
            int id = Integer.parseInt(input);
            if (id > 0) {
                return Block.getBlockById(id);
            } else {
                crack.INSTANCE.errorChat("Cannot add Air to Itemfind");
                return null;
            }
        } else {
            Block block = Block.getBlockFromName(input.toLowerCase());
            if (block != null) {
                if (block == Blocks.AIR) {
                    crack.INSTANCE.errorChat("Cannot add Air to Itemfind");
                    return null;
                } else {
                    return block;
                }
            } else {
                crack.INSTANCE.logChat("\247c" + input + "\247f is not a valid block");
                return null;
            }
        }
    }
}
