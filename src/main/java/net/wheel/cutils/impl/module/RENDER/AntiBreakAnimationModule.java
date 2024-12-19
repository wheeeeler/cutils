package net.wheel.cutils.impl.module.RENDER;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayerDigging;

import net.wheel.cutils.api.event.player.EventPlayerDamageBlock;
import net.wheel.cutils.api.event.render.EventSpawnEffect;
import net.wheel.cutils.api.module.Module;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AntiBreakAnimationModule extends Module {

    public AntiBreakAnimationModule() {
        super("AntiBreakAnimation", new String[] { "AntiBreakAnim", "NoBreakAnimation" },
                "Prevents the break animation server-side and blocks related particles", "NONE", -1, ModuleType.RENDER);
    }

    @Listener
    public void damageBlock(EventPlayerDamageBlock event) {
        Minecraft.getMinecraft().player.connection.sendPacket(new CPacketPlayerDigging(
                CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, event.getPos(), event.getFace()));
    }

    @Listener
    public void onSpawnParticle(EventSpawnEffect event) {
        event.setCanceled(true);
    }
}
