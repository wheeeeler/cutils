package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketChatMessage;

import net.wheel.cutils.api.command.Command;

public final class SayCommand extends Command {

    public SayCommand() {
        super("Say", new String[] { "S" }, "Allows you to send a direct chat message", "Say <Message>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        Minecraft.getMinecraft().player.connection
                .sendPacket(new CPacketChatMessage(input.substring(split[0].length() + 1)));
    }
}
