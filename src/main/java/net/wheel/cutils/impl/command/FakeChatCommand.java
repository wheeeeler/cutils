package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

import net.wheel.cutils.api.command.Command;

public final class FakeChatCommand extends Command {

    public FakeChatCommand() {
        super("FakeChat", new String[] { "FChat", "TellRaw" }, "Allows you to add a fake chat message",
                "FakeChat <Message>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        final StringBuilder sb = new StringBuilder();

        for (int i = 1; i < split.length; i++) {
            final String s = split[i];
            sb.append(s).append(i == split.length - 1 ? "" : " ");
        }

        final String message = sb.toString();
        Minecraft.getMinecraft().ingameGUI.getChatGUI()
                .printChatMessage(new TextComponentString(message.replace("&", "\247")));
    }
}
