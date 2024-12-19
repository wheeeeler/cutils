package net.wheel.cutils.api.gui.hud.component;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import lombok.Setter;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.gui.menu.GuiPasswordField;
import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.crack;

public class ConsoleComponent extends GuiScreen {

    private final GuiPasswordField inputField;
    private static final List<String> outputLines = new ArrayList<>();
    private static final List<String> commandHistory = new ArrayList<>();
    private int commandHistoryIndex = -1;
    private final int consoleHeight = 120;
    private int consoleWidth;
    private final int maxLines = 10;
    private final int margin = 2;
    private int scrollOffset = 0;
    private long consoleOpenedTime = 0;
    private boolean showCursor = true;

    private int selectionStartLine = -1;
    private int selectionStartChar = -1;
    private int selectionEndLine = -1;
    private int selectionEndChar = -1;
    private boolean selecting = false;

    @Setter
    private Process runningProcess = null;

    private long ignoreInputUntil = 0;

    public ConsoleComponent() {
        this.inputField = new GuiPasswordField(0, Minecraft.getMinecraft().fontRenderer, margin + 2, consoleHeight - 12,
                200, 10);
        updateConsoleDimensions();
        this.inputField.setFocused(true);
    }

    private void updateConsoleDimensions() {
        this.consoleWidth = width - (margin * 2);
        this.inputField.width = consoleWidth - 4;
    }

    @Override
    public void initGui() {
        updateConsoleDimensions();
        inputField.setText("");
        crack.setConsoleOpen(true);
        Keyboard.enableRepeatEvents(true);
        ignoreInputUntil = System.currentTimeMillis() + 100;
    }

    @Override
    public void onGuiClosed() {
        crack.setConsoleOpen(false);
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        handleMouseScroll();
        updateConsoleDimensions();
        drawConsoleBackground();
        drawConsoleOutput();
        drawCursor();

        String prompt = applyColor("➜  ", "\u00A7a") + applyColor("~ ", "\u00A7b");
        Minecraft.getMinecraft().fontRenderer.drawString(prompt, margin + 4, consoleHeight - 12, 0x00FF00);

        inputField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawConsoleBackground() {
        RenderUtil.drawRect(margin, 2, width - margin, consoleHeight, 0xFF131212);
    }

    private void drawConsoleOutput() {
        int y = 6;
        int lineStartIndex = Math.max(0, outputLines.size() - maxLines - scrollOffset);

        for (int i = lineStartIndex; i < outputLines.size() - scrollOffset; i++) {
            String line = outputLines.get(i);
            int color = 0xFFFFFF;

            if (selecting && isSelected(i, line)) {

                int startX = margin + 4;
                int endX = startX + Minecraft.getMinecraft().fontRenderer.getStringWidth(line);
                RenderUtil.drawRect(startX, y, endX, y + 10, 0xFF848484);
                color = 0xFFFFFF;
            }

            Minecraft.getMinecraft().fontRenderer.drawString(line, margin + 4, y, color);
            y += 10;
        }
    }

    private boolean isSelected(int lineIndex, String line) {
        if (selectionStartLine == -1 || selectionEndLine == -1) {
            return false;
        }

        if (lineIndex < selectionStartLine || lineIndex > selectionEndLine) {
            return false;
        }

        if (lineIndex == selectionStartLine && lineIndex == selectionEndLine) {
            return selectionStartChar <= selectionEndChar;
        }

        if (lineIndex == selectionStartLine) {
            return selectionStartChar < line.length();
        }

        if (lineIndex == selectionEndLine) {
            return selectionEndChar >= 0;
        }

        return true;
    }

    private void drawCursor() {
        if (System.currentTimeMillis() - consoleOpenedTime > 500) {
            showCursor = !showCursor;
            consoleOpenedTime = System.currentTimeMillis();
        }

        if (showCursor && inputField.isFocused()) {
            int cursorX = inputField.x + Minecraft.getMinecraft().fontRenderer.getStringWidth(inputField.getText());
            int cursorY = inputField.y;
            RenderUtil.drawRect(cursorX, cursorY, cursorX + 1, cursorY + inputField.height, 0xFFFFFFFF);
        }
    }

    private void handleMouseScroll() {
        int wheel = Mouse.getDWheel();
        if (wheel > 0) {
            if (scrollOffset < outputLines.size() - maxLines) {
                scrollOffset++;
            }
        } else if (wheel < 0) {
            if (scrollOffset > 0) {
                scrollOffset--;
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            inputField.mouseClicked(mouseX, mouseY, mouseButton);

            if (mouseButton == 0) {
                selecting = true;
                int[] start = getCharIndexAt(mouseX, mouseY);
                selectionStartLine = start[0];
                selectionStartChar = start[1];
                selectionEndLine = selectionStartLine;
                selectionEndChar = selectionStartChar;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        selecting = false;
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        if (selecting) {
            int[] end = getCharIndexAt(mouseX, mouseY);
            selectionEndLine = end[0];
            selectionEndChar = end[1];
        }
    }

    private int[] getCharIndexAt(int mouseX, int mouseY) {
        int y = 6;
        int lineStartIndex = Math.max(0, outputLines.size() - maxLines - scrollOffset);

        for (int i = lineStartIndex; i < outputLines.size() - scrollOffset; i++) {
            if (mouseY >= y && mouseY < y + 10) {
                String line = outputLines.get(i);
                int charIndex = 0;
                int x = margin + 4;

                for (char c : line.toCharArray()) {
                    int charWidth = Minecraft.getMinecraft().fontRenderer.getCharWidth(c);
                    if (mouseX >= x && mouseX < x + charWidth) {
                        return new int[] { i, charIndex };
                    }
                    x += charWidth;
                    charIndex++;
                }
                return new int[] { i, line.length() };
            }
            y += 10;
        }
        return new int[] { -1, -1 };
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        try {

            if (System.currentTimeMillis() < ignoreInputUntil) {
                return;
            }

            super.keyTyped(typedChar, keyCode);

            if (keyCode == Keyboard.KEY_UP) {
                navigateCommandHistory(1);
            } else if (keyCode == Keyboard.KEY_DOWN) {
                navigateCommandHistory(-1);
            } else if (keyCode == Keyboard.KEY_PRIOR) {
                scrollOffset = Math.max(0, scrollOffset - 10);
            } else if (keyCode == Keyboard.KEY_NEXT) {
                scrollOffset = Math.min(outputLines.size() - maxLines, scrollOffset + 10);
            } else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && keyCode == Keyboard.KEY_C) {
                copySelectedTextToClipboard();
            } else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && keyCode == Keyboard.KEY_T) {
                if (runningProcess != null) {
                    runningProcess.destroy();
                    runningProcess = null;
                    addConsoleOutput("killed threads", "\u00A7c");
                }
            } else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && keyCode == Keyboard.KEY_L) {
                for (int i = 0; i < 10; i++) {
                    addConsoleOutput("", "\u00A7f");
                }
            } else if (inputField.textboxKeyTyped(typedChar, keyCode)) {

            } else if (keyCode == Keyboard.KEY_RETURN) {
                String input = inputField.getText();
                runCommand(input);
                inputField.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copySelectedTextToClipboard() {
        if (selectionStartLine == -1 || selectionEndLine == -1) {
            return;
        }

        StringBuilder selectedText = new StringBuilder();
        for (int i = selectionStartLine; i <= selectionEndLine; i++) {
            String line = outputLines.get(i);
            int start = (i == selectionStartLine) ? selectionStartChar : 0;
            int end = (i == selectionEndLine) ? selectionEndChar : line.length();

            start = Math.max(0, Math.min(start, line.length()));
            end = Math.max(0, Math.min(end, line.length()));
            if (start > end) {
                int temp = start;
                start = end;
                end = temp;
            }

            selectedText.append(line, start, end).append("\n");
        }

        GuiScreen.setClipboardString(selectedText.toString());
    }

    private void runCommand(String input) {
        if (!input.isEmpty()) {
            String prompt = applyColor("$  ", "\u00A7a") + applyColor("~ ", "\u00A7b");

            commandHistory.add(input);
            commandHistoryIndex = -1;

            String commandName = input.split(" ")[0];
            Command command = crack.INSTANCE.getCommandManager().find(commandName);

            if (command != null) {
                addConsoleOutput(prompt + input, "\u00A7a");
                command.exec(input);
            } else {
                addConsoleOutput("cracksh: command not found: " + applyColor(input, "\u00A7c"), "\u00A7f");
            }

            scrollOffset = 0;
        }
    }

    private void navigateCommandHistory(int direction) {
        if (commandHistory.isEmpty())
            return;

        if (commandHistoryIndex == -1) {
            commandHistoryIndex = commandHistory.size();
        }

        commandHistoryIndex += direction;

        if (commandHistoryIndex < 0) {
            commandHistoryIndex = 0;
        } else if (commandHistoryIndex >= commandHistory.size()) {
            commandHistoryIndex = commandHistory.size() - 1;
        }

        inputField.setText(commandHistory.get(commandHistoryIndex));
    }

    public static void addConsoleOutput(String message, String colorCode) {
        List<String> wrappedText = Minecraft.getMinecraft().fontRenderer
                .listFormattedStringToWidth(applyColor(message, colorCode), Minecraft.getMinecraft().displayWidth - 6);
        outputLines.addAll(wrappedText);
        while (outputLines.size() > 1000) {
            outputLines.remove(0);
        }
    }

    private static String applyColor(String text, String colorCode) {
        return colorCode + text + "\u00A7r";
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
