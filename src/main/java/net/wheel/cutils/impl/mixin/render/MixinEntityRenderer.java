package net.wheel.cutils.impl.mixin.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.EntityRenderer;

import net.wheel.cutils.api.event.render.EventRenderFog;
import net.wheel.cutils.crack;

@Mixin(value = EntityRenderer.class, remap = true)
public class MixinEntityRenderer {

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    private void onSetupFog(int startCoords, float partialTicks, CallbackInfo ci) {
        EventRenderFog event = new EventRenderFog();
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
