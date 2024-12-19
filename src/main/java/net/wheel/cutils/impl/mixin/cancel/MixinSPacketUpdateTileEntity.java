package net.wheel.cutils.impl.mixin.cancel;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import net.wheel.cutils.api.event.world.EventTileEntityUpdate;
import net.wheel.cutils.crack;

@Mixin(value = SPacketUpdateTileEntity.class, remap = true)
public abstract class MixinSPacketUpdateTileEntity {

    @Inject(method = "processPacket", at = @At("HEAD"), cancellable = true)
    private void onProcessPacket(CallbackInfo ci) {

        SPacketUpdateTileEntity packet = (SPacketUpdateTileEntity) (Object) this;

        BlockPos pos = packet.getPos();

        TileEntity tileEntity = Minecraft.getMinecraft().world.getTileEntity(pos);

        EventTileEntityUpdate event = new EventTileEntityUpdate(pos, packet, tileEntity);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
