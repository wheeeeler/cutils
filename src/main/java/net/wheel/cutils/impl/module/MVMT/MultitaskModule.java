package net.wheel.cutils.impl.module.MVMT;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import net.wheel.cutils.api.event.mouse.EventMouseRightClick;
import net.wheel.cutils.api.event.player.EventHandActive;
import net.wheel.cutils.api.event.player.EventHittingBlock;
import net.wheel.cutils.api.event.player.EventResetBlockRemoving;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class MultitaskModule extends Module {

    private final Value<Boolean> bowDisable = new Value<Boolean>("BowDisable", new String[] { "disableonbow", "bd" },
            "Disables multi-tasking when holding a bow", true);
    private final Value<Boolean> shieldDisable = new Value<Boolean>("ShielDisable",
            new String[] { "disablewithshield", "sd" }, "Disables multi-tasking when holding a shield", true);

    public MultitaskModule() {
        super("MultiTasker", new String[] { "multi", "task" },
                "Allows the player to perform multiple actions at once (eating, placing, attacking)", "NONE", -1,
                ModuleType.MVMT);
    }

    @Listener
    public void onActiveHand(EventHandActive event) {
        if (Minecraft.getMinecraft().player != null) {
            final Item heldItem = Minecraft.getMinecraft().player.getHeldItemMainhand().getItem();
            if (this.bowDisable.getValue()) {
                if (heldItem.equals(Items.BOW)) {
                    return;
                }
            }
            if (this.shieldDisable.getValue()) {
                if (heldItem.equals(Items.SHIELD)) {
                    return;
                }
            }
        }
        event.setCanceled(true);
    }

    @Listener
    public void onHittingBlock(EventHittingBlock event) {
        event.setCanceled(true);
    }

    @Listener
    public void onResetBlockRemoving(EventResetBlockRemoving event) {
        event.setCanceled(true);
    }

    @Listener
    public void onRightClick(EventMouseRightClick event) {
        Minecraft.getMinecraft().player.rowingBoat = false;
    }
}
