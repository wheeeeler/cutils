package net.wheel.cutils.impl.command;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;

public final class ReloadCommand extends Command {

    public ReloadCommand() {
        super("Reload", new String[] { "Rload" }, "Reloads the client", "Reload");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 1, 1)) {
            this.printUsage();
            return;
        }

        crack.INSTANCE.reload();
        crack.INSTANCE.logChat("Client Reloaded");
    }
}
