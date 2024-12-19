package net.wheel.cutils.impl.module.CRACK;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.math.MathHelper;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.entity.EventHorseSaddled;
import net.wheel.cutils.api.event.entity.EventPigTravel;
import net.wheel.cutils.api.event.entity.EventSteerEntity;
import net.wheel.cutils.api.event.player.EventPlayerUpdate;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class EntityCommanderModule extends Module {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public final Value<Boolean> horseJump = new Value<>("HorseJump", new String[] { "horseJump" },
            "Makes horses and llamas jump at max height", false);
    public final Value<Boolean> fly = new Value<>("AnimalFlight", new String[] { "AnimalFly" },
            "Allows you to fly with the entity you're riding", false);
    public final Value<Float> flySpeed = new Value<>("FlightSpeed", new String[] { "FSpeed" },
            "Flight speed for riding animals", 0.1f, 0.01f, 10.0f, 0.01f);
    public final Value<Boolean> noFall = new Value<>("NoFall", new String[] { "NoFallDamage" },
            "Prevents fall damage for riding animals", true);

    public EntityCommanderModule() {
        super("EntityCommander", new String[] { "AntiSaddle", "EntityRide", "NoSaddle", "HorseJump" }, "drift animals",
                "NONE", -1, ModuleType.CRACK);
    }

    @Listener
    public void pigTravel(EventPigTravel event) {
        final boolean moving = mc.player.movementInput.moveForward != 0 || mc.player.movementInput.moveStrafe != 0
                || mc.player.movementInput.jump;

        final Entity riding = mc.player.getRidingEntity();

        if (riding instanceof EntityPig) {
            if (!moving && riding.onGround) {
                event.setCanceled(true);
            }
        }
    }

    @Listener
    public void steerEntity(EventSteerEntity event) {
        event.setCanceled(true);
    }

    @Listener
    public void horseSaddled(EventHorseSaddled event) {
        event.setCanceled(true);
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        if (horseJump.getValue() && event.getStage() == EventStageable.EventStage.PRE) {
            mc.player.horseJumpPower = 1;
            mc.player.horseJumpPowerCounter = -10;
        }

        if (fly.getValue() && mc.player.isRiding()) {
            final Entity riding = mc.player.getRidingEntity();
            if (riding != null) {
                flyDaAnimal(riding);
            }
        }

        if (noFall.getValue() && mc.player.isRiding()) {
            final Entity riding = mc.player.getRidingEntity();
            if (riding != null && riding.fallDistance >= 3.0f) {
                riding.onGround = true;
            }
        }
    }

    private void flyDaAnimal(Entity riding) {
        if (mc.player.movementInput.jump) {
            riding.motionY = flySpeed.getValue();
        } else if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
            riding.motionY = -flySpeed.getValue();
        } else {
            riding.motionY = 0;
        }

        float forward = mc.player.movementInput.moveForward;
        float strafe = -mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;

        if (forward != 0 || strafe != 0) {
            float radiansYaw = (float) Math.toRadians(yaw);
            float cosYaw = MathHelper.cos(radiansYaw);
            float sinYaw = MathHelper.sin(radiansYaw);

            double motionX = (-sinYaw * forward - cosYaw * strafe) * flySpeed.getValue();
            double motionZ = (cosYaw * forward - sinYaw * strafe) * flySpeed.getValue();

            riding.motionX = motionX;
            riding.motionZ = motionZ;
        } else {
            riding.motionX = 0;
            riding.motionZ = 0;
        }
    }
}
