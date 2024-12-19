package net.wheel.cutils.impl.command;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.util.StringUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.ModuleConfig;

public final class ColorCommand extends Command {

    public ColorCommand() {
        super("Color", new String[] { "Col", "Colour" }, "Allows you to change arraylist colors",
                "Color <Module> <Hex or Color Code>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 3, 3)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        final Module mod = crack.INSTANCE.getModuleManager().find(split[1]);

        if (mod != null) {
            if (mod.getType() == Module.ModuleType.HIDDEN) {
                crack.INSTANCE.errorChat("Cannot change color of " + "\u00A7f\"" + mod.getDisplayName() + "\"");
            } else {
                if (StringUtil.isLong(split[2], 16)) {
                    crack.INSTANCE.logChat("\u00A7c" + mod.getDisplayName() + "\u00A7f color has been set to "
                            + split[2].toUpperCase());
                    mod.setColor((int) Long.parseLong(split[2], 16));
                    crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
                } else {
                    crack.INSTANCE.errorChat("Invalid input " + "\"" + split[2] + "\" expected a hex value");
                }
            }
        } else {
            crack.INSTANCE.errorChat("Unknown module " + "\u00A7f\"" + split[1] + "\"");
            final Module similar = crack.INSTANCE.getModuleManager().findSimilar(split[1]);
            if (similar != null) {
                crack.INSTANCE.logChat("Did you mean " + "\u00A7c" + similar.getDisplayName() + "\u00A7f?");
            }
        }
    }

    @Override
    public ObjectArrayList<String> getCommandArgs(String[] args) {
        ObjectArrayList<String> suggestions = new ObjectArrayList<>();

        if (args.length < 1) {
            for (Module mod : crack.INSTANCE.getModuleManager().getModuleList()) {
                suggestions.add(mod.getDisplayName());
            }
        } else if (args.length == 2) {
            suggestions.add("\u00A70Black");
            suggestions.add("\u00A71DarkBlue");
            suggestions.add("\u00A72DarkGreen");
            suggestions.add("\u00A73DarkAqua");
            suggestions.add("\u00A74DarkRed");
            suggestions.add("\u00A75DarkPurple");
            suggestions.add("\u00A76Gold");
            suggestions.add("\u00A77Gray");
            suggestions.add("\u00A78DarkGray");
            suggestions.add("\u00A79Blue");
            suggestions.add("\u00A7aGreen");
            suggestions.add("\u00A7bAqua");
            suggestions.add("\u00A7cRed");
            suggestions.add("\u00A7dLightPurple");
            suggestions.add("\u00A7eYellow");
            suggestions.add("\u00A7fWhite");
        }

        return suggestions;
    }
}
