package net.wheel.cutils.impl.module.LOCAL;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketCloseWindow;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventSendPacket;
import net.wheel.cutils.api.module.Module;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class InventoryExtenderModule extends Module {

    public InventoryExtenderModule() {
        super("InvExtender", new String[] { "XCarry", "extendedInv" },
                "Allows you to carry items in your crafting and dragging slot", "NONE", -1, ModuleType.LOCAL);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (Minecraft.getMinecraft().world != null) {
            Minecraft.getMinecraft().player.connection
                    .sendPacket(new CPacketCloseWindow(Minecraft.getMinecraft().player.inventoryContainer.windowId));
        }
    }

    @Listener
    public void sendPacket(EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof CPacketCloseWindow) {
                final CPacketCloseWindow packet = (CPacketCloseWindow) event.getPacket();
                if (packet.windowId == Minecraft.getMinecraft().player.inventoryContainer.windowId) {
                    event.setCanceled(true);
                }
            }
        }
    }

}
