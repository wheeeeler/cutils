package net.wheel.cutils.impl.module.CRACK;

import net.minecraft.client.Minecraft;

import net.wheel.cutils.api.event.player.EventCapeLocation;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class CapeModule extends Module {

    public CapeModule() {
        super("Cape", new String[] { "Capes" }, "custom cape", "NONE", -1, ModuleType.CRACK);
    }

    @Listener
    public void displayCape(EventCapeLocation event) {
        if (event.getPlayer() != null) {
            if (Minecraft.getMinecraft().player != null && event.getPlayer() == Minecraft.getMinecraft().player) {
                if (crack.INSTANCE.getCapeManager().getCape(event.getPlayer()) != null) {
                    event.setLocation(crack.INSTANCE.getCapeManager().getCape(event.getPlayer()));
                    event.setCanceled(true);
                }
            }
        }
    }

}
