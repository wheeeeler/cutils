package net.wheel.cutils.impl.mixin.cancel;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import net.wheel.cutils.api.event.world.EventBlockInteract;
import net.wheel.cutils.crack;

@Mixin(value = PlayerControllerMP.class, remap = true)
public abstract class MixinPlayerControllerMP {

    @Inject(method = "processRightClickBlock", at = @At("HEAD"), cancellable = true)
    private void onProcessRightClickBlock(EntityPlayerSP player, WorldClient worldIn, BlockPos pos,
            EnumFacing direction, Vec3d vec, EnumHand hand, CallbackInfoReturnable<EnumActionResult> cir) {

        EventBlockInteract event = new EventBlockInteract(pos, direction, hand);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        if (event.isCanceled()) {
            cir.setReturnValue(EnumActionResult.SUCCESS);
        }
    }
}
