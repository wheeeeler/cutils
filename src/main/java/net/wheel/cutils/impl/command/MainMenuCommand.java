package net.wheel.cutils.impl.command;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;

public final class MainMenuCommand extends Command {

    public MainMenuCommand() {
        super("MainMenu", new String[] { "ToggleMainMenu", "ToggleMM", "CustomMainMenu", "CustomMM" },
                "Enables or disables the crack main menu", "MainMenu");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 1, 1)) {
            this.printUsage();
            return;
        }

        crack.INSTANCE.getConfigManager()
                .setCustomMainMenuHidden(!crack.INSTANCE.getConfigManager().isCustomMainMenuHidden());
        crack.INSTANCE.logChat("Custom main menu "
                + (crack.INSTANCE.getConfigManager().isCustomMainMenuHidden() ? "hidden!" : "restored!"));
    }
}
