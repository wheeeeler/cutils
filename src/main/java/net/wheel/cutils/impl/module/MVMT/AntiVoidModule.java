package net.wheel.cutils.impl.module.MVMT;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.player.EventPlayerUpdate;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AntiVoidModule extends Module {

    public final Value<Integer> height = new Value<Integer>("Height", new String[] { "hgt" },
            "The Y level the player must be at or below to start running ray-traces for void checks", 16, 0, 256, 1);

    public AntiVoidModule() {
        super("AntiVoid", new String[] { "AntiVoid" }, "Slows down MVMT when over the void.", "NONE", -1,
                ModuleType.MVMT);
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            final Minecraft mc = Minecraft.getMinecraft();
            if (!mc.player.noClip) {
                if (mc.player.posY <= this.height.getValue()) {

                    final RayTraceResult trace = mc.world.rayTraceBlocks(mc.player.getPositionVector(),
                            new Vec3d(mc.player.posX, 0, mc.player.posZ), false, false, false);

                    if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK) {
                        return;
                    }

                    mc.player.setVelocity(0, 0, 0);

                    if (mc.player.getRidingEntity() != null) {
                        mc.player.getRidingEntity().setVelocity(0, 0, 0);
                    }
                }
            }
        }
    }

}
