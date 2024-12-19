package net.wheel.cutils.impl.gui.hud.component;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;

import net.wheel.cutils.api.gui.hud.component.DraggableHudComponent;
import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.impl.gui.hud.GuiHudEditor;

public final class KeyboardComponent extends DraggableHudComponent {

    public KeyboardComponent() {
        super("KeyInputs");
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (mc.player == null) {
            this.setW(Minecraft.getMinecraft().fontRenderer.getStringWidth("(keyboard)"));
            this.setW(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);
            if (mc.currentScreen instanceof GuiHudEditor) {
                mc.fontRenderer.drawStringWithShadow("(keyboard)", this.getX(), this.getY(), 0xFFAAAAAA);
            }
            return;
        }

        final boolean[] keysPressed = { Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown(),
                Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown(),
                Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown(),
                Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown() };

        final String[] keyNames = {
                String.valueOf(Minecraft.getMinecraft().gameSettings.keyBindForward.getDisplayName().charAt(0)),
                String.valueOf(Minecraft.getMinecraft().gameSettings.keyBindLeft.getDisplayName().charAt(0)),
                String.valueOf(Minecraft.getMinecraft().gameSettings.keyBindBack.getDisplayName().charAt(0)),
                String.valueOf(Minecraft.getMinecraft().gameSettings.keyBindRight.getDisplayName().charAt(0)) };

        this.setW(Minecraft.getMinecraft().fontRenderer.getStringWidth(keyNames[1] + keyNames[2] + keyNames[3]));
        this.setH(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * 2);

        if (!(mc.currentScreen instanceof GuiHudEditor) && this.getGlueSide() == null) {
            RenderUtil.drawRect(this.getX() - 1, this.getY() - 1, this.getX() + this.getW() + 1,
                    this.getY() + this.getH() + 1, 0x75101010);
        }

        final boolean isLocalSneaking = Minecraft.getMinecraft().player.isSneaking();
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(keyNames[0],
                this.getX() + (this.getW() / 2f)
                        - (Minecraft.getMinecraft().fontRenderer.getStringWidth(keyNames[0]) / 2f),
                this.getY(), keysPressed[0] ? (isLocalSneaking ? 0xFFFFFF55 : 0xFFFFFFFF) : 0xFF999999);
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(keyNames[1], this.getX(),
                this.getY() + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT,
                keysPressed[1] ? (isLocalSneaking ? 0xFFFFFF55 : 0xFFFFFFFF) : 0xFF999999);
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(keyNames[2],
                this.getX() + Minecraft.getMinecraft().fontRenderer.getStringWidth(keyNames[1]),
                this.getY() + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT,
                keysPressed[2] ? (isLocalSneaking ? 0xFFFFFF55 : 0xFFFFFFFF) : 0xFF999999);
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(keyNames[3],
                this.getX() + Minecraft.getMinecraft().fontRenderer.getStringWidth(keyNames[1] + keyNames[2]),
                this.getY() + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT,
                keysPressed[3] ? (isLocalSneaking ? 0xFFFFFF55 : 0xFFFFFFFF) : 0xFF999999);

        if (Mouse.isButtonDown(0)) {
            RenderUtil.drawRect(this.getX() + 1, this.getY() + 1, this.getX() + 3, this.getY() + 3, 0xFFFFFFFF);
        }
        if (Mouse.isButtonDown(1)) {
            RenderUtil.drawRect(this.getX() + this.getW() - 3, this.getY() + 1, this.getX() + this.getW() - 1,
                    this.getY() + 3, 0xFFFFFFFF);
        }
    }
}
