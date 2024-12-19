package net.wheel.cutils.impl.module.LOCAL;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventSendPacket;
import net.wheel.cutils.api.event.player.EventClickBlock;
import net.wheel.cutils.api.event.player.EventExtendPlayerReach;
import net.wheel.cutils.api.event.player.EventPlayerReach;
import net.wheel.cutils.api.event.player.EventRightClickBlock;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class ReachModule extends Module {

    public final Value<Float> distance = new Value<Float>("Distance", new String[] { "Dist", "D" }, "distance", 5.5f,
            0.0f, 10.0f, 0.5f);
    public final Value<Boolean> teleport = new Value<Boolean>("Teleport", new String[] { "Tp", "Tele" }, "reach far",
            false);

    private final Minecraft mc = Minecraft.getMinecraft();

    public ReachModule() {
        super("ReachModule", new String[] { "Rch" }, "extend reach", "NONE", -1, ModuleType.LOCAL);
    }

    private float getVanillaReach() {
        float attrib = (float) this.mc.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        return mc.player.isCreative() ? attrib : attrib - 0.5F;
    }

    @Listener
    public void onPlayerReach(EventPlayerReach event) {
        event.setReach(this.distance.getValue());
        event.setCanceled(true);
    }

    @Listener
    public void onExtendReach(EventExtendPlayerReach event) {
        event.setCanceled(true);
    }

    @Listener
    public void onClickBlock(EventClickBlock event) {
        if (event.getPos() != null && mc.player.getDistance(event.getPos().getX(), event.getPos().getY(),
                event.getPos().getZ()) > getVanillaReach() && this.teleport.getValue()) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(event.getPos().getX(), event.getPos().getY() + 2,
                    event.getPos().getZ(), true));
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK,
                    event.getPos(), event.getFace()));
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                    event.getPos(), event.getFace()));
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.connection
                    .sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
            event.setCanceled(true);
        }
    }

    @Listener
    public void onRightClickBlock(EventRightClickBlock event) {
        if (event.getPos() != null && mc.player.getDistance(event.getPos().getX(), event.getPos().getY(),
                event.getPos().getZ()) > getVanillaReach() && this.teleport.getValue()) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(event.getPos().getX(), event.getPos().getY() + 2,
                    event.getPos().getZ(), true));
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(event.getPos(), event.getFacing(),
                    EnumHand.MAIN_HAND, 0.5F, 0.5F, 0.5F));
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.connection
                    .sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
            event.setCanceled(true);
        }
    }

    @Listener
    public void sendPacket(EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof CPacketUseEntity) {
                final CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
                final Entity entity = mc.world.getEntityByID(packet.entityId);
                if (entity != null && mc.player.getDistance(entity.posX, entity.posY, entity.posZ) > getVanillaReach()
                        && this.teleport.getValue()) {
                    mc.player.connection
                            .sendPacket(new CPacketPlayer.Position(entity.posX, entity.posY + 2, entity.posZ, true));
                }
            }
        }
    }
}
