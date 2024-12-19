package net.wheel.cutils.impl.module.WAR;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;

import net.wheel.cutils.api.event.minecraft.EventDisplayGui;
import net.wheel.cutils.api.module.Module;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class RespawnModule extends Module {

    public RespawnModule() {
        super("AutoRespawn", new String[] { "Respawn", "Resp" }, "Automatically respawn after death", "NONE", -1,
                ModuleType.WAR);
    }

    @Listener
    public void displayGuiScreen(EventDisplayGui event) {
        if (event.getScreen() != null && event.getScreen() instanceof GuiGameOver) {
            Minecraft.getMinecraft().player.respawnPlayer();
        }
    }

}
