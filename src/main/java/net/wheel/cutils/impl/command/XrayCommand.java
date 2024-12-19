package net.wheel.cutils.impl.command;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.util.StringUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.XrayConfig;
import net.wheel.cutils.impl.module.RENDER.XrayModule;

public final class XrayCommand extends Command {

    private final String[] addAlias = new String[] { "Add" };
    private final String[] removeAlias = new String[] { "Remove" };
    private final String[] listAlias = new String[] { "List" };
    private final String[] clearAlias = new String[] { "Clear" };

    public XrayCommand() {
        super("Xray", new String[] { "JadeVision", "Jade" }, "Manage visible blocks for xray",
                "Xray Add <Block_Name | ID>\n" +
                        "Xray Remove <Block_Name | ID>\n" +
                        "Xray List\n" +
                        "Xray Clear");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 3)) {
            this.printUsage();
            return;
        }

        String[] split = input.split(" ");
        XrayModule xray = (XrayModule) crack.INSTANCE.getModuleManager().find(XrayModule.class);

        if (xray == null) {
            crack.INSTANCE.errorChat("Xray module not found.");
            return;
        }

        if (equals(addAlias, split[1])) {
            handleAddCommand(split, xray);
        } else if (equals(removeAlias, split[1])) {
            handleRemoveCommand(split, xray);
        } else if (equals(listAlias, split[1])) {
            displayXrayList(xray);
        } else if (equals(clearAlias, split[1])) {
            clearXray(xray);
        } else {
            crack.INSTANCE.errorChat("Unknown input \u00A7f\"" + input + "\"");
            this.printUsage();
        }
    }

    private void handleAddCommand(String[] split, XrayModule xray) {
        if (!this.clamp(String.valueOf(split.length), 3, 3)) {
            this.printUsage();
            return;
        }

        Block block = getBlockFromInput(split[2]);
        if (block != null && block != Blocks.AIR) {
            if (xray.contains(block)) {
                crack.INSTANCE.logChat("Xray already contains " + block.getLocalizedName());
            } else {
                xray.add(Block.getIdFromBlock(block));
                if (xray.isEnabled())
                    xray.updateRenders();
                crack.INSTANCE.getConfigManager().save(XrayConfig.class);
                crack.INSTANCE.logChat("Added " + block.getLocalizedName() + " to xray");
            }
        } else {
            crack.INSTANCE.logChat("\u00A7c" + split[2] + "\u00A7f is not a valid block");
        }
    }

    private void handleRemoveCommand(String[] split, XrayModule xray) {
        if (!this.clamp(String.valueOf(split.length), 3, 3)) {
            this.printUsage();
            return;
        }

        Block block = getBlockFromInput(split[2]);
        if (block != null && block != Blocks.AIR) {
            if (xray.contains(block)) {
                xray.remove(Block.getIdFromBlock(block));
                if (xray.isEnabled())
                    xray.updateRenders();
                crack.INSTANCE.getConfigManager().save(XrayConfig.class);
                crack.INSTANCE.logChat("Removed " + block.getLocalizedName() + " from xray");
            } else {
                crack.INSTANCE.logChat("Xray does not contain " + block.getLocalizedName());
            }
        } else {
            crack.INSTANCE.errorChat("Cannot remove Air from xray");
        }
    }

    private void displayXrayList(XrayModule xray) {
        if (xray.getBlocks().getValue().isEmpty()) {
            crack.INSTANCE.logChat("Xray list is empty.");
            return;
        }

        TextComponentString msg = new TextComponentString("\u00A77Xray IDs: ");
        for (Block block : xray.getBlocks().getValue()) {
            msg.appendSibling(new TextComponentString("\u00A77[\u00A7a" + Block.getIdFromBlock(block) + "\u00A77] ")
                    .setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new TextComponentString(block.getLocalizedName())))));
        }
        crack.INSTANCE.logcChat(msg);
    }

    private void clearXray(XrayModule xray) {
        xray.clear();
        if (xray.isEnabled())
            xray.updateRenders();
        crack.INSTANCE.getConfigManager().save(XrayConfig.class);
        crack.INSTANCE.logChat("Cleared all blocks from xray");
    }

    private Block getBlockFromInput(String input) {
        if (StringUtil.isInt(input)) {
            int id = Integer.parseInt(input);
            return id > 0 ? Block.getBlockById(id) : Blocks.AIR;
        } else {
            return Block.getBlockFromName(input.toLowerCase());
        }
    }

    @Override
    public ObjectArrayList<String> getCommandArgs(String[] args) {
        ObjectArrayList<String> suggestions = new ObjectArrayList<>();
        if (args.length == 1) {
            suggestions.add("Add");
            suggestions.add("Remove");
            suggestions.add("List");
            suggestions.add("Clear");
        } else if (args.length == 2) {
            if (equals(addAlias, args[1]) || equals(removeAlias, args[1])) {
                suggestions.add("stone");
                suggestions.add("diamond_ore");
                suggestions.add("iron_ore");
                suggestions.add("coal_ore");
            }
        }
        return suggestions;
    }
}
