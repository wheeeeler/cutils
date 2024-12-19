package net.wheel.cutils.impl.module.WAR;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemTool;

import net.wheel.cutils.api.event.player.EventGetMouseOver;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AntiEntityTrace extends Module {

    public Value<Boolean> toolsOnly = new Value<Boolean>("Tools",
            new String[] { "OnlyTools", "Tool", "Pickaxe", "Axe", "Shovel" },
            "When enabled, you will only trace through entities when holding tools", true);

    public AntiEntityTrace() {
        super("AntiEntityTrace", new String[] { "NoMiningTrace", "EntityTrace", "MiningTrace", "NoBB" },
                "Mine through entities by overriding the moused over entity-list", "NONE", -1, ModuleType.WAR);
    }

    @Listener
    public void onGetMouseOver(EventGetMouseOver event) {
        if (this.toolsOnly.getValue()) {
            final Minecraft mc = Minecraft.getMinecraft();
            if (mc.player != null) {
                if (mc.player.getHeldItemMainhand().getItem() instanceof ItemTool ||
                        mc.player.getHeldItemOffhand().getItem() instanceof ItemTool) {
                    event.setCanceled(true);
                }
            }
            return;
        }

        event.setCanceled(true);
    }
}
