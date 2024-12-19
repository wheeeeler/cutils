package net.wheel.cutils.impl.module.CRACK;

import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import net.wheel.cutils.api.event.player.EventMove;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.util.BlockUtil;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.module.MVMT.FlightModule;
import net.wheel.cutils.impl.module.MVMT.SafeWalkModule;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class StrafeModule extends Module {

    public Value<Boolean> ground = new Value<>("onGround", new String[] { "Floor", "OnGround", "G" },
            "Enables strafe MVMT while on ground", false);
    public Value<Boolean> elytraFix = new Value<>("elytrafix", new String[] { "FlyChecks", "Elytra" },
            "Lets you use ElytraFly and Strafe simultaneously", true);
    public Value<Boolean> liquidFix = new Value<>("liquidfix", new String[] { "LiquidChecks", "Liquids" },
            "Fixes bugs while swimming", true);
    public Value<Float> speedMultiplier = new Value<>("speed", new String[] { "Speed", "StrafeSpeed" },
            "Adjusts the MVMT speed multiplier", 1.0f, 1.0f, 5.0f, 0.1f);

    public StrafeModule() {
        super("StrafeModule", new String[] { "Strafe" }, "gigamover", "NONE", -1, ModuleType.CRACK);
    }

    @Listener
    public void onMove(EventMove event) {
        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.player == null)
            return;

        if (mc.player.isSneaking() || mc.player.isOnLadder() || mc.player.isInWeb || mc.player.isInLava()
                || mc.player.isInWater() || mc.player.capabilities.isFlying)
            return;

        if (this.liquidFix.getValue()) {
            if (BlockUtil.getBlock(mc.player.posX, mc.player.posY - 1, mc.player.posZ) instanceof BlockLiquid) {
                return;
            }
        }

        if (this.elytraFix.getValue() && mc.player.isElytraFlying())
            return;

        if (!this.ground.getValue()) {
            if (mc.player.onGround)
                return;
        }

        final FlightModule flightModule = (FlightModule) crack.INSTANCE.getModuleManager().find(FlightModule.class);
        if (flightModule != null && flightModule.isEnabled())
            return;

        float playerSpeed = 0.2873f * this.speedMultiplier.getValue();

        PotionEffect speedEffect = mc.player.getActivePotionEffect(MobEffects.SPEED);
        if (speedEffect != null) {
            int amplifier = speedEffect.getAmplifier();
            playerSpeed *= (1.0f + 0.2f * (amplifier + 1));
        }

        mc.player.removePotionEffect(MobEffects.SPEED);

        float moveForward = mc.player.movementInput.moveForward;
        float moveStrafe = mc.player.movementInput.moveStrafe;
        float rotationYaw = mc.player.rotationYaw;

        if (moveForward == 0.0f && moveStrafe == 0.0f) {
            event.setX(0.0d);
            event.setZ(0.0d);
        } else {
            if (moveForward != 0.0f) {
                if (moveStrafe > 0.0f) {
                    rotationYaw += (moveForward > 0.0f) ? -45 : 45;
                } else if (moveStrafe < 0.0f) {
                    rotationYaw += (moveForward > 0.0f) ? 45 : -45;
                }
                moveStrafe = 0.0f;
                moveForward = (moveForward > 0.0f) ? 1.0f : -1.0f;
            }
            event.setX((moveForward * playerSpeed) * Math.cos(Math.toRadians(rotationYaw + 90.0f))
                    + (moveStrafe * playerSpeed) * Math.sin(Math.toRadians(rotationYaw + 90.0f)));
            event.setZ((moveForward * playerSpeed) * Math.sin(Math.toRadians(rotationYaw + 90.0f))
                    - (moveStrafe * playerSpeed) * Math.cos(Math.toRadians(rotationYaw + 90.0f)));
        }

        final SafeWalkModule safeWalkModule = (SafeWalkModule) crack.INSTANCE.getModuleManager()
                .find(SafeWalkModule.class);
        if (safeWalkModule != null && safeWalkModule.isEnabled()) {
            safeWalkModule.onMove(event);
        }
    }
}
