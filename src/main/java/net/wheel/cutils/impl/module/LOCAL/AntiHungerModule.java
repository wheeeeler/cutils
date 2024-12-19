package net.wheel.cutils.impl.module.LOCAL;

import static net.minecraft.network.play.client.CPacketEntityAction.Action.START_SPRINTING;
import static net.minecraft.network.play.client.CPacketEntityAction.Action.STOP_SPRINTING;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventSendPacket;
import net.wheel.cutils.api.module.Module;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AntiHungerModule extends Module {

    private boolean isEating = false;
    private final ExecutorService fastEatExecutor = Executors.newSingleThreadExecutor();

    public AntiHungerModule() {
        super("AntiHunger", new String[] { "nohunger", "fastEat" }, "ee", "NONE", -1, ModuleType.LOCAL);
    }

    @Listener
    public void sendPacket(EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof CPacketPlayer) {
                final CPacketPlayer packet = (CPacketPlayer) event.getPacket();
                packet.onGround = Minecraft.getMinecraft().player.fallDistance > 0
                        || Minecraft.getMinecraft().playerController.isHittingBlock;
            }
            if (event.getPacket() instanceof CPacketEntityAction) {
                final CPacketEntityAction packet = (CPacketEntityAction) event.getPacket();
                if (packet.getAction() == START_SPRINTING || packet.getAction() == STOP_SPRINTING) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @Listener
    public void onUpdate(EventStageable.EventStage event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null)
            return;

        if (event == EventStageable.EventStage.PRE) {
            if (mc.player.getHeldItemMainhand().getItem() instanceof ItemFood
                    && mc.gameSettings.keyBindUseItem.isKeyDown()) {
                if (!isEating) {
                    startFastEat();
                    isEating = true;
                }
            } else {
                isEating = false;
            }
        }
    }

    private void startFastEat() {
        final Minecraft mc = Minecraft.getMinecraft();

        fastEatExecutor.execute(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    if (mc.player == null)
                        break;
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(mc.player.getPosition().down(),
                            mc.player.getHorizontalFacing(), EnumHand.MAIN_HAND, 0, 0, 0));
                    Thread.sleep(5);
                }
            } catch (InterruptedException ignored) {
            } finally {
                sanitizeThreads();
            }
        });
    }

    private void sanitizeThreads() {
        try {
            fastEatExecutor.shutdown();
            if (!fastEatExecutor.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                fastEatExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            fastEatExecutor.shutdownNow();
        }
    }

    @Override
    public void onDisable() {
        sanitizeThreads();
        isEating = false;
    }
}
