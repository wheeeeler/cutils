package net.wheel.cutils.impl.command;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.macro.Macro;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.MacroConfig;

public final class MacroCommand extends Command {

    private final String[] addAlias = new String[] { "Add", "A" };
    private final String[] removeAlias = new String[] { "Remove", "R", "Rem", "Delete", "Del" };
    private final String[] listAlias = new String[] { "List", "L" };
    private final String[] clearAlias = new String[] { "Clear", "C" };

    public MacroCommand() {
        super("Macro", new String[] { "Mac" }, "Allows you to create chat macros", "Macro Add <Name> <Key> <Macro>\n" +
                "Macro Remove <Name>\n" +
                "Macro List\n" +
                "Macro Clear");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        if (equals(addAlias, split[1])) {
            if (!this.clamp(input, 5)) {
                this.printUsage();
                return;
            }

            final String name = split[2];
            final String key = split[3];
            final Macro macro = crack.INSTANCE.getMacroManager().find(name);

            if (macro != null) {
                crack.INSTANCE.logChat("\u00A7c\"" + name + "\"\u00A7f is already a macro");
            } else {
                if (Keyboard.getKeyIndex(key.toUpperCase()) != Keyboard.KEY_NONE) {
                    final StringBuilder sb = new StringBuilder();
                    for (int i = 4; i < split.length; i++) {
                        sb.append(split[i]).append((i == split.length - 1) ? "" : " ");
                    }

                    crack.INSTANCE.logChat("Added macro \u00A7c" + name + "\u00A7f bound to " + key.toUpperCase());
                    crack.INSTANCE.getMacroManager().getMacroList()
                            .add(new Macro(name, key.toUpperCase(), sb.toString()));
                    crack.INSTANCE.getConfigManager().save(MacroConfig.class);
                } else {
                    crack.INSTANCE.logChat("\u00A7c" + key + "\u00A7f is not a valid key");
                }
            }
        } else if (equals(removeAlias, split[1])) {
            if (!this.clamp(input, 3, 3)) {
                this.printUsage();
                return;
            }

            final String name = split[2];
            final Macro macro = crack.INSTANCE.getMacroManager().find(name);

            if (macro != null) {
                crack.INSTANCE.logChat("Removed macro \u00A7c" + macro.getName() + "\u00A7f");
                crack.INSTANCE.getMacroManager().getMacroList().remove(macro);
                crack.INSTANCE.getConfigManager().save(MacroConfig.class);
            } else {
                crack.INSTANCE.errorChat("Unknown macro \u00A7f\"" + name + "\"");
            }
        } else if (equals(listAlias, split[1])) {
            if (!this.clamp(input, 2, 2)) {
                this.printUsage();
                return;
            }

            final int size = crack.INSTANCE.getMacroManager().getMacroList().size();
            if (size > 0) {
                final TextComponentString msg = new TextComponentString("\u00A77Macros [" + size + "]\u00A7f ");
                for (int i = 0; i < size; i++) {
                    final Macro macro = crack.INSTANCE.getMacroManager().getMacroList().get(i);
                    if (macro != null) {
                        msg.appendSibling(new TextComponentString(
                                "\u00A7a" + macro.getName() + "\u00A7c" + ((i == size - 1) ? "" : ", "))
                                        .setStyle(new Style()
                                                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                        new TextComponentString("Key: " + macro.getKey().toUpperCase()
                                                                + "\nMacro: " + macro.getMacro())))));
                    }
                }
                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(msg);
            } else {
                crack.INSTANCE.logChat("\u00A7cYou don't have any macros");
            }
        } else if (equals(clearAlias, split[1])) {
            if (!this.clamp(input, 2, 2)) {
                this.printUsage();
                return;
            }

            final int macros = crack.INSTANCE.getMacroManager().getMacroList().size();
            if (macros > 0) {
                crack.INSTANCE.logChat("Removed \u00A7c" + macros + "\u00A7f macro" + (macros > 1 ? "s" : ""));
                crack.INSTANCE.getMacroManager().getMacroList().clear();
                crack.INSTANCE.getConfigManager().save(MacroConfig.class);
            } else {
                crack.INSTANCE.logChat("\u00A7cYou don't have any macros");
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
                suggestions.add("\u00A7a<Name>");
            } else if (equals(removeAlias, args[1])) {
                for (Macro macro : crack.INSTANCE.getMacroManager().getMacroList()) {
                    suggestions.add("\u00A7c" + macro.getName());
                }
            }
        } else if (args.length == 3 && equals(addAlias, args[1])) {
            suggestions.add("\u00A7a<Key>");
        } else if (args.length == 4 && equals(addAlias, args[1])) {
            suggestions.add("\u00A7a<Macro Text>");
        }

        return suggestions;
    }
}
