package net.wheel.cutils.impl.module.MISC;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventReceivePacket;
import net.wheel.cutils.api.event.player.EventDestroyBlock;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class DesyncFixModule extends Module {

    public final Value<Boolean> crystals = new Value<Boolean>("Crystals", new String[] { "Crystal", "c" },
            "Attempts to fix crystal de-sync (could be buggy)", false);
    public final Value<Boolean> destroyedBlocks = new Value<Boolean>("Blocks", new String[] { "DestroyedBlocks", "b" },
            "Attempts to fix server->client block de-sync", true);

    private boolean destroy;
    private BlockPos pos;

    public DesyncFixModule() {
        super("DesyncFix", new String[] { "NoDes", "AntiDesync", "NoDe-sync" },
                "Prevents the client from de-syncing in some situations", "NONE", -1, ModuleType.MISC);
    }

    @Override
    public void onToggle() {
        super.onToggle();
        this.destroy = false;
        this.pos = null;
    }

    @Listener
    public void receivePacket(EventReceivePacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof SPacketSoundEffect) {
                if (this.crystals.getValue()) {
                    final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
                    if (packet.getCategory() == SoundCategory.BLOCKS
                            && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                        final Minecraft mc = Minecraft.getMinecraft();
                        if (mc.world != null) {
                            for (int i = mc.world.loadedEntityList.size() - 1; i > 0; i--) {
                                Entity entity = mc.world.loadedEntityList.get(i);
                                if (entity != null) {
                                    if (entity.isEntityAlive() && entity instanceof EntityEnderCrystal) {
                                        if (entity.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0f) {
                                            entity.setDead();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (event.getPacket() instanceof SPacketBlockChange) {
                if (this.destroyedBlocks.getValue()) {
                    SPacketBlockChange packet = (SPacketBlockChange) event.getPacket();
                    if (packet.getBlockPosition() == this.pos) {
                        this.destroy = true;
                    }
                }
            }
        }
    }

    @Listener
    public void onDestroyBlock(EventDestroyBlock event) {
        if (this.destroyedBlocks.getValue()) {
            this.pos = event.getPos();
            if (this.destroy) {
                event.setCanceled(false);
                this.destroy = false;
                this.pos = null;
            } else {
                event.setCanceled(true);
            }
        }
    }
}
