package net.wheel.cutils.impl.command;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.ModuleConfig;

public final class ToggleCommand extends Command {

    public ToggleCommand() {
        super("Toggle", new String[] { "T", "Tog" }, "Allows you to toggle modules or between two mode options",
                "Toggle <Module>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 5)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        final Module mod = crack.INSTANCE.getModuleManager().find(split[1]);

        if (mod != null) {
            if (mod.getType() == Module.ModuleType.HIDDEN) {
                crack.INSTANCE.errorChat("Cannot toggle " + "\247f\"" + mod.getDisplayName() + "\"");
            } else {
                mod.toggle();
                crack.INSTANCE.logChat("Toggled " + (mod.isEnabled() ? "\247a" : "\247c") + mod.getDisplayName());
            }
            crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
        } else {
            crack.INSTANCE.errorChat("Unknown module " + "\247f\"" + split[1] + "\"");
            final Module similar = crack.INSTANCE.getModuleManager().findSimilar(split[1]);

            if (similar != null) {
                crack.INSTANCE.logChat("Did you mean " + "\247c" + similar.getDisplayName() + "\247f?");
            }
        }
    }

}
