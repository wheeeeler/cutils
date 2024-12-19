package net.wheel.cutils.impl.gui.hud.component;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.wheel.cutils.api.chatrelayhandler.ChatRelayManager;
import net.wheel.cutils.api.gui.hud.component.DraggableHudComponent;

public final class RelayConnectionComponent extends DraggableHudComponent {

    public RelayConnectionComponent() {
        super("RelayConnection");
        this.setH(mc.fontRenderer.FONT_HEIGHT);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        ChatRelayManager manager = ChatRelayManager.getInstance();

        String status = ChatFormatting.WHITE + "Crack Relay: ";
        int color;

        if (manager == null) {
            status += ChatFormatting.YELLOW + "Error/Unknown";
            color = 0xFFFF00;
        } else if (manager.isConnected()) {
            status += ChatFormatting.GREEN + "Connected";
            color = 0x00FF00;
        } else {
            status += ChatFormatting.RED + "Disconnected";
            color = 0xFF0000;
        }

        this.setW(mc.fontRenderer.getStringWidth(status));
        mc.fontRenderer.drawStringWithShadow(status, this.getX(), this.getY(), color);
    }

}
