package net.wheel.cutils.api.task.hand;

import net.minecraft.client.Minecraft;

public final class HandSwapContext {
    private int oldSlot;
    private int newSlot;

    public HandSwapContext(int oldSlot, int newSlot) {
        this.oldSlot = oldSlot;
        this.newSlot = newSlot;
    }

    public int getOldSlot() {
        return oldSlot;
    }

    public void setOldSlot(int oldSlot) {
        this.oldSlot = oldSlot;
    }

    public int getNewSlot() {
        return newSlot;
    }

    public void setNewSlot(int newSlot) {
        this.newSlot = newSlot;
    }

    public void handleHandSwap(final boolean restore,
            final Minecraft minecraft) {
        minecraft.player.inventory.currentItem = restore ? this.getOldSlot() : this.getNewSlot();
        minecraft.playerController.updateController();
    }
}
