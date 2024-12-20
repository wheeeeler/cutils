package net.wheel.cutils.api.event.player;

import net.wheel.cutils.api.event.EventCancellable;

public final class EventChatKeyTyped extends EventCancellable {

    private char typedChar;
    private int keyCode;

    public EventChatKeyTyped(char typedChar, int keyCode) {
        this.typedChar = typedChar;
        this.keyCode = keyCode;
    }

    public char getTypedChar() {
        return typedChar;
    }

    public void setTypedChar(char typedChar) {
        this.typedChar = typedChar;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }
}
