package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.gui.hud.component.ConsoleComponent;

public final class ConsoleCommand extends Command {

    public ConsoleCommand() {
        super("console", new String[] { "console", "term" }, "open term", "console");
    }

    @Override
    public void exec(String input) {
        Minecraft.getMinecraft().displayGuiScreen(new ConsoleComponent());
    }
}
