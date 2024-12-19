package net.wheel.cutils.impl.module.hidden;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;

import net.wheel.cutils.api.chatrelayhandler.ChatRelayManager;
import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.event.player.EventChatKeyTyped;
import net.wheel.cutils.api.event.player.EventSendChatMessage;
import net.wheel.cutils.api.event.render.EventRender2D;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class CommandsModule extends Module {

    @Getter
    public final Value<String> prefix = new Value<>("Prefix", new String[] { "prefx", "pfx" }, "prefix", ".");
    public final Value<Boolean> predictions = new Value<>("Predictions", new String[] { "predict", "pre" },
            "command predictions", true);

    private final ObjectArrayList<String> suggestions = new ObjectArrayList<>();
    private int suggestionIndex = -1;
    private String lastInput = "";
    private boolean isNavigatingSuggestions = false;
    private boolean suggestionsVisible = false;

    public CommandsModule() {
        super("Commands", new String[] { "cmds", "cmd" }, "c", "NONE", -1, ModuleType.HIDDEN);
        this.setHidden(true);
        this.toggle();
    }

    @Listener
    public void onRender2D(EventRender2D event) {
        if (!predictions.getValue() || suggestions.isEmpty())
            return;

        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || !(mc.currentScreen instanceof GuiChat))
            return;

        GuiChat chatGui = (GuiChat) mc.currentScreen;
        GuiTextField inputField = chatGui.inputField;
        String input = inputField.getText();

        if (!input.startsWith(prefix.getValue()))
            return;

        final int maxDisplayCount = 20;
        final int displayedSuggestions = Math.min(suggestions.size(), maxDisplayCount);

        final int x = 2;
        final int width = 180;

        final int lineHeight = 14;
        final int y = event.getScaledResolution().getScaledHeight() - 20 - (displayedSuggestions * lineHeight);
        final int height = displayedSuggestions * lineHeight;

        final int borderColor = 0xFF555555;
        final int backgroundColor = 0xAA1E1E1E;

        RenderUtil.drawBorderedRect(x, y, x + width, y + height, 1.0f, backgroundColor, borderColor);

        for (int i = 0; i < displayedSuggestions; i++) {
            String suggestion = suggestions.get(i);
            int color = (i == suggestionIndex) ? 0xFFFFFF : 0xBBBBBB;
            mc.fontRenderer.drawStringWithShadow(suggestion, x + 6, y + i * lineHeight + 2, color);
        }
    }

    @Listener
    public void onChatKeyTyped(EventChatKeyTyped event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.currentScreen instanceof GuiChat))
            return;

        GuiChat chatGui = (GuiChat) mc.currentScreen;
        GuiTextField inputField = chatGui.inputField;
        String input = inputField.getText();
        int keyCode = event.getKeyCode();

        if (!input.startsWith(prefix.getValue())) {
            suggestionsVisible = false;
            suggestionIndex = -1;
            isNavigatingSuggestions = false;
            return;
        }

        if (!input.equals(lastInput)) {
            lastInput = input;
            populateSuggestions(input);
            suggestionIndex = -1;
            isNavigatingSuggestions = false;
        }

        if (suggestionsVisible
                && (keyCode == Keyboard.KEY_TAB || keyCode == Keyboard.KEY_UP || keyCode == Keyboard.KEY_DOWN)) {
            event.setCanceled(true);

            if (keyCode == Keyboard.KEY_TAB) {
                if (!suggestions.isEmpty()) {
                    applySuggestion(suggestions.get(Math.max(suggestionIndex, 0)));
                    suggestionsVisible = false;
                    suggestionIndex = -1;
                }
            } else if (keyCode == Keyboard.KEY_UP) {
                navigateSuggestions(-1);
            } else if (keyCode == Keyboard.KEY_DOWN) {
                navigateSuggestions(1);
            }
        } else if (keyCode == Keyboard.KEY_TAB && !suggestionsVisible) {
            event.setCanceled(true);
            if (!suggestions.isEmpty()) {
                suggestionsVisible = true;
                suggestionIndex = -1;
            }
        } else {
            suggestionIndex = -1;
            isNavigatingSuggestions = false;
            suggestionsVisible = !suggestions.isEmpty();
        }
    }

    @Listener
    public void sendChatMessage(EventSendChatMessage event) {
        String message = event.getMessage();

        if (message.startsWith("!")) {
            ChatRelayManager.getInstance().sendMessage(message.substring(1));
            event.setCanceled(true);
            return;
        }

        if (message.startsWith(this.prefix.getValue())) {
            String commandInput = message.substring(this.prefix.getValue().length()).trim();

            Command command = crack.INSTANCE.getCommandManager().find(commandInput.split(" ")[0]);
            if (command != null) {
                try {
                    command.exec(commandInput);
                } catch (Exception e) {
                    crack.INSTANCE.errorChat("Error executing command: " + command.getDisplayName());
                }
            } else {
                crack.INSTANCE.errorChat("Unknown command: \"" + commandInput + "\"");
            }

            event.setCanceled(true);
        }
    }

    private void populateSuggestions(String input) {
        suggestions.clear();

        if (input.isEmpty() || !input.startsWith(prefix.getValue())) {
            suggestionsVisible = false;
            return;
        }

        GuiChat chatGui = (GuiChat) Minecraft.getMinecraft().currentScreen;
        GuiTextField inputField = chatGui.inputField;
        int cursorPosition = inputField.getCursorPosition();

        String inputUpToCursor = input.substring(0, cursorPosition);

        String commandText = inputUpToCursor.substring(prefix.getValue().length());

        String[] splitInput = commandText.split("\\s+");
        int wordIndex = getWordIndex(commandText, cursorPosition - prefix.getValue().length());
        String currentWord = getCurrentWord(commandText, cursorPosition - prefix.getValue().length());

        if (wordIndex == 0) {
            for (Command cmd : crack.INSTANCE.getCommandManager().getCommandList()) {
                if (cmd.getDisplayName().toLowerCase().startsWith(currentWord.toLowerCase())) {
                    suggestions.add(cmd.getDisplayName());
                }
            }
        } else {
            String baseCommandName = splitInput[0];
            Command command = crack.INSTANCE.getCommandManager().find(baseCommandName);

            if (command != null) {
                String[] args = Arrays.copyOfRange(splitInput, 0, wordIndex + 1);
                List<String> argSuggestions = command.getCommandArgs(args);
                if (argSuggestions != null) {
                    for (String arg : argSuggestions) {
                        if (arg.toLowerCase().startsWith(currentWord.toLowerCase())) {
                            suggestions.add(arg);
                        }
                    }
                }
            }
        }

        if (!suggestions.isEmpty() || (currentWord.isEmpty() && wordIndex == 0)) {
            suggestionsVisible = true;
            suggestionIndex = -1;
        } else {
            suggestionsVisible = false;
        }
    }

    private int getWordIndex(String text, int cursorPosition) {
        int index = 0;
        int wordStart = 0;
        boolean inWord = false;

        for (int i = 0; i <= cursorPosition && i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == ' ') {
                if (inWord) {
                    index++;
                    inWord = false;
                }
                wordStart = i + 1;
            } else {
                inWord = true;
            }
        }

        return index;
    }

    private String getCurrentWord(String text, int cursorPosition) {
        int wordStart = cursorPosition - 1;
        while (wordStart >= 0 && text.charAt(wordStart) != ' ') {
            wordStart--;
        }
        wordStart++;

        int wordEnd = cursorPosition;
        while (wordEnd < text.length() && text.charAt(wordEnd) != ' ') {
            wordEnd++;
        }

        return text.substring(wordStart, wordEnd);
    }

    private void applySuggestion(String suggestion) {
        GuiChat chatGui = (GuiChat) Minecraft.getMinecraft().currentScreen;
        if (chatGui == null)
            return;

        GuiTextField inputField = chatGui.inputField;
        String input = inputField.getText();
        int cursorPosition = inputField.getCursorPosition();

        String commandText = input.substring(prefix.getValue().length(), cursorPosition);

        int wordStart = cursorPosition - 1;
        while (wordStart >= prefix.getValue().length() && input.charAt(wordStart) != ' ') {
            wordStart--;
        }
        wordStart++;

        String beforeWord = input.substring(0, wordStart);
        String afterCursor = input.substring(cursorPosition);

        String newInput = beforeWord + suggestion + afterCursor;

        inputField.setText(newInput);
        inputField.setCursorPosition(beforeWord.length() + suggestion.length());
    }

    private void navigateSuggestions(int direction) {
        if (suggestions.isEmpty())
            return;

        suggestionIndex = (suggestionIndex + direction + suggestions.size()) % suggestions.size();
        isNavigatingSuggestions = true;
    }

}
