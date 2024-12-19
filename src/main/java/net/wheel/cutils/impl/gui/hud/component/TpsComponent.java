package net.wheel.cutils.impl.gui.hud.component;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.wheel.cutils.api.gui.hud.component.DraggableHudComponent;
import net.wheel.cutils.crack;

public final class TpsComponent extends DraggableHudComponent {

    public TpsComponent() {
        super("TpsView");
        this.setH(mc.fontRenderer.FONT_HEIGHT);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (mc.world != null && mc.getCurrentServerData() != null) {
            final String tps = String.format(ChatFormatting.GRAY + "TPS " + ChatFormatting.RESET + "%.2f",
                    crack.INSTANCE.getTickRateManager().getTickRate());
            this.setW(mc.fontRenderer.getStringWidth(tps));
            mc.fontRenderer.drawStringWithShadow(tps, this.getX(), this.getY(), -1);
        } else {
            this.setW(mc.fontRenderer.getStringWidth("(tps)"));
            mc.fontRenderer.drawStringWithShadow("(tps)", this.getX(), this.getY(), 0xFFAAAAAA);
        }
    }

}
