package net.wheel.cutils.impl.module.LOCAL;

import java.util.Arrays;
import java.util.OptionalInt;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketSetPassengers;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventReceivePacket;
import net.wheel.cutils.api.event.network.EventSendPacket;
import net.wheel.cutils.api.event.player.EventPlayerUpdate;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class EntityDesyncModule extends Module {

    public final Value<Boolean> noDismountPlugin = new Value<Boolean>("NoDismountPlugin",
            new String[] { "NoDismount", "nd", "ndp", "AntiDismount" },
            "Prevents server plugin from dismounting you while riding", false);
    public final Value<Boolean> dismountEntity = new Value<Boolean>("DismountEntity",
            new String[] { "Dismount", "d", "de" }, "Dismounts the riding entity client-side (debug)", true);
    public final Value<Boolean> removeEntity = new Value<Boolean>("RemoveEntity", new String[] { "Remove", "r", "re" },
            "Removes the entity from the world client-side (debug)", true);
    public final Value<Boolean> respawnEntity = new Value<Boolean>("RespawnEntity",
            new String[] { "Respawn", "res", "resp" },
            "Forces the riding entity's 'isDead' value to be false on respawn (debug)", true);
    public final Value<Boolean> sendMovePackets = new Value<Boolean>("SendMovePackets",
            new String[] { "MovePackets", "sendmp", "SendMove", "sm" },
            "Sends CPacketVehicleMove packets for the riding entity (debug)", true);
    public final Value<Boolean> forceOnGround = new Value<Boolean>("ForceOnGround",
            new String[] { "ForceGound", "fog", "fg", "ground" },
            "Forces player.onGround = true when de-syncing (debug)", true);
    public final Value<Boolean> setMountPosition = new Value<Boolean>("SetMountPosition",
            new String[] { "SetMountPos", "setmp", "setmpos" },
            "Updates the riding entity position & bounding-box client-side (debug)", true);

    private Entity originalRidingEntity;

    public EntityDesyncModule() {
        super("EntityDesync", new String[] { "EntityDesync", "EDesync", "Desync" }, "NONE", -1, ModuleType.LOCAL);
        this.setDesc("Dismounts you from an entity client-side");
    }

    @Listener
    public void onReceivePacket(EventReceivePacket event) {
        if (event.getStage().equals(EventStageable.EventStage.POST)) {
            if (event.getPacket() instanceof SPacketSetPassengers) {
                if (this.hasOriginalRidingEntity() && Minecraft.getMinecraft().world != null) {
                    SPacketSetPassengers packetSetPassengers = (SPacketSetPassengers) event.getPacket();
                    if (this.originalRidingEntity
                            .equals(Minecraft.getMinecraft().world.getEntityByID(packetSetPassengers.getEntityId()))) {
                        OptionalInt isPlayerAPassenger = Arrays.stream(packetSetPassengers.getPassengerIds())
                                .filter(value -> Minecraft.getMinecraft().world
                                        .getEntityByID(value) == Minecraft.getMinecraft().player)
                                .findAny();
                        if (!isPlayerAPassenger.isPresent()) {
                            crack.INSTANCE.logChat("You've been dismounted.");
                            this.toggle();
                        }
                    }
                }
            }

            if (event.getPacket() instanceof SPacketDestroyEntities) {
                SPacketDestroyEntities packetDestroyEntities = (SPacketDestroyEntities) event.getPacket();
                boolean isEntityNull = Arrays.stream(packetDestroyEntities.getEntityIDs())
                        .filter(value -> value == originalRidingEntity.getEntityId()).findAny().isPresent();
                if (isEntityNull) {
                    crack.INSTANCE.logChat("The current riding entity is now null (destroyed or deleted).");
                }
            }
        }
    }

    @Listener
    public void onSendPacket(EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (this.noDismountPlugin.getValue()) {
                if (event.getPacket() instanceof CPacketPlayer.Position) {
                    event.setCanceled(true);
                    CPacketPlayer.Position packet = (CPacketPlayer.Position) event.getPacket();
                    Minecraft.getMinecraft().player.connection.sendPacket(new CPacketPlayer.PositionRotation(packet.x,
                            packet.y, packet.z, packet.yaw, packet.pitch, packet.onGround));
                }
                if (event.getPacket() instanceof CPacketPlayer
                        && !(event.getPacket() instanceof CPacketPlayer.PositionRotation))
                    event.setCanceled(true);
            }
        }
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        if (event.getStage().equals(EventStageable.EventStage.POST)) {
            final Minecraft mc = Minecraft.getMinecraft();
            if (mc.world != null && mc.player != null) {
                if (!mc.player.isRiding() && this.hasOriginalRidingEntity()) {
                    if (this.forceOnGround.getValue())
                        mc.player.onGround = true;

                    if (this.setMountPosition.getValue())
                        this.originalRidingEntity.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ);

                    if (this.sendMovePackets.getValue())
                        mc.player.connection.sendPacket(new CPacketVehicleMove(this.originalRidingEntity));
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.originalRidingEntity = null;

        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null && mc.world != null) {
            if (mc.player.isRiding()) {
                this.originalRidingEntity = mc.player.getRidingEntity();

                if (this.dismountEntity.getValue()) {
                    mc.player.dismountRidingEntity();
                    crack.INSTANCE.logChat("Dismounted entity.");
                }

                if (this.removeEntity.getValue()) {
                    mc.world.removeEntity(this.originalRidingEntity);
                    crack.INSTANCE.logChat("Removed entity from world.");
                }
            } else {
                crack.INSTANCE.logChat("Please mount an entity before enabling this module.");
                this.toggle();
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (this.hasOriginalRidingEntity()) {
            final Minecraft mc = Minecraft.getMinecraft();

            if (this.respawnEntity.getValue())
                this.originalRidingEntity.isDead = false;

            if (!mc.player.isRiding()) {
                mc.world.spawnEntity(this.originalRidingEntity);
                mc.player.startRiding(this.originalRidingEntity, true);
                crack.INSTANCE.logChat("Spawned & mounted original entity.");
            }

            this.originalRidingEntity = null;
        }
    }

    private boolean hasOriginalRidingEntity() {
        return this.originalRidingEntity != null;
    }
}
