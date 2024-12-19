package net.wheel.cutils.impl.management;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.camera.Camera;
import net.wheel.cutils.api.event.minecraft.EventUpdateFramebufferSize;
import net.wheel.cutils.api.event.player.EventFovModifier;
import net.wheel.cutils.api.event.render.EventHurtCamEffect;
import net.wheel.cutils.api.event.render.EventRenderOverlay;
import net.wheel.cutils.api.event.render.EventRenderSky;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

@Getter
@Setter
public final class CameraManager {

    private List<Camera> cameraList = new ArrayList<>();

    public CameraManager() {
        crack.INSTANCE.getEventManager().addEventListener(this);
    }

    public void update() {
        for (Camera cam : this.cameraList) {
            if (cam != null && !cam.isRecording() && cam.isRendering()) {
                cam.updateFbo();
            }
        }
    }

    @Listener
    public void renderOverlay(EventRenderOverlay event) {
        if (this.isCameraRecording()) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void fboResize(EventUpdateFramebufferSize event) {
        for (Camera cam : this.cameraList) {
            if (cam != null) {
                cam.resize();
            }
        }
    }

    @Listener
    public void fovModifier(EventFovModifier event) {
        if (this.isCameraRecording()) {
            event.setFov(90.0f);
            event.setCanceled(true);
        }
    }

    @Listener
    public void hurtCamEffect(EventHurtCamEffect event) {
        if (this.isCameraRecording()) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void renderSky(EventRenderSky event) {
        if (this.isCameraRecording()) {
            event.setCanceled(true);
        }
    }

    public void addCamera(Camera cam) {
        this.cameraList.add(cam);
    }

    public void unload() {
        this.cameraList.clear();
        crack.INSTANCE.getEventManager().removeEventListener(this);
    }

    public boolean isCameraRecording() {
        for (Camera cam : this.cameraList) {
            if (cam != null && cam.isRecording()) {
                return true;
            }
        }
        return false;
    }

}
