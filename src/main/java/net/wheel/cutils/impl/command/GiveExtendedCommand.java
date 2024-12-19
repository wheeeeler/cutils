package net.wheel.cutils.impl.command;

import java.util.TimerTask;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.gui.menu.GiveInputBox;

public final class GiveExtendedCommand extends Command {

    public GiveExtendedCommand() {
        super("givext", new String[] { "givext" }, "asda", "GiveExtended");
    }

    @Override
    public void exec(String input) {
        delayCmdScreen();
    }

    private void delayCmdScreen() {
        Minecraft mc = Minecraft.getMinecraft();
        GiveInputBox inputScreen = new GiveInputBox(mc.currentScreen);

        if (mc.currentScreen instanceof GuiChat) {
            mc.displayGuiScreen(null);

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    mc.displayGuiScreen(inputScreen);
                }
            };
            new java.util.Timer().schedule(task, 50);
        } else {
            mc.displayGuiScreen(inputScreen);
        }
    }

    public static void execGiveFromInput(String input) {
        String[] args = input.split(" ");
        GiveCommand giveCommand = new GiveCommand();
        giveCommand.exec(input);
    }
}
