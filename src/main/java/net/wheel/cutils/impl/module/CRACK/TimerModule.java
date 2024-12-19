package net.wheel.cutils.impl.module.CRACK;

import net.minecraft.client.Minecraft;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.player.EventPlayerUpdate;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class TimerModule extends Module {

    public final Value<Float> speed = new Value<Float>("Speed", new String[] { "Spd" },
            "Tick-rate multiplier [(20tps/second) * (this value)]", 4.00f, 0.00f, 10.00f, 0.10f);
    public final Value<Boolean> tpsSync = new Value<Boolean>("TPSSync", new String[] { "TPS", "Sync" },
            "Syncs timer with the servers TPS", false);

    public TimerModule() {
        super("TimerModule", new String[] { "Time", "Tmr" }, "Speeds up the client tick rate", "NONE", -1,
                ModuleType.CRACK);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Minecraft.getMinecraft().timer.tickLength = 50;
    }

    @Override
    public String getMetaData() {
        return "" + String.format("%.2f", this.speed.getValue());
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (this.tpsSync.getValue()) {
                this.speed.setValue(crack.INSTANCE.getTickRateManager().getTickRate() / 20.0f);
            }
            Minecraft.getMinecraft().timer.tickLength = 50.0f / this.speed.getValue();
        }
    }

}
