package net.wheel.cutils.impl.command;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;

public final class ConfigCommand extends Command {
    public ConfigCommand() {
        super("Config", new String[] { "Conf" }, "Change the active config", "Config <config>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 2)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");
        final String config = split[1];
        crack.INSTANCE.getConfigManager().switchToConfig(config);
        crack.INSTANCE.logChat("\247c" + "Switched to config " + config);
    }
}
