package net.wheel.cutils.impl.command;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.util.StringUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.NukerFilterConfig;
import net.wheel.cutils.impl.module.MISC.NukeModule;

public class NukerFilterCommand extends Command {

    private final String[] addAlias = new String[] { "Add", "A" };
    private final String[] removeAlias = new String[] { "Remove", "Rem", "R", "Delete", "Del", "D" };
    private final String[] listAlias = new String[] { "List", "Lst" };
    private final String[] clearAlias = new String[] { "Clear", "C" };

    public NukerFilterCommand() {
        super("NukerFilter", new String[] { "NukerF", "FilterN" }, "Allows you to change what blocks nuker mines",
                "NukerFilter Add <Block_Name>\n" +
                        "NukerFilter Add <ID>\n" +
                        "NukerFilter Remove <Block_Name>\n" +
                        "NukerFilter Remove <ID>\n" +
                        "NukerFilter List\n" +
                        "NukerFilter Clear");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 3)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        final NukeModule nuker = (NukeModule) crack.INSTANCE.getModuleManager().find(NukeModule.class);

        if (nuker != null) {
            if (equals(addAlias, split[1])) {
                if (!this.clamp(input, 3, 3)) {
                    this.printUsage();
                    return;
                }

                if (StringUtil.isInt(split[2])) {
                    final int id = Integer.parseInt(split[2]);

                    if (id > 0) {
                        final Block block = Block.getBlockById(id);

                        if (nuker.contains(block)) {
                            crack.INSTANCE.logChat("Nuker already contains " + block.getLocalizedName());
                        } else {
                            nuker.add(Block.getIdFromBlock(block));

                            crack.INSTANCE.getConfigManager().save(NukerFilterConfig.class);
                            crack.INSTANCE.logChat("Added " + block.getLocalizedName() + " to nuker");
                        }
                    } else {
                        crack.INSTANCE.errorChat("Cannot add Air to nuker");
                    }
                } else {
                    final Block block = Block.getBlockFromName(split[2].toLowerCase());

                    if (block != null) {
                        if (block == Blocks.AIR) {
                            crack.INSTANCE.errorChat("Cannot add Air to nuker");
                        } else {
                            if (nuker.contains(block)) {
                                crack.INSTANCE.logChat("Nuker already contains " + block.getLocalizedName());
                            } else {
                                nuker.add(Block.getIdFromBlock(block));

                                crack.INSTANCE.getConfigManager().save(NukerFilterConfig.class);
                                crack.INSTANCE.logChat("Added " + block.getLocalizedName() + " to nuker");
                            }
                        }
                    } else {
                        crack.INSTANCE.logChat("\247c" + split[2] + "\247f is not a valid block");
                    }
                }
            } else if (equals(removeAlias, split[1])) {
                if (!this.clamp(input, 3, 3)) {
                    this.printUsage();
                    return;
                }

                if (StringUtil.isInt(split[2])) {
                    final int id = Integer.parseInt(split[2]);

                    if (id > 0) {
                        final Block block = Block.getBlockById(id);

                        if (nuker.contains(block)) {
                            nuker.remove(Block.getIdFromBlock(block));

                            crack.INSTANCE.getConfigManager().save(NukerFilterConfig.class);
                            crack.INSTANCE.logChat("Removed " + block.getLocalizedName() + " from nuker");
                        } else {
                            crack.INSTANCE.logChat("Nuker doesn't contain " + block.getLocalizedName());
                        }
                    } else {
                        crack.INSTANCE.errorChat("Cannot remove Air from nuker");
                    }
                } else {
                    final Block block = Block.getBlockFromName(split[2].toLowerCase());

                    if (block != null) {
                        if (block == Blocks.AIR) {
                            crack.INSTANCE.errorChat("Cannot remove Air from nuker");
                        } else {
                            if (nuker.contains(block)) {
                                nuker.remove(Block.getIdFromBlock(block));

                                crack.INSTANCE.getConfigManager().save(NukerFilterConfig.class);
                                crack.INSTANCE.logChat("Removed " + block.getLocalizedName() + " from nuker");
                            } else {
                                crack.INSTANCE.logChat("Nuker doesn't contain " + block.getLocalizedName());
                            }
                        }
                    } else {
                        crack.INSTANCE.logChat("\247c" + split[2] + "\247f is not a valid block");
                    }
                }
            } else if (equals(listAlias, split[1])) {
                if (!this.clamp(input, 2, 2)) {
                    this.printUsage();
                    return;
                }

                if (!nuker.getFilter().getValue().isEmpty()) {
                    final TextComponentString msg = new TextComponentString("\2477Nuker IDs: ");

                    for (Block block : nuker.getFilter().getValue()) {
                        msg.appendSibling(
                                new TextComponentString("\2477[\247a" + Block.getIdFromBlock(block) + "\2477] ")
                                        .setStyle(new Style()
                                                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                        new TextComponentString(block.getLocalizedName())))));
                    }

                    crack.INSTANCE.logcChat(msg);
                } else {
                    crack.INSTANCE.logChat("You don't have any nuker ids");
                }
            } else if (equals(clearAlias, split[1])) {
                if (!this.clamp(input, 2, 2)) {
                    this.printUsage();
                    return;
                }
                nuker.clear();

                crack.INSTANCE.getConfigManager().save(NukerFilterConfig.class);
                crack.INSTANCE.logChat("Cleared all blocks from nuker");
            } else {
                crack.INSTANCE.errorChat("Unknown input " + "\247f\"" + input + "\"");
                this.printUsage();
            }
        } else {
            crack.INSTANCE.errorChat("Nuker not present");
        }
    }
}
