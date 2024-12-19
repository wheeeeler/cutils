package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;

public final class NameCommand extends Command {

    public NameCommand() {
        super("Name", new String[] { "Nam" }, "Allows you to change the capitalizaton of your name", "Name <Username>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 2)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        if (split[1].equalsIgnoreCase(Minecraft.getMinecraft().session.getUsername())) {
            Minecraft.getMinecraft().session = new Session(split[1], Minecraft.getMinecraft().session.getPlayerID(),
                    Minecraft.getMinecraft().session.getToken(), "mojang");
            crack.INSTANCE.logChat("Set username to " + split[1]);
        } else {
            crack.INSTANCE.errorChat("Name must match");
        }
    }
}
