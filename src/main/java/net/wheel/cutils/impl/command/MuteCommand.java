package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.MuteConfig;

public final class MuteCommand extends Command {

    private final String[] addAlias = new String[] { "Add", "A" };
    private final String[] removeAlias = new String[] { "Remove", "R", "Rem", "Delete", "Del" };
    private final String[] listAlias = new String[] { "List", "L" };
    private final String[] clearAlias = new String[] { "Clear", "C" };

    public MuteCommand() {
        super("Mute", new String[] { "MuteMod" }, "add or remove modids", "Mute Add <modid>\n" +
                "Mute Remove <modid>\n" +
                "Mute List\n" +
                "Mute Clear");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 3)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        if (equals(addAlias, split[1])) {
            if (!this.clamp(input, 3, 3)) {
                this.printUsage();
                return;
            }

            crack.INSTANCE.getMuteManager().addMutedModId(split[2]);
            crack.INSTANCE.getConfigManager().save(MuteConfig.class);
            crack.INSTANCE.logChat("\u00A7aAdded \u00A7c" + split[2] + "\u00A7f to the mute list");
        } else if (equals(removeAlias, split[1])) {
            if (!this.clamp(input, 3, 3)) {
                this.printUsage();
                return;
            }

            crack.INSTANCE.getMuteManager().removeMutedModId(split[2]);
            crack.INSTANCE.getConfigManager().save(MuteConfig.class);
            crack.INSTANCE.logChat("\u00A7aRemoved \u00A7c" + split[2] + "\u00A7f from the mute list");
        } else if (equals(listAlias, split[1])) {
            if (!this.clamp(input, 2, 2)) {
                this.printUsage();
                return;
            }

            final int size = crack.INSTANCE.getMuteManager().getMutedModIds().size();
            if (size > 0) {
                final TextComponentString msg = new TextComponentString("\u00A77Muted mod IDs [" + size + "]\u00A7f: ");
                for (String modId : crack.INSTANCE.getMuteManager().getMutedModIds()) {
                    msg.appendSibling(new TextComponentString("\u00A7a" + modId + "\u00A7f, "));
                }
                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(msg);
            } else {
                crack.INSTANCE.logChat("\u00A7cNo mods are currently muted");
            }
        } else if (equals(clearAlias, split[1])) {
            if (!this.clamp(input, 2, 2)) {
                this.printUsage();
                return;
            }

            crack.INSTANCE.getMuteManager().clearMutedModIds();
            crack.INSTANCE.getConfigManager().save(MuteConfig.class);
            crack.INSTANCE.logChat("\u00A7aCleared the mute list");
        } else {
            crack.INSTANCE.errorChat("Unknown input \u00A7f\"" + input + "\"");
            this.printUsage();
        }
    }

    @Override
    public ObjectArrayList<String> getCommandArgs(String[] args) {
        ObjectArrayList<String> suggestions = new ObjectArrayList<>();

        if (args.length == 1) {
            suggestions.add("\u00A7aAdd");
            suggestions.add("\u00A7aRemove");
            suggestions.add("\u00A7aList");
            suggestions.add("\u00A7aClear");
        } else if (args.length == 2) {
            if (equals(addAlias, args[1]) || equals(removeAlias, args[1])) {
                for (String modId : crack.INSTANCE.getMuteManager().getMutedModIds()) {
                    suggestions.add("\u00A7c" + modId);
                }
                suggestions.add("\u00A7a<minecraft>");
            }
        }

        return suggestions;
    }
}
