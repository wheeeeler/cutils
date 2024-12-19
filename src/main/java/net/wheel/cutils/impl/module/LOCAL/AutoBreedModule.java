package net.wheel.cutils.impl.module.LOCAL;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.EnumHand;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.player.EventPlayerUpdate;
import net.wheel.cutils.api.module.Module;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AutoBreedModule extends Module {

    public AutoBreedModule() {
        super("AutoBreed", new String[] { "bred" },
                "Automatically breeds nearby animals if holding the correct breeding item", "NONE", -1,
                ModuleType.LOCAL);
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            final Minecraft mc = Minecraft.getMinecraft();
            for (Entity e : mc.world.loadedEntityList) {
                if (e != null && e instanceof EntityAnimal) {
                    final EntityAnimal animal = (EntityAnimal) e;
                    if (animal.getHealth() > 0) {
                        if (!animal.isChild() && !animal.isInLove() && mc.player.getDistance(animal) <= 4.5f
                                && animal.isBreedingItem(mc.player.inventory.getCurrentItem())) {
                            mc.playerController.interactWithEntity(mc.player, animal, EnumHand.MAIN_HAND);
                        }
                    }
                }
            }
        }
    }

}
