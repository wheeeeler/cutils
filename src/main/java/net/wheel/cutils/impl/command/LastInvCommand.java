package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.event.render.EventRender2D;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.module.GLOBAL.EnderChestModule;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class LastInvCommand extends Command {

    public LastInvCommand() {
        super("LastInv", new String[] { "EnderChest", "Echest", "Portable" },
                "Opens your previous inventory if \"MoreInv\" is enabled", "LastInv");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 1, 1)) {
            this.printUsage();
            return;
        }

        crack.INSTANCE.getEventManager().addEventListener(this);
    }

    @Listener
    public void render(EventRender2D event) {
        final EnderChestModule mod = (EnderChestModule) crack.INSTANCE.getModuleManager().find(EnderChestModule.class);
        if (mod != null) {
            if (mod.getScreen() != null) {
                Minecraft.getMinecraft().displayGuiScreen(mod.getScreen());
                crack.INSTANCE.logChat("Opening the last inventory.");
            } else {
                crack.INSTANCE.logChat("Inventory already closed.");
            }
        }
        crack.INSTANCE.getEventManager().removeEventListener(this);
    }

}
