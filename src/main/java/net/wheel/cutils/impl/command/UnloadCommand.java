package net.wheel.cutils.impl.command;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;

public final class UnloadCommand extends Command {

    public UnloadCommand() {
        super("Unload", new String[] { "ULoad" }, "Unloads the client", "Unload");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 1, 1)) {
            this.printUsage();
            return;
        }

        crack.INSTANCE.unload();
    }
}
