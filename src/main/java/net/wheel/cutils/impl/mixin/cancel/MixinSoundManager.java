package net.wheel.cutils.impl.mixin.cancel;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;

import net.wheel.cutils.api.event.minecraft.EventPlaySound;
import net.wheel.cutils.crack;

@Mixin(value = SoundManager.class, remap = true)
public class MixinSoundManager {

    @Inject(method = "playSound", at = @At("HEAD"), cancellable = true)
    private void onPlaySound(ISound sound, CallbackInfo ci) {
        EventPlaySound event = new EventPlaySound((SoundManager) (Object) this, sound);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        if (event.getResultSound() == null) {
            ci.cancel();
        }
    }
}
