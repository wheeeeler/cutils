package net.wheel.cutils.impl.module.WAR;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventSendPacket;
import net.wheel.cutils.api.event.player.EventUpdateWalkingPlayer;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class BowModule extends Module {

    public final Value<Integer> power = new Value<>("p0wer",
            new String[] { "power", "strength", "intensity", "frequency" }, "num", 1, 0, 50, 1);
    public final Value<Integer> pullTime = new Value<>("pulltime", new String[] { "threshold" }, "delay", 3, 0, 20, 1);
    public final Value<Boolean> sync = new Value<>("sync", new String[] { "sync" }, "sync", true);

    private final Set<Item> projectileWeapons = new HashSet<>();
    private long lastShot = System.currentTimeMillis();
    private final Minecraft mc = Minecraft.getMinecraft();

    public BowModule() {
        super("BowModule", new String[] { "BowTurbo", "TurboBow" }, "faste", "NONE", -1, ModuleType.WAR);
        runWeaponTask();
    }

    private void runWeaponTask() {
        for (Item item : ForgeRegistries.ITEMS) {
            if (projectileWeapon(item)) {
                projectileWeapons.add(item);
            }
        }
    }

    @Listener
    public void onWalkingUpdate(EventUpdateWalkingPlayer event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            final ItemStack currentItem = mc.player.inventory.getCurrentItem();
            if (!projectileWeapons.contains(currentItem.getItem())) {
                return;
            }

            if (mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= pullTime.getValue()) {
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM,
                        BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
                mc.player.stopActiveHand();

                if (sync.getValue() && !syncedTps()) {
                    return;
                }

                pullFASTE();
            }
        }
    }

    @Listener
    public void sendPacket(EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof CPacketPlayerDigging
                    || event.getPacket() instanceof CPacketPlayerTryUseItem) {
                pullFASTE();
            }
        }
    }

    private boolean projectileWeapon(Item item) {
        String itemName = item.getClass().getSimpleName().toLowerCase();
        return item instanceof ItemBow || itemName.contains("bow") || itemName.contains("projectile")
                || itemName.contains("gun");
    }

    private void pullFASTE() {
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));

        for (int i = 0; i < (10 * power.getValue()); i++) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + 1.57e-6,
                    mc.player.posY + 1.57e-9, mc.player.posZ + 1.57e-6, false));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + 1.57e-7,
                    mc.player.posY + 1.57e-10, mc.player.posZ + 1.57e-7, true));
        }

        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
    }

    private boolean syncedTps() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShot < tpsDelay()) {
            return false;
        }
        lastShot = currentTime;
        return true;
    }

    private long tpsDelay() {
        int tps = 20;
        return (1000 / tps);
    }
}
