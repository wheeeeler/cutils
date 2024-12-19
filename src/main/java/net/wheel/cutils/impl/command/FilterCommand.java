package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.FilterConfig;

public final class FilterCommand extends Command {

    private final String[] addAlias = new String[] { "Add" };
    private final String[] removeAlias = new String[] { "Remove" };
    private final String[] listAlias = new String[] { "List" };
    private final String[] clearAlias = new String[] { "Clear" };

    public FilterCommand() {
        super("Filter", new String[] { "Filt" }, "Manage filtered words or phrases", "Filter Add <Word>\n" +
                "Filter Remove <Word>\n" +
                "Filter List\n" +
                "Filter Clear");
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

            crack.INSTANCE.getFilterManager().addFilteredWord(split[2]);
            crack.INSTANCE.getConfigManager().save(FilterConfig.class);
            crack.INSTANCE.logChat("\u00A7aAdded \u00A7c" + split[2] + "\u00A7f to the filter list");
        } else if (equals(removeAlias, split[1])) {
            if (!this.clamp(input, 3, 3)) {
                this.printUsage();
                return;
            }

            crack.INSTANCE.getFilterManager().removeFilteredWord(split[2]);
            crack.INSTANCE.getConfigManager().save(FilterConfig.class);
            crack.INSTANCE.logChat("\u00A7aRemoved \u00A7c" + split[2] + "\u00A7f from the filter list");
        } else if (equals(listAlias, split[1])) {
            if (!this.clamp(input, 2, 2)) {
                this.printUsage();
                return;
            }

            final int size = crack.INSTANCE.getFilterManager().getFilteredWords().size();
            if (size > 0) {
                final TextComponentString msg = new TextComponentString(
                        "\u00A77Filtered words/phrases [" + size + "]\u00A7f: ");
                for (String word : crack.INSTANCE.getFilterManager().getFilteredWords()) {
                    msg.appendSibling(new TextComponentString("\u00A7a" + word + "\u00A7f, "));
                }
                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(msg);
            } else {
                crack.INSTANCE.logChat("\u00A7cNo words filtered");
            }
        } else if (equals(clearAlias, split[1])) {
            if (!this.clamp(input, 2, 2)) {
                this.printUsage();
                return;
            }

            crack.INSTANCE.getFilterManager().clearFilteredWords();
            crack.INSTANCE.getConfigManager().save(FilterConfig.class);
            crack.INSTANCE.logChat("\u00A7aCleared all filtered words and phrases");
        } else {
            crack.INSTANCE.errorChat("\u00A7cUnknown input \u00A7f\"" + input + "\"");
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
            if (equals(addAlias, args[1])) {
                suggestions.add("\u00A7a<cafcat>");
            } else if (equals(removeAlias, args[1])) {
                for (String word : crack.INSTANCE.getFilterManager().getFilteredWords()) {
                    suggestions.add("\u00A7c" + word);
                }
            }
        }

        return suggestions;
    }
}
