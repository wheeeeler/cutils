package net.wheel.cutils.impl.management;

import net.minecraft.client.Minecraft;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class PositionManager {

    private double x;
    private double y;
    private double z;

    public void updatePosition() {
        this.x = Minecraft.getMinecraft().player.posX;
        this.y = Minecraft.getMinecraft().player.posY;
        this.z = Minecraft.getMinecraft().player.posZ;
    }

    public void restorePosition() {
        Minecraft.getMinecraft().player.posX = this.x;
        Minecraft.getMinecraft().player.posY = this.y;
        Minecraft.getMinecraft().player.posZ = this.z;
    }

    public void setPlayerPosition(double x, double y, double z) {
        Minecraft.getMinecraft().player.posX = x;
        Minecraft.getMinecraft().player.posY = y;
        Minecraft.getMinecraft().player.posZ = z;
    }

}
