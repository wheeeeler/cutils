package net.wheel.cutils.impl.gui.hud.component;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.wheel.cutils.api.gui.hud.component.DraggableHudComponent;

public final class TimeComponent extends DraggableHudComponent {

    public TimeComponent() {
        super("MR.Clock");
        this.setH(mc.fontRenderer.FONT_HEIGHT);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        final String hourMinute = new SimpleDateFormat("HH:mm").format(new Date());
        final String time = ChatFormatting.RESET + hourMinute;

        this.setW(mc.fontRenderer.getStringWidth(time));
        mc.fontRenderer.drawStringWithShadow(time, this.getX(), this.getY(), -1);
    }
}
