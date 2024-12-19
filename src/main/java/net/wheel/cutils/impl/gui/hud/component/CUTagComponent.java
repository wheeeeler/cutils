package net.wheel.cutils.impl.gui.hud.component;

import net.minecraft.client.Minecraft;

import net.wheel.cutils.api.gui.hud.component.DraggableHudComponent;
import net.wheel.cutils.impl.fml.cUtils;

public final class CUTagComponent extends DraggableHudComponent {

    private final String brandTag = "crackutils " + cUtils.VERSION;

    public CUTagComponent() {
        super("CUWatermark");
        this.setH(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);
    }

    private int getLetterColor(int letterIndex, long time) {
        int currentIndex = (int) ((time / 225) % brandTag.length());
        return letterIndex == currentIndex ? 0xFFFF0000 : 0xFFFFFFFF;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        this.setW(Minecraft.getMinecraft().fontRenderer.getStringWidth(brandTag));

        long currentTime = System.currentTimeMillis();
        int x = (int) this.getX();

        for (int i = 0; i < brandTag.length(); i++) {
            int letterColor = getLetterColor(i, currentTime);
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(String.valueOf(brandTag.charAt(i)), x,
                    this.getY(), letterColor);
            x += Minecraft.getMinecraft().fontRenderer.getCharWidth(brandTag.charAt(i));
        }
    }
}
