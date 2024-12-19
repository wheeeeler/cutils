package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.ignore.Ignored;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.IgnoreConfig;

public final class IgnoreCommand extends Command {

    private final String[] addAlias = new String[] { "Add" };
    private final String[] removeAlias = new String[] { "Remove" };
    private final String[] listAlias = new String[] { "List" };
    private final String[] clearAlias = new String[] { "Clear" };

    public IgnoreCommand() {
        super("Ignore", new String[] { "Ignor" }, "Allows you to ignore other players", "Ignore Add <Username>\n" +
                "Ignore Remove <Username>\n" +
                "Ignore List\n" +
                "Ignore Clear");
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

            final Ignored ignored = crack.INSTANCE.getIgnoredManager().find(split[2]);

            if (ignored != null) {
                crack.INSTANCE.logChat("\u00A7c" + ignored.getName() + " \u00A7f is already ignored");
            } else {
                crack.INSTANCE.logChat("Added \u00A7c" + split[2] + "\u00A7f to your ignore list");
                crack.INSTANCE.getIgnoredManager().add(split[2]);
                crack.INSTANCE.getConfigManager().save(IgnoreConfig.class);
            }
        } else if (equals(removeAlias, split[1])) {
            if (!this.clamp(input, 3, 3)) {
                this.printUsage();
                return;
            }

            final int size = crack.INSTANCE.getIgnoredManager().getIgnoredList().size();

            if (size == 0) {
                crack.INSTANCE.logChat("\u00A7cYou don't have anyone ignored");
                return;
            }

            final Ignored ignored = crack.INSTANCE.getIgnoredManager().find(split[2]);

            if (ignored != null) {
                crack.INSTANCE.logChat("Removed \u00A7c" + ignored.getName() + "\u00A7f from your ignore list");
                crack.INSTANCE.getIgnoredManager().getIgnoredList().remove(ignored);
                crack.INSTANCE.getConfigManager().save(IgnoreConfig.class);
            } else {
                crack.INSTANCE.logChat("\u00A7c" + split[2] + " \u00A7f is not ignored");
            }
        } else if (equals(listAlias, split[1])) {
            if (!this.clamp(input, 2, 2)) {
                this.printUsage();
                return;
            }

            final int size = crack.INSTANCE.getIgnoredManager().getIgnoredList().size();

            if (size > 0) {
                final TextComponentString msg = new TextComponentString("\u00A77Ignored [" + size + "]\u00A7f ");

                for (int i = 0; i < size; i++) {
                    final Ignored ignored = crack.INSTANCE.getIgnoredManager().getIgnoredList().get(i);
                    if (ignored != null) {
                        msg.appendSibling(new TextComponentString(
                                "\u00A7a" + ignored.getName() + "\u00A7c" + ((i == size - 1) ? "" : ", ")));
                    }
                }

                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(msg);
            } else {
                crack.INSTANCE.logChat("\u00A7cYou don't have anyone ignored");
            }
        } else if (equals(clearAlias, split[1])) {
            if (!this.clamp(input, 2, 2)) {
                this.printUsage();
                return;
            }

            final int size = crack.INSTANCE.getIgnoredManager().getIgnoredList().size();

            if (size > 0) {
                crack.INSTANCE.logChat("Removed \u00A7c" + size + "\u00A7f ignored player" + (size > 1 ? "s" : ""));
                crack.INSTANCE.getIgnoredManager().getIgnoredList().clear();
                crack.INSTANCE.getConfigManager().save(IgnoreConfig.class);
            } else {
                crack.INSTANCE.logChat("\u00A7cYou don't have anyone ignored");
            }
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
            if (equals(addAlias, args[1])) {
                suggestions.add("\u00A7a<cafcat>");
            } else if (equals(removeAlias, args[1])) {
                for (Ignored ignored : crack.INSTANCE.getIgnoredManager().getIgnoredList()) {
                    suggestions.add("\u00A7c" + ignored.getName());
                }
            }
        }

        return suggestions;
    }
}
