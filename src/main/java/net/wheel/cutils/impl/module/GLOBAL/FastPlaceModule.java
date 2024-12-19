package net.wheel.cutils.impl.module.GLOBAL;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemExpBottle;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.player.EventPlayerUpdate;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class FastPlaceModule extends Module {

    public final Value<Boolean> xp = new Value<Boolean>("XP", new String[] { "EXP" },
            "Only activate while holding XP bottles", false);

    public FastPlaceModule() {
        super("FastPlace", new String[] { "Fp" }, "Removes placement delay", "NONE", -1, ModuleType.GLOBAL);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Minecraft.getMinecraft().rightClickDelayTimer = 6;
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (this.xp.getValue()) {
                if (Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof ItemExpBottle
                        || Minecraft.getMinecraft().player.getHeldItemOffhand().getItem() instanceof ItemExpBottle) {
                    Minecraft.getMinecraft().rightClickDelayTimer = 0;
                }
            } else {
                Minecraft.getMinecraft().rightClickDelayTimer = 0;
            }
        }
    }

}
