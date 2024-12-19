package net.wheel.cutils.impl.module.WAR;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventSendPacket;
import net.wheel.cutils.api.module.Module;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class WeaponModule extends Module {

    private final Set<Item> projectileWeapons = new HashSet<>();

    public WeaponModule() {
        super("WeaponModule", new String[] { "pWeapon" }, "hehe", "NONE", -1, ModuleType.WAR);
        runWeaponTask();
    }

    @Listener
    public void onCreativeInventoryAction(EventSendPacket event) {
        if (event.getPacket() instanceof CPacketCreativeInventoryAction) {
            CPacketCreativeInventoryAction packet = (CPacketCreativeInventoryAction) event.getPacket();
            ItemStack stack = packet.getStack();
            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                if (runWeaponTask(item)) {
                    projectileWeapons.add(item);
                }
            }
        }
    }

    @Listener
    public void sendPacket(EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof CPacketPlayerDigging) {
                final Minecraft mc = Minecraft.getMinecraft();
                final CPacketPlayerDigging packet = (CPacketPlayerDigging) event.getPacket();

                if (packet.getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                    final ItemStack currentItem = mc.player.inventory.getCurrentItem();
                    final Item currentItemType = currentItem.getItem();

                    if (projectileWeapon(currentItemType)) {
                        if (mc.player.getItemInUseMaxCount() >= getMinPullTime(currentItemType)) {
                            if (!mc.player.onGround) {
                                execBow(mc);
                            }
                        }
                    }
                }
            }
        }
    }

    private void runWeaponTask() {
        for (Item item : ForgeRegistries.ITEMS) {
            if (runWeaponTask(item)) {
                projectileWeapons.add(item);
            }
        }
    }

    private boolean projectileWeapon(Item item) {
        return projectileWeapons.contains(item);
    }

    private int getMinPullTime(Item item) {
        if (item instanceof ItemBow)
            return 20;
        return 15;
    }

    private void execBow(Minecraft mc) {
        for (int i = 0; i < 5; i++) {
            mc.player.connection.sendPacket(
                    new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1f, mc.player.posZ, false));
            mc.player.connection.sendPacket(
                    new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.1f, mc.player.posZ, false));
        }

        mc.player.connection
                .sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 10000f, mc.player.posZ, false));
        mc.player.connection
                .sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 10000f, mc.player.posZ, true));

        for (int i = 0; i < 10; i++) {
            mc.player.connection.sendPacket(
                    new CPacketPlayer.Position(mc.player.posX, mc.player.posY + (i * 0.001), mc.player.posZ, false));
        }
    }

    private boolean runWeaponTask(Item item) {
        String itemName = item.getClass().getSimpleName().toLowerCase();
        return itemName.contains("bow") || itemName.contains("crossbow") || itemName.contains("gun")
                || itemName.contains("projectile");
    }
}
