package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.GuiConnecting;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.util.StringUtil;
import net.wheel.cutils.crack;

public final class ConnectCommand extends Command {

    public ConnectCommand() {
        super("Connect", new String[] { "Con" }, "Connects to a server", "Connect <Host>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 2)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");
        final String[] host = split[1].split(":");
        int port = 25565;

        if (host.length > 1) {
            if (StringUtil.isInt(host[1])) {
                port = Integer.parseInt(host[1]);
            } else {
                crack.INSTANCE.errorChat("Invalid port \"" + host[1] + "\"");
                return;
            }
        }

        if (Minecraft.getMinecraft().player.connection.getNetworkManager().channel().isOpen()) {
            Minecraft.getMinecraft().player.connection.getNetworkManager().closeChannel(null);
        }

        Minecraft.getMinecraft().displayGuiScreen(new GuiConnecting(null, Minecraft.getMinecraft(), host[0], port));
    }

    @Override
    public ObjectArrayList<String> getCommandArgs(String[] args) {
        ObjectArrayList<String> suggestions = new ObjectArrayList<>();

        if (args.length == 1) {

            suggestions.add("\u00A7asiriusmc.in");
        }

        return suggestions;
    }
}
