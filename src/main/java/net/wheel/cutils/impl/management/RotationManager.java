package net.wheel.cutils.impl.management;

import net.minecraft.client.Minecraft;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.task.rotation.RotationTask;
import net.wheel.cutils.api.task.rotation.RotationTaskFactory;

@Getter
public final class RotationManager {

    private final RotationTaskFactory factory;

    @Setter
    private float yaw;
    @Setter
    private float pitch;

    public RotationManager() {
        this.factory = new RotationTaskFactory();
    }

    public void addTask(RotationTask rotationTask) {
        this.factory.addTask(rotationTask);
    }

    public void removeTask(RotationTask rotationTask) {
        this.factory.removeTask(rotationTask);
    }

    public void removeTask(String rotationTaskName) {
        this.factory.removeTask(rotationTaskName);
    }

    public void startTask(RotationTask rotationTask) {
        this.factory.startTask(rotationTask);
    }

    public void finishTask(RotationTask rotationTask) {
        this.factory.finishTask(rotationTask);
    }

    public void updateRotations() {
        this.yaw = Minecraft.getMinecraft().player.rotationYaw;
        this.pitch = Minecraft.getMinecraft().player.rotationPitch;
    }

    public void restoreRotations() {
        Minecraft.getMinecraft().player.rotationYaw = yaw;
        Minecraft.getMinecraft().player.rotationYawHead = yaw;
        Minecraft.getMinecraft().player.rotationPitch = pitch;
    }

    public void setPlayerRotations(float yaw, float pitch) {
        Minecraft.getMinecraft().player.rotationYaw = yaw;
        Minecraft.getMinecraft().player.rotationYawHead = yaw;
        Minecraft.getMinecraft().player.rotationPitch = pitch;
    }

    public void setPlayerYaw(float yaw) {
        Minecraft.getMinecraft().player.rotationYaw = yaw;
        Minecraft.getMinecraft().player.rotationYawHead = yaw;
    }

    public void setPlayerPitch(float pitch) {
        Minecraft.getMinecraft().player.rotationPitch = pitch;
    }

}
