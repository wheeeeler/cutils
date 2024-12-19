package net.wheel.cutils.impl.module.MISC;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.util.EnumHand;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.gui.EventBookPage;
import net.wheel.cutils.api.event.gui.EventBookTitle;
import net.wheel.cutils.api.event.network.EventSendPacket;
import net.wheel.cutils.api.event.player.EventPlayerUpdate;
import net.wheel.cutils.api.module.Module;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AutoPaintModule extends Module {

    public AutoPaintModule() {
        super("AutoPaint",
                new String[] { "CSigns", "CSign", "SignColor", "BookColor", "BookColors", "cbooks", "cbook", "dy" },
                "asda", "NONE", -1, ModuleType.MISC);
    }

    @Listener
    public void sendPacket(EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof CPacketUpdateSign) {
                final CPacketUpdateSign packet = (CPacketUpdateSign) event.getPacket();
                for (int i = 0; i < 4; i++) {
                    packet.lines[i] = packet.lines[i].replace("&", "\247" + "\247a");
                }
            }
        }
    }

    @Listener
    public void addPage(EventBookPage event) {
        event.setPage(event.getPage().replace("&", "\247"));
    }

    @Listener
    public void editTitle(EventBookTitle event) {
        event.setTitle(event.getTitle().replace("&", "\247"));
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            final Minecraft mc = Minecraft.getMinecraft();
            if (mc.player.inventory.getCurrentItem().getItem() instanceof ItemDye) {
                final EnumDyeColor color = EnumDyeColor.byDyeDamage(mc.player.inventory.getCurrentItem().getMetadata());

                for (Entity e : mc.world.loadedEntityList) {
                    if (e != null && e instanceof EntitySheep) {
                        final EntitySheep sheep = (EntitySheep) e;
                        if (sheep.getHealth() > 0) {
                            if (sheep.getFleeceColor() != color && !sheep.getSheared()
                                    && mc.player.getDistance(sheep) <= 4.5f) {
                                mc.playerController.interactWithEntity(mc.player, sheep, EnumHand.MAIN_HAND);
                            }
                        }
                    }
                }
            }
        }
    }
}
