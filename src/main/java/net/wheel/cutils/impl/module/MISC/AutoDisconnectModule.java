package net.wheel.cutils.impl.module.MISC;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketHeldItemChange;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.player.EventPlayerUpdate;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AutoDisconnectModule extends Module {

    public final Value<Float> health = new Value("Health", new String[] { "Hp" },
            "The amount of health needed to disconnect", 8.0f, 0.0f, 20.0f, 0.5f);

    public AutoDisconnectModule() {
        super("AutoDisconnect", new String[] { "Disconnect" }, "Auto disconnects when health is low enough", "NONE", -1,
                ModuleType.MISC);
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (Minecraft.getMinecraft().player.getHealth() <= this.health.getValue()) {
                Minecraft.getMinecraft().player.connection.sendPacket(new CPacketHeldItemChange(420));
                this.toggle();
            }
        }
    }
}
