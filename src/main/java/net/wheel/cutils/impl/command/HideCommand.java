package net.wheel.cutils.impl.command;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.ModuleConfig;

public final class HideCommand extends Command {

    public HideCommand() {
        super("Hide", new String[] { "Hid" }, "Allows you to hide modules from the arraylist", "Hide <Module>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 2)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");
        final Module mod = crack.INSTANCE.getModuleManager().find(split[1]);

        if (mod != null) {
            if (mod.getType() == Module.ModuleType.HIDDEN) {
                crack.INSTANCE.errorChat("Cannot hide \u00A7f\"" + mod.getDisplayName() + "\"");
            } else {
                mod.setHidden(!mod.isHidden());
                crack.INSTANCE.getConfigManager().save(ModuleConfig.class);

                if (mod.isHidden()) {
                    crack.INSTANCE.logChat("\u00A7c" + mod.getDisplayName() + "\u00A7f is now hidden");
                } else {
                    crack.INSTANCE.logChat("\u00A7c" + mod.getDisplayName() + "\u00A7f is no longer hidden");
                }
            }
        } else {
            crack.INSTANCE.errorChat("Unknown module \u00A7f\"" + split[1] + "\"");
            final Module similar = crack.INSTANCE.getModuleManager().findSimilar(split[1]);

            if (similar != null) {
                crack.INSTANCE.logChat("Did you mean \u00A7c" + similar.getDisplayName() + "\u00A7f?");
            }
        }
    }

    @Override
    public ObjectArrayList<String> getCommandArgs(String[] args) {
        ObjectArrayList<String> suggestions = new ObjectArrayList<>();

        if (args.length == 1) {
            for (Module mod : crack.INSTANCE.getModuleManager().getModuleList()) {
                if (mod.getType() != Module.ModuleType.HIDDEN) {
                    suggestions.add("\u00A7a" + mod.getDisplayName());
                }
            }
        }

        return suggestions;
    }
}
