package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.realms.RealmsBridge;

import net.wheel.cutils.api.command.Command;

public final class DisconnectCommand extends Command {

    public DisconnectCommand() {
        super("Disconnect", new String[] { "Discon" }, "Disconnects from the current server", "Disconnect");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 1, 1)) {
            this.printUsage();
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();

        boolean flag = mc.isIntegratedServerRunning();
        boolean flag1 = mc.isConnectedToRealms();
        mc.world.sendQuittingDisconnectingPacket();
        mc.loadWorld(null);

        if (flag) {
            mc.displayGuiScreen(new GuiMainMenu());
        } else if (flag1) {
            RealmsBridge realmsbridge = new RealmsBridge();
            realmsbridge.switchToRealms(new GuiMainMenu());
        } else {
            mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
        }
    }
}
