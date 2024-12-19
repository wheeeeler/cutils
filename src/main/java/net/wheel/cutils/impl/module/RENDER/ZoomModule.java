package net.wheel.cutils.impl.module.RENDER;

import net.minecraft.client.Minecraft;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.player.EventPlayerUpdate;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class ZoomModule extends Module {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static Value<Float> zoomFactor = new Value<>("ZoomScale", new String[] { "Zoom" }, "Zoom Factor", 0.3f, 0.1f,
            200f, 0.01f);

    private float normalFov;
    private boolean zooming = false;

    public ZoomModule() {
        super("Zoom", new String[] { "Zoom" }, "Zooms", "NONE", -1, ModuleType.RENDER);
    }

    @Listener
    public void onEnable() {
        normalFov = mc.gameSettings.fovSetting != 0 ? mc.gameSettings.fovSetting : 90;
        zooming = true;
        setFov();
    }

    @Listener
    public void onDisable() {
        mc.gameSettings.fovSetting = normalFov;
        zooming = false;
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (zooming) {
                setFov();
            }
        }
    }

    private void setFov() {
        mc.gameSettings.fovSetting = normalFov - zoomFactor.getValue();
    }
}
