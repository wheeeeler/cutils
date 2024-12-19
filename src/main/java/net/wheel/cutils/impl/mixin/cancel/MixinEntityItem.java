package net.wheel.cutils.impl.mixin.cancel;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;

import net.wheel.cutils.api.event.player.EventItemPickup;
import net.wheel.cutils.crack;

@Mixin(value = EntityItem.class, remap = true)
public class MixinEntityItem {

    @Inject(method = "onCollideWithPlayer", at = @At("HEAD"), cancellable = true)
    private void onItemPickup(EntityPlayer entityIn, CallbackInfo ci) {
        EventItemPickup event = new EventItemPickup((EntityItem) (Object) this, entityIn);

        crack.INSTANCE.getEventManager().dispatchEvent(event);

        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
