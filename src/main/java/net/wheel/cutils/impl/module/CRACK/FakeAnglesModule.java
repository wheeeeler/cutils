package net.wheel.cutils.impl.module.CRACK;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.player.EventPlayerUpdate;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.handler.ListenerPriority;
import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class FakeAnglesModule extends Module {

    public final Value<Boolean> headless = new Value<>("Rope", new String[] { "rope", "roper" }, "look down", false);
    public final Value<Boolean> fakeSpin = new Value<>("Helicopter", new String[] { "helicopter", "spinner" }, "spin",
            false);
    public final Value<Boolean> randomizeAnimations = new Value<>("Randomizer", new String[] { "sperg", "sperg" },
            "random p much animations", false);
    public final Value<Boolean> glideAnimations = new Value<>("glideAnimations", new String[] { "extendedRun", "run" },
            "extends arms and legs", false);
    public final Value<Boolean> desyncHitbox = new Value<>("DesyncHitbox",
            new String[] { "fakeHitbox", "hitboxDesync" }, "Desync the player hitbox", false);
    public final Value<Double> hitboxOffsetX = new Value<>("HitboxX", new String[] { "hitboxOffsetX" }, "hitboxOffsetX",
            3.0, 0.1, 100.0, 0.01);
    public final Value<Double> hitboxOffsetZ = new Value<>("HitboxZ", new String[] { "hitboxOffsetZ" }, "hitboxOffsetZ",
            3.0, 0.1, 100.0, 0.01);
    public final Value<Double> hitboxOffsetY = new Value<>("HitboxY", new String[] { "hitboxOffsetY" }, "hitboxOffsetY",
            3.0, 0.1, 100.0, 0.01);

    private final Random random = new Random();
    private final Object2ObjectOpenHashMap<String, String> animationMap = new Object2ObjectOpenHashMap<>();

    public FakeAnglesModule() {
        super("FakeAngles", new String[] { "tard" }, "aids", "NONE", -1, ModuleType.CRACK);
        initializeAnimationMap();
    }

    @Listener(priority = ListenerPriority.HIGHEST)
    public void onUpdate(EventPlayerUpdate event) {
        final Minecraft mc = Minecraft.getMinecraft();
        final EntityPlayerSP player = mc.player;
        if (player == null || event.getStage() != EventStageable.EventStage.PRE)
            return;

        if (headless.getValue()) {
            player.connection.sendPacket(new CPacketPlayer.Rotation(player.rotationYaw, 180.0F, player.onGround));
        } else if (fakeSpin.getValue()) {
            player.connection.sendPacket(new CPacketPlayer.Rotation((player.ticksExisted % 360) * 50.0F,
                    player.rotationPitch, player.onGround));
        } else if (randomizeAnimations.getValue()) {
            animationRandomizer(player);
        } else if (glideAnimations.getValue()) {
            glideAnimation(player);
        } else if (desyncHitbox.getValue()) {
            fakePosPackets(player);
        }
    }

    private void fakePosPackets(EntityPlayerSP player) {
        double offsetX = hitboxOffsetX.getValue();
        double offsetY = hitboxOffsetY.getValue();
        double offsetZ = hitboxOffsetZ.getValue();

        if (isOffsetValid(offsetX, offsetY, offsetZ)) {
            double newX = player.posX + offsetX;
            double newY = player.posY + offsetY;
            double newZ = player.posZ + offsetZ;

            player.connection.sendPacket(new CPacketPlayer.PositionRotation(newX, newY, newZ, player.rotationYaw,
                    player.rotationPitch, player.onGround));
        }
    }

    private boolean isOffsetValid(double offsetX, double offsetY, double offsetZ) {
        return Math.abs(offsetX) > 0.0001 || Math.abs(offsetY) > 0.0001 || Math.abs(offsetZ) > 0.0001;
    }

    private void animationRandomizer(EntityPlayerSP player) {
        switch (random.nextInt(5)) {
            case 0:
                for (int i = 0; i < 10; i++) {
                    player.connection.sendPacket(
                            new CPacketPlayer.Rotation(player.rotationYaw, player.rotationPitch, player.onGround));
                }
                break;
            case 1:
                player.connection.sendPacket(new CPacketPlayer.PositionRotation(
                        player.posX + random.nextDouble() * 2 - 1, player.posY + 2.0,
                        player.posZ + random.nextDouble() * 2 - 1, random.nextFloat() * 360.0F,
                        random.nextFloat() * 360.0F, player.onGround));
                break;
            case 2:
                player.connection.sendPacket(new CPacketPlayer.Rotation((player.ticksExisted % 360) * 50.0F,
                        player.rotationPitch, player.onGround));
                break;
            case 3:
                player.connection.sendPacket(new CPacketPlayer.Rotation(random.nextFloat() * 360.0F,
                        random.nextFloat() * 360.0F, player.onGround));
                break;
            case 4:
                player.connection.sendPacket(new CPacketPlayer.Position(player.posX + random.nextDouble() * 2 - 1,
                        player.posY + 1.0, player.posZ + random.nextDouble() * 2 - 1, player.onGround));
                break;
        }
    }

    private void glideAnimation(EntityPlayerSP player) {
        player.connection.sendPacket(new CPacketPlayer.PositionRotation(
                player.posX + Math.sin(player.ticksExisted * 0.1) * 0.1,
                player.posY, player.posZ + Math.cos(player.ticksExisted * 0.1) * 0.1,
                player.rotationYaw + (player.ticksExisted % 360) * 0.5F, 15.0F, player.onGround));
    }

    private void initializeAnimationMap() {
        animationMap.put("key1", "value1");
        animationMap.put("key2", "value2");
    }
}
