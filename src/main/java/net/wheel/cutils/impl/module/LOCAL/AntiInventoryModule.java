package net.wheel.cutils.impl.module.LOCAL;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import net.wheel.cutils.api.event.world.EventBlockInteract;
import net.wheel.cutils.api.module.Module;

import hand.interactor.voodoo.handler.ListenerPriority;
import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AntiInventoryModule extends Module {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public AntiInventoryModule() {
        super("AntiContainer", new String[] { "AntiInv" }, "wawawawawa", "NONE", -1, ModuleType.LOCAL);
    }

    public static boolean itemInHand() {
        return mc.player != null && !mc.player.getHeldItemMainhand().isEmpty();
    }

    @Listener(priority = ListenerPriority.HIGHEST)
    public void onBlockInteract(EventBlockInteract event) {
        if (isEnabled() && itemInHand()) {
            event.setCanceled(true);
            placeBlock(event.getPos(), event.getFace(), event.getHand());
        }
    }

    private void placeBlock(BlockPos pos, EnumFacing facing, EnumHand hand) {
        if (pos != null) {
            mc.player.connection
                    .sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.5F, 0.5F, 0.5F));
            mc.player.connection
                    .sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
    }
}
