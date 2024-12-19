package net.wheel.cutils.impl.mixin.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;

import net.wheel.cutils.api.event.render.EventRenderTileEntity;
import net.wheel.cutils.crack;

@Mixin(value = TileEntityRendererDispatcher.class, remap = true)
public class MixinTileEntityRendererDispatcher {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRenderTileEntity(TileEntity tileEntity, double x, double y, double z, float partialTicks,
            int destroyStage, float alpha, CallbackInfo ci) {
        EventRenderTileEntity event = new EventRenderTileEntity(tileEntity);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
