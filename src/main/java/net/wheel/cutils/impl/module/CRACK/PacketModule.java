package net.wheel.cutils.impl.module.CRACK;

import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketDestroyEntities;

import net.wheel.cutils.api.event.network.EventReceivePacket;
import net.wheel.cutils.api.event.network.EventSendPacket;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.handler.ListenerPriority;
import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class PacketModule extends Module {

    public final Value<Boolean> player = new Value<Boolean>("Player", new String[] { "pos", "p" },
            "Cancel all player related MVMT packets", false);
    public final Value<Boolean> playerPosition = new Value<Boolean>("Position", new String[] { "pos", "p" },
            "Cancel player position packets", false);
    public final Value<Boolean> playerRotation = new Value<Boolean>("Rotation", new String[] { "rot", "r" },
            "Cancel player rotation packets", false);
    public final Value<Boolean> playerPosRot = new Value<Boolean>("PositionRotation", new String[] { "PosRot", "pr" },
            "Cancel player position & rotation packets", false);
    public final Value<Boolean> steerBoat = new Value<Boolean>("SteerBoat", new String[] { "steer", "boat", "sb" },
            "Cancel boat steering packets", false);
    public final Value<Boolean> vehicleMove = new Value<Boolean>("VehicleMove",
            new String[] { "vehicle", "vehicle-move", "vm", "move" }, "Cancel vehicle MVMT packets", false);
    public final Value<Boolean> input = new Value<Boolean>("Input", new String[] { "in", "i" },
            "Cancel player input packets", false);
    public final Value<Boolean> abilities = new Value<Boolean>("Abilities",
            new String[] { "abilities", "player", "ability", "pa" }, "Cancel \"player-ability\" packets", false);
    public final Value<Boolean> removeEntity = new Value<Boolean>("RemoveEntity",
            new String[] { "DestroyEntities", "RemoveEntities", "remove", "destroy", "re", "de" },
            "Cancel receiving all \"remove/destroy entity\" packets", false);

    public PacketModule() {
        super("PacketModule", new String[] { "NoPacket", "NoPac", "AntiPacket", "PacketDisable", "PacketCancel" },
                "CRACK packet manipulation", "NONE", -1, ModuleType.CRACK);
    }

    @Listener
    public void onSendPacket(EventSendPacket event) {
        if (this.playerPosRot.getValue())
            if (event.getPacket() instanceof CPacketPlayer.PositionRotation)
                event.setCanceled(true);

        if (this.playerPosition.getValue())
            if (event.getPacket() instanceof CPacketPlayer.Position)
                event.setCanceled(true);

        if (this.playerRotation.getValue())
            if (event.getPacket() instanceof CPacketPlayer.Rotation)
                event.setCanceled(true);

        if (this.player.getValue())
            if (event.getPacket() instanceof CPacketPlayer)
                event.setCanceled(true);

        if (this.steerBoat.getValue())
            if (event.getPacket() instanceof CPacketSteerBoat)
                event.setCanceled(true);

        if (this.vehicleMove.getValue())
            if (event.getPacket() instanceof CPacketVehicleMove)
                event.setCanceled(true);

        if (this.abilities.getValue())
            if (event.getPacket() instanceof CPacketPlayerAbilities)
                event.setCanceled(true);

        if (this.input.getValue())
            if (event.getPacket() instanceof CPacketInput)
                event.setCanceled(true);
    }

    @Listener(priority = ListenerPriority.HIGHEST)
    public void onReceivePacket(EventReceivePacket event) {

        if (this.removeEntity.getValue() && event.getPacket() instanceof SPacketDestroyEntities) {
            event.setCanceled(true);
        }
    }
}
