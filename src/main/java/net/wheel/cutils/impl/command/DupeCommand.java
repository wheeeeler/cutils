package net.wheel.cutils.impl.command;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.NonNullList;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;

public final class DupeCommand extends Command {

    private final ExecutorService dupeExecutor = Executors.newSingleThreadExecutor();

    public DupeCommand() {
        super("Dupe", new String[] { "Dup", "Doop" }, "Attempts to dupe your inventory using an updated method",
                "Dupe");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 1, 1)) {
            this.printUsage();
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.player != null) {
            dupeExecutor.execute(() -> performDupe(mc));
        }
    }

    private void performDupe(Minecraft mc) {
        try {
            NonNullList<ItemStack> inventoryStacks = mc.player.inventory.mainInventory;

            for (int i = 0; i <= 45; i++) {
                if (!inventoryStacks.get(i).isEmpty()) {
                    sendClickWindowPacket(mc, i, -999, ClickType.PICKUP);
                    Thread.sleep(10);
                }
            }

            for (ItemStack stack : inventoryStacks) {
                if (!stack.isEmpty()) {
                    sendCreativeInventoryAction(mc, stack);
                }
            }

            mc.player.connection.sendPacket(new CPacketUseEntity(mc.player));

            crack.INSTANCE.errorChat(":-)");

            shutdownExecutor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendClickWindowPacket(Minecraft mc, int slotId, int mouseButton, ClickType clickType) {
        mc.player.connection.sendPacket(new CPacketClickWindow(mc.player.inventoryContainer.windowId, slotId,
                mouseButton, clickType, ItemStack.EMPTY, (short) 0));
    }

    private void sendCreativeInventoryAction(Minecraft mc, ItemStack stack) {
        mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(0, stack));
    }

    private void shutdownExecutor() {
        if (!dupeExecutor.isShutdown()) {
            dupeExecutor.shutdown();
        }
    }
}
