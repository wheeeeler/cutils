package net.wheel.cutils.api.gui.menu;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import net.wheel.cutils.impl.command.GiveExtendedCommand;

public class GiveInputBox extends GuiScreen {
    private GuiTextField inputField;
    private final GuiScreen parentScreen;

    public GiveInputBox(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        int inputFieldWidth = 390;
        this.inputField = new GuiTextField(0, this.fontRenderer, this.width / 2 - inputFieldWidth / 2,
                this.height / 2 - 20, inputFieldWidth, 20);
        this.inputField.setMaxStringLength(Integer.MAX_VALUE);
        this.inputField.setFocused(true);

        this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 2 + 20, 98, 20, "YEA"));
        this.addButton(new GuiButton(1, this.width / 2 + 2, this.height / 2 + 20, 98, 20, "NAY"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            String input = inputField.getText();
            Minecraft.getMinecraft().displayGuiScreen(this.parentScreen);
            GiveExtendedCommand.execGiveFromInput(input);
        } else if (button.id == 1) {
            Minecraft.getMinecraft().displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
        this.inputField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.inputField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.inputField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
