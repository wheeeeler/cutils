package net.wheel.cutils.impl.command;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;

public final class SaveCommand extends Command {

    public SaveCommand() {
        super("Save", new String[] { "SaveAll" }, "Saves all client settings to disk", "Save");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 1, 1)) {
            this.printUsage();
            return;
        }

        crack.INSTANCE.getConfigManager().saveAll();
        crack.INSTANCE.logChat("Saved current config");
    }
}
