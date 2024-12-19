package net.wheel.cutils.impl.command;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.ModuleConfig;

public final class RenameModuleCommand extends Command {

    public RenameModuleCommand() {
        super("RenameModule", new String[] { "rm", "renamemod", "renamemodule" }, "Rename modules",
                "renamemodule <module> <name>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 3, 3)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        final String originalModuleName = split[1];
        final String newModuleName = split[2];

        if (crack.INSTANCE.getModuleManager().find(originalModuleName) != null) {
            final Module mod = crack.INSTANCE.getModuleManager().find(originalModuleName);
            if (mod != null) {
                mod.setDisplayName(newModuleName);

                crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
                crack.INSTANCE.logChat("Set " + originalModuleName + " custom alias to " + newModuleName);
            }
        } else {
            crack.INSTANCE.logChat(originalModuleName + " does not exist!");
        }

    }
}
