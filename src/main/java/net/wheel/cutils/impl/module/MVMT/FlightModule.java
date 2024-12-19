package net.wheel.cutils.impl.module.MVMT;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventReceivePacket;
import net.wheel.cutils.api.event.network.EventSendPacket;
import net.wheel.cutils.api.event.player.EventFovModifier;
import net.wheel.cutils.api.event.player.EventUpdateWalkingPlayer;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.util.MathUtil;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class FlightModule extends Module {

    public final Value<Mode> mode = new Value<>("Mode", new String[] { "Mode", "M" }, "The flight mode to use",
            Mode.MEKASUIT);
    public final Value<Float> speed = new Value<>("Speed", new String[] { "Spd" },
            "Speed multiplier for flight, higher values equals more speed", 1.0f, 0.0f, 5.0f, 0.01f);
    public final Value<Float> verticalSpeed = new Value<>("Vertical speed", new String[] { "VSpd" },
            "Vertical speed multiplier", 1.0f, 0.0f, 5.0f, 0.01f);
    public final Value<Boolean> noKick = new Value<>("NoKick", new String[] { "AntiKick", "Kick" },
            "Bypass the server kicking you for flying while in flight", true);
    private int teleportId;
    private final List<CPacketPlayer> packets = new ArrayList<>();

    public FlightModule() {
        super("FlightModule", new String[] { "Fly" }, "Allows you to fly", "NONE", -1, ModuleType.MVMT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (this.mode.getValue() == Mode.PACKET) {
            final Minecraft mc = Minecraft.getMinecraft();

            if (mc.world != null) {
                this.teleportId = 0;
                this.packets.clear();
                final CPacketPlayer bounds = new CPacketPlayer.Position(mc.player.posX, 0, mc.player.posZ,
                        mc.player.onGround);
                this.packets.add(bounds);
                mc.player.connection.sendPacket(bounds);
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public String getMetaData() {
        return this.mode.getValue().name();
    }

    @Listener
    public void onFovModifier(EventFovModifier event) {
        if (this.mode.getValue() == Mode.MEKASUIT) {
            float fovSetting = Minecraft.getMinecraft().gameSettings.fovSetting;
            event.setFov(fovSetting);
        }
    }

    @Listener
    public void onWalkingUpdate(EventUpdateWalkingPlayer event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            final Minecraft mc = Minecraft.getMinecraft();

            if (this.mode.getValue() == Mode.VANILLA) {
                mc.player.setVelocity(0, 0, 0);

                mc.player.jumpMovementFactor = this.speed.getValue();

                if (this.noKick.getValue()) {
                    if (mc.player.ticksExisted % 4 == 0) {
                        mc.player.motionY = -0.04f;
                    }
                }

                final double[] dir = MathUtil.directionSpeed(this.speed.getValue());

                if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                    mc.player.motionX = dir[0];
                    mc.player.motionZ = dir[1];
                } else {
                    mc.player.motionX = 0;
                    mc.player.motionZ = 0;
                }

                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    if (this.noKick.getValue()) {
                        mc.player.motionY = mc.player.ticksExisted % 20 == 0 ? -0.04f : this.verticalSpeed.getValue();
                    } else {
                        mc.player.motionY += this.verticalSpeed.getValue();
                    }
                }

                if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.player.motionY -= this.verticalSpeed.getValue();
                }
            }

            if (this.mode.getValue() == Mode.PACKET) {
                if (this.teleportId <= 0) {
                    final CPacketPlayer bounds = new CPacketPlayer.Position(Minecraft.getMinecraft().player.posX, 0,
                            Minecraft.getMinecraft().player.posZ, Minecraft.getMinecraft().player.onGround);
                    this.packets.add(bounds);
                    Minecraft.getMinecraft().player.connection.sendPacket(bounds);
                    return;
                }

                mc.player.setVelocity(0, 0, 0);

                if (mc.world
                        .getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(-0.0625d, 0, -0.0625d))
                        .isEmpty()) {
                    double ySpeed = 0;

                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        if (this.noKick.getValue()) {
                            ySpeed = mc.player.ticksExisted % 20 == 0 ? -0.04f : this.verticalSpeed.getValue();
                        } else {
                            ySpeed = this.verticalSpeed.getValue();
                        }
                    } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                        ySpeed = -this.verticalSpeed.getValue();
                    } else {
                        ySpeed = mc.world.getCollisionBoxes(mc.player,
                                mc.player.getEntityBoundingBox().expand(-0.0625d, -0.0625d, -0.0625d)).isEmpty()
                                        ? (mc.player.ticksExisted % 4 == 0) ? (this.noKick.getValue() ? -0.04f : 0.0f)
                                                : 0.0f
                                        : 0.0f;
                    }

                    final double[] directionalSpeed = MathUtil.directionSpeed(this.speed.getValue());

                    if (mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown()
                            || mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()
                            || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown()) {
                        if (directionalSpeed[0] != 0.0d || ySpeed != 0.0d || directionalSpeed[1] != 0.0d) {
                            if (mc.player.movementInput.jump
                                    && (mc.player.moveStrafing != 0 || mc.player.moveForward != 0)) {
                                mc.player.setVelocity(0, 0, 0);
                                move(0, 0, 0);
                                for (int i = 0; i <= 3; i++) {
                                    mc.player.setVelocity(0, ySpeed * i, 0);
                                    move(0, ySpeed * i, 0);
                                }
                            } else {
                                if (mc.player.movementInput.jump) {
                                    mc.player.setVelocity(0, 0, 0);
                                    move(0, 0, 0);
                                    for (int i = 0; i <= 3; i++) {
                                        mc.player.setVelocity(0, ySpeed * i, 0);
                                        move(0, ySpeed * i, 0);
                                    }
                                } else {
                                    for (int i = 0; i <= 2; i++) {
                                        mc.player.setVelocity(directionalSpeed[0] * i, ySpeed * i,
                                                directionalSpeed[1] * i);
                                        move(directionalSpeed[0] * i, ySpeed * i, directionalSpeed[1] * i);
                                    }
                                }
                            }
                        }
                    } else {
                        if (this.noKick.getValue()) {
                            if (mc.world
                                    .getCollisionBoxes(mc.player,
                                            mc.player.getEntityBoundingBox().expand(-0.0625d, -0.0625d, -0.0625d))
                                    .isEmpty()) {
                                mc.player.setVelocity(0, (mc.player.ticksExisted % 2 == 0) ? 0.04f : -0.04f, 0);
                                move(0, (mc.player.ticksExisted % 2 == 0) ? 0.04f : -0.04f, 0);
                            }
                        }
                    }
                }
            }

            if (this.mode.getValue() == Mode.MEKASUIT) {
                EntityPlayerSP player = mc.player;

                if (player != null) {
                    if (player.capabilities.isCreativeMode) {
                        player.capabilities.allowFlying = false;
                    }

                    if (!player.capabilities.isFlying && !player.isSneaking()) {
                        player.capabilities.isFlying = true;
                        player.sendPlayerAbilities();
                    }

                    float currentSpeed = speed.getValue();
                    if (mc.gameSettings.keyBindSprint.isKeyDown()) {
                        currentSpeed += 1.0f;
                    }

                    player.jumpMovementFactor = currentSpeed;
                    player.capabilities.setFlySpeed(currentSpeed / 10);

                    float currentVerticalSpeed = verticalSpeed.getValue();
                    float verticalMotion = 0.0F;

                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        verticalMotion += currentVerticalSpeed;
                    }
                    if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                        verticalMotion -= currentVerticalSpeed;

                        if (mc.gameSettings.keyBindSprint.isKeyDown()) {
                            verticalMotion -= 2f;
                        }
                    }

                    if (verticalMotion != 0) {
                        player.motionY += verticalMotion * player.capabilities.getFlySpeed() * 3.0F;
                    }

                    if (player.capabilities.isFlying && player.moveForward == 0.0F && player.moveStrafing == 0.0F) {
                        player.motionX = 0.0;
                        player.motionY = 0.0;
                        player.motionZ = 0.0;
                    }
                }
            }
        }
    }

    private void move(double x, double y, double z) {
        final Minecraft mc = Minecraft.getMinecraft();
        final CPacketPlayer pos = new CPacketPlayer.Position(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z,
                mc.player.onGround);
        this.packets.add(pos);
        mc.player.connection.sendPacket(pos);

        final CPacketPlayer bounds = new CPacketPlayer.Position(mc.player.posX + x, 0, mc.player.posZ + z,
                mc.player.onGround);
        this.packets.add(bounds);
        mc.player.connection.sendPacket(bounds);

        this.teleportId++;
        mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId - 1));
        mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId));
        mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId + 1));
    }

    @Listener
    public void sendPacket(EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (this.mode.getValue() == Mode.PACKET) {
                if (event.getPacket() instanceof CPacketPlayer
                        && !(event.getPacket() instanceof CPacketPlayer.Position)) {
                    event.setCanceled(true);
                }
                if (event.getPacket() instanceof CPacketPlayer) {
                    final CPacketPlayer packet = (CPacketPlayer) event.getPacket();
                    if (packets.contains(packet)) {
                        packets.remove(packet);
                        return;
                    }
                    event.setCanceled(true);
                }
            }
        }
    }

    @Listener
    public void receivePacket(EventReceivePacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (this.mode.getValue() == Mode.PACKET) {
                if (event.getPacket() instanceof SPacketPlayerPosLook) {
                    final SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
                    if (Minecraft.getMinecraft().player.isEntityAlive()
                            && Minecraft.getMinecraft().world
                                    .isBlockLoaded(new BlockPos(Minecraft.getMinecraft().player.posX,
                                            Minecraft.getMinecraft().player.posY, Minecraft.getMinecraft().player.posZ))
                            && !(Minecraft.getMinecraft().currentScreen instanceof GuiDownloadTerrain)) {
                        if (this.teleportId <= 0) {
                            this.teleportId = packet.getTeleportId();
                        } else {
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    private enum Mode {
        VANILLA, PACKET, MEKASUIT
    }
}
