package net.wheel.cutils.impl.command;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import net.minecraft.client.Minecraft;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;

public final class IPCommand extends Command {

    public IPCommand() {
        super("IP", new String[] { "IPAddress" }, "Copies the current server ip to your clipboard", "IP");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 1, 1)) {
            this.printUsage();
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.getCurrentServerData() != null) {
            final StringSelection contents = new StringSelection(mc.getCurrentServerData().serverIP);
            final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(contents, null);
            crack.INSTANCE.logChat("Copied IP to clipboard");
        } else {
            crack.INSTANCE.errorChat("Error, Join a server");
        }
    }
}
