package net.wheel.cutils.impl.module.CRACK;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventSendPacket;
import net.wheel.cutils.api.module.Module;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class HandSpoofModule extends Module {

    private BlockPos position;
    private EnumFacing placedBlockDirection;
    private EnumHand hand;
    private float facingX;
    private float facingY;
    private float facingZ;
    private boolean send;
    private Entity entity;

    public HandSpoofModule() {
        super("HandSpoof", new String[] { "ItemS" }, "aAAaa", "NONE", -1, ModuleType.CRACK);
    }

    @Listener
    public void sendPacket(EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            final Minecraft mc = Minecraft.getMinecraft();
            if (mc.player == null)
                return;

            if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
                handleItemOnBlock((CPacketPlayerTryUseItemOnBlock) event.getPacket());
            } else if (event.getPacket() instanceof CPacketPlayerTryUseItem) {
                handleItemUse((CPacketPlayerTryUseItem) event.getPacket());
            } else if (event.getPacket() instanceof CPacketUseEntity) {
                handleEntityUse((CPacketUseEntity) event.getPacket());
            }
        }
    }

    private void handleItemOnBlock(CPacketPlayerTryUseItemOnBlock packet) {
        if (send) {
            send = false;
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();
        this.position = packet.getPos();
        this.placedBlockDirection = packet.getDirection();
        this.hand = packet.getHand();
        this.facingX = packet.getFacingX();
        this.facingY = packet.getFacingY();
        this.facingZ = packet.getFacingZ();

        if (this.position != null) {
            eventSwap(packet);
        }
    }

    private void handleItemUse(CPacketPlayerTryUseItem packet) {
        if (send) {
            send = false;
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();
        this.hand = packet.getHand();

        eventSwap(packet);
    }

    private void handleEntityUse(CPacketUseEntity packet) {
        if (send) {
            send = false;
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();

        if (packet.getAction() == CPacketUseEntity.Action.ATTACK) {
            this.entity = packet.getEntityFromWorld(mc.world);

            if (this.entity != null) {
                eventSwap(packet);
            }
        }
    }

    private void eventSwap(Object packet) {
        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.player == null)
            return;

        int spoofSlot = 9;
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, spoofSlot,
                mc.player.inventory.currentItem, ClickType.SWAP, mc.player);

        this.send = true;

        if (packet instanceof CPacketPlayerTryUseItemOnBlock) {
            CPacketPlayerTryUseItemOnBlock blockPacket = (CPacketPlayerTryUseItemOnBlock) packet;
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.position, this.placedBlockDirection,
                    this.hand, this.facingX, this.facingY, this.facingZ));
        } else if (packet instanceof CPacketPlayerTryUseItem) {
            CPacketPlayerTryUseItem itemPacket = (CPacketPlayerTryUseItem) packet;
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(this.hand));
        } else if (packet instanceof CPacketUseEntity) {
            CPacketUseEntity entityPacket = (CPacketUseEntity) packet;
            mc.player.connection.sendPacket(new CPacketUseEntity(this.entity));
        }

        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, spoofSlot,
                mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
    }
}
