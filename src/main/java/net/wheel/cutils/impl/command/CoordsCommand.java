package net.wheel.cutils.impl.command;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.text.DecimalFormat;

import net.minecraft.client.Minecraft;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;

public final class CoordsCommand extends Command {

    public CoordsCommand() {
        super("Coords", new String[] { "Coord", "Coordinates", "Coordinate" },
                "Copies your coordinates to the clipboard", "Coords");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 1, 1)) {
            this.printUsage();
            return;
        }

        final DecimalFormat format = new DecimalFormat("#.#");
        final StringSelection contents = new StringSelection(format.format(Minecraft.getMinecraft().player.posX) + ", "
                + format.format(Minecraft.getMinecraft().player.posY) + ", "
                + format.format(Minecraft.getMinecraft().player.posZ));
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(contents, null);
        crack.INSTANCE.logChat("Copied coordinates to clipboard");
    }
}
