package net.wheel.cutils.impl.module.RENDER;

import java.awt.*;

import net.minecraft.client.Minecraft;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.player.EventPlayerUpdate;
import net.wheel.cutils.api.event.world.EventFoliageColor;
import net.wheel.cutils.api.event.world.EventGrassColor;
import net.wheel.cutils.api.event.world.EventWaterColor;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AntiBiomeColorModule extends Module {

    public final Value<Mode> mode = new Value<Mode>("Mode", new String[] { "Mode", "M" },
            "Change between NoBiomeColor modes, Default to use vanilla colors, Custom to use specified RGB values",
            Mode.DEFAULT);
    public final Value<Color> color = new Value<Color>("CustomColor", new String[] { "customcolor", "color", "c" },
            "Edit the custom biome color", new Color(255, 255, 255));
    private float prevRed;
    private float prevGreen;
    private float prevBlue;
    private Mode prevMode;

    public AntiBiomeColorModule() {
        super("AntiBiomeColor", new String[] { "AntiBiomeColor", "NoBiomeC", "NoBiome" },
                "Prevents the game from altering the color of foliage, water and grass in biomes", "NONE", -1,
                ModuleType.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.reload();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.reload();
    }

    @Override
    public String getMetaData() {
        return this.mode.getValue().name();
    }

    private void reload() {
        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.world != null) {
            mc.renderGlobal.markBlockRangeForRenderUpdate(
                    (int) mc.player.posX - 256,
                    (int) mc.player.posY - 256,
                    (int) mc.player.posZ - 256,
                    (int) mc.player.posX + 256,
                    (int) mc.player.posY + 256,
                    (int) mc.player.posZ + 256);
        }
    }

    private int getHex() {
        return (255 << 24) | (this.color.getValue().getRed() << 16)
                | (this.color.getValue().getGreen() << 8 | this.color.getValue().getBlue());
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (this.prevRed != this.color.getValue().getRed()) {
                this.prevRed = this.color.getValue().getRed();
                this.reload();
            }
            if (this.prevGreen != this.color.getValue().getGreen()) {
                this.prevGreen = this.color.getValue().getGreen();
                this.reload();
            }
            if (this.prevBlue != this.color.getValue().getBlue()) {
                this.prevBlue = this.color.getValue().getBlue();
                this.reload();
            }
            if (this.prevMode != this.mode.getValue()) {
                this.prevMode = this.mode.getValue();
                this.reload();
            }
        }
    }

    @Listener
    public void getGrassColor(EventGrassColor event) {
        switch (this.mode.getValue()) {
            case DEFAULT:
                event.setColor(0x79c05a);
                break;
            case CUSTOM:
                event.setColor(this.getHex());
                break;
        }
        event.setCanceled(true);
    }

    @Listener
    public void getFoliageColor(EventFoliageColor event) {
        switch (this.mode.getValue()) {
            case DEFAULT:
                event.setColor(0x59ae30);
                break;
            case CUSTOM:
                event.setColor(this.getHex());
                break;
        }
        event.setCanceled(true);
    }

    @Listener
    public void getWaterColor(EventWaterColor event) {
        switch (this.mode.getValue()) {
            case DEFAULT:
                event.setColor(0x1E97F2);
                break;
            case CUSTOM:
                event.setColor(this.getHex());
                break;
        }
        event.setCanceled(true);
    }

    private enum Mode {
        DEFAULT, CUSTOM
    }

}
