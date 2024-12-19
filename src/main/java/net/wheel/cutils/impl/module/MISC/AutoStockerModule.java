package net.wheel.cutils.impl.module.MISC;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.player.EventUpdateWalkingPlayer;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.util.Timer;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public class AutoStockerModule extends Module {
    public final Value<Float> delay = new Value<>("Delay", new String[] { "Del" },
            "The delay(ms) per item transfer to hot-bar", 500.0f, 0.0f, 2000.0f, 1.0f);
    public final Value<Integer> percentage = new Value<>("RefillPercentage", new String[] { "percent", "p", "percent" },
            "The percentage a slot should be filled to get refilled", 50, 0, 100, 1);
    public final Value<Boolean> offHand = new Value<>("OffHand", new String[] { "oh", "off", "hand" },
            "If the off hand should be refilled", true);

    private final Timer timer = new Timer();

    public AutoStockerModule() {
        super("AutoStocker", new String[] { "Replenish", "Refill", "AutoHotBar", "hbr", "Restock", "HBRestock",
                "HBRefill", "Hotbar", "Hot-bar" }, "NONE", -1, ModuleType.MISC);
        this.setDesc("Automatically refills the players hot-bar.");
    }

    @Listener
    public void onWalkingUpdate(EventUpdateWalkingPlayer event) {
        if (this.timer.passed(this.delay.getValue())) {
            if (event.getStage() == EventStageable.EventStage.PRE) {
                Minecraft mc = Minecraft.getMinecraft();

                if (mc.currentScreen instanceof GuiInventory) {
                    return;
                }

                int toRefill = getRefillable(mc.player);
                if (toRefill != -1) {
                    refillHotbarSlot(mc, toRefill);
                }
            }

            timer.reset();
        }
    }

    private int getRefillable(EntityPlayerSP player) {
        if (offHand.getValue()) {
            if (player.getHeldItemOffhand().getItem() != Items.AIR
                    && player.getHeldItemOffhand().getCount() < player.getHeldItemOffhand().getMaxStackSize()
                    && (double) player.getHeldItemOffhand().getCount()
                            / player.getHeldItemOffhand().getMaxStackSize() <= (percentage.getValue() / 100.0)) {
                return 45;
            }
        }

        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.inventory.mainInventory.get(i);
            if (stack.getItem() != Items.AIR && stack.getCount() < stack.getMaxStackSize()
                    && (double) stack.getCount() / stack.getMaxStackSize() <= (percentage.getValue() / 100.0)) {
                return i;
            }
        }

        return -1;
    }

    private int getSmallestStack(EntityPlayerSP player, ItemStack itemStack) {
        if (itemStack == null) {
            return -1;
        }

        int minCount = itemStack.getMaxStackSize() + 1;
        int minIndex = -1;

        for (int i = 9; i < player.inventory.mainInventory.size(); i++) {
            ItemStack stack = player.inventory.mainInventory.get(i);

            if (stack.getItem() != Items.AIR
                    && stack.getItem() == itemStack.getItem()
                    && stack.getCount() < minCount) {

                minCount = stack.getCount();
                minIndex = i;
            }
        }

        return minIndex;
    }

    public void refillHotbarSlot(Minecraft mc, int slot) {
        ItemStack stack;
        if (slot == 45) {
            stack = mc.player.getHeldItemOffhand();
        } else {
            stack = mc.player.inventory.mainInventory.get(slot);
        }

        if (stack.getItem() == Items.AIR) {
            return;
        }

        int biggestStack = getSmallestStack(mc.player, stack);
        if (biggestStack == -1) {
            return;
        }

        if (slot == 45) {
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, biggestStack, 0, ClickType.PICKUP,
                    mc.player);
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, biggestStack, 0, ClickType.PICKUP,
                    mc.player);
            return;
        }

        int overflow = -1;
        for (int i = 0; i < 9 && overflow == -1; i++) {
            if (mc.player.inventory.mainInventory.get(i).getItem() == Items.AIR) {
                overflow = i;
            }
        }

        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, biggestStack, 0, ClickType.QUICK_MOVE,
                mc.player);

        if (overflow != -1 && mc.player.inventory.mainInventory.get(overflow).getItem() != Items.AIR) {
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, biggestStack, overflow,
                    ClickType.SWAP, mc.player);
        }
    }
}
