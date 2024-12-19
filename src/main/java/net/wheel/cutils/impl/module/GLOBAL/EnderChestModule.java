package net.wheel.cutils.impl.module.GLOBAL;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.util.EnumHand;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.minecraft.EventDisplayGui;
import net.wheel.cutils.api.event.network.EventReceivePacket;
import net.wheel.cutils.api.event.network.EventSendPacket;
import net.wheel.cutils.api.event.player.EventPlayerUpdate;
import net.wheel.cutils.api.event.player.EventRightClickBlock;
import net.wheel.cutils.api.event.world.EventLoadWorld;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class EnderChestModule extends Module {

    @Getter
    @Setter
    private GuiContainer screen;

    private boolean enderChestDesynced;
    private boolean hideEnderChestGui = false;

    public EnderChestModule() {
        super("EnderChest",
                new String[] { "EnderChest", "EChest", "InfiniteEChest", "InfiniteEnderChest", "InfEChest" },
                "Replaces your inventory with a view of your Ender Chest after you open it", "NONE", -1,
                ModuleType.GLOBAL);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (Minecraft.getMinecraft().world != null) {
            Minecraft.getMinecraft().player.connection
                    .sendPacket(new CPacketCloseWindow(Minecraft.getMinecraft().player.inventoryContainer.windowId));
            this.resetBackupEnderChest();
            crack.INSTANCE.logChat("Container closed.");
        }
    }

    @Listener
    public void loadWorld(EventLoadWorld event) {
        if (Minecraft.getMinecraft().world != null) {
            Minecraft.getMinecraft().player.connection
                    .sendPacket(new CPacketCloseWindow(Minecraft.getMinecraft().player.inventoryContainer.windowId));
            this.resetBackupEnderChest();
        }
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        if (event.getStage() != EventStageable.EventStage.PRE)
            return;

        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null)
            return;

        if (this.enderChestDesynced && this.hideEnderChestGui) {
            if (mc.currentScreen instanceof GuiContainer && !(mc.currentScreen instanceof GuiInventory)) {
                mc.currentScreen = null;
                this.hideEnderChestGui = false;
            }
        }
    }

    @Listener
    public void onRightClickBlock(EventRightClickBlock event) {
        if (event.getPos() != null) {
            final Block block = Minecraft.getMinecraft().world.getBlockState(event.getPos()).getBlock();
            if (block == Blocks.ENDER_CHEST) {
                final float deltaX = (float) (event.getVec().x - (double) event.getPos().getX());
                final float deltaY = (float) (event.getVec().y - (double) event.getPos().getY());
                final float deltaZ = (float) (event.getVec().z - (double) event.getPos().getZ());
                Minecraft.getMinecraft().player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(event.getPos(),
                        event.getFacing(), EnumHand.MAIN_HAND, deltaX, deltaY, deltaZ));
                this.enderChestDesynced = true;
            } else if (block instanceof BlockContainer) {
                this.resetBackupEnderChest();
            }
        }
    }

    @Listener
    public void onDisplayGui(EventDisplayGui event) {
        if (event.getScreen() instanceof GuiContainer && !(event.getScreen() instanceof GuiInventory)) {
            this.screen = (GuiContainer) event.getScreen();
        } else if (event.getScreen() instanceof GuiInventory) {
            if (this.enderChestDesynced && this.getScreen() != null) {
                this.hideEnderChestGui = false;
                event.setCanceled(true);
                Minecraft.getMinecraft().displayGuiScreen(this.screen);
            }
        }
    }

    @Listener
    public void onReceivePacket(EventReceivePacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof SPacketCloseWindow) {
                final SPacketCloseWindow packetCloseWindow = (SPacketCloseWindow) event.getPacket();
                if (this.getScreen() != null
                        && packetCloseWindow.windowId == this.getScreen().inventorySlots.windowId) {
                    this.resetBackupEnderChest();
                    crack.INSTANCE.logChat("Container closed.");
                }
            }
        }
    }

    @Listener
    public void onSendPacket(EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof CPacketCloseWindow) {
                final CPacketCloseWindow packetCloseWindow = (CPacketCloseWindow) event.getPacket();
                if (this.getScreen() != null && this.enderChestDesynced) {
                    event.setCanceled(true);
                }
            } else if (event.getPacket() instanceof CPacketClickWindow) {
                final CPacketClickWindow packetClickWindow = (CPacketClickWindow) event.getPacket();
                if (packetClickWindow.getClickType().equals(ClickType.THROW) && this.enderChestDesynced
                        && this.getScreen() != null) {
                    this.hideEnderChestGui = true;
                } else if (packetClickWindow.getClickType().equals(ClickType.THROW) && !this.enderChestDesynced) {
                    this.resetBackupEnderChest();
                }
            }
        }
    }

    public boolean hasBackupEnderChest() {
        return this.enderChestDesynced;
    }

    public void resetBackupEnderChest() {
        this.enderChestDesynced = false;
        this.hideEnderChestGui = false;
        this.setScreen(null);
    }

}
