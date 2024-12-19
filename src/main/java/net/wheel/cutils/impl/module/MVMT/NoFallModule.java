package net.wheel.cutils.impl.module.MVMT;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventSendPacket;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class NoFallModule extends Module {

    public final Value<Boolean> elytraDisable = new Value<Boolean>("ElytraDisable",
            new String[] { "noelytra", "elytra", "disableonelytrafly" }, "Disables NoFall when the player is flying",
            true);

    public NoFallModule() {
        super("NofallModule", new String[] { "NoFallDamage" }, "Prevents fall damage", "NONE", -1, ModuleType.MVMT);
    }

    private boolean isFlying() {
        return this.elytraDisable.getValue() && Minecraft.getMinecraft().player.isElytraFlying();
    }

    @Listener
    public void sendPacket(EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof CPacketPlayer && Minecraft.getMinecraft().player.fallDistance >= 3.0f
                    && !this.isFlying()) {
                final CPacketPlayer packet = (CPacketPlayer) event.getPacket();
                packet.onGround = true;
            }
        }
    }

}
