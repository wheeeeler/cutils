package net.wheel.cutils.api.event.minecraft;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;

import net.wheel.cutils.api.event.EventCancellable;

public class EventPlaySound extends EventCancellable {
    private final SoundManager manager;
    private final ISound sound;
    private ISound result;

    public EventPlaySound(SoundManager manager, ISound sound) {
        this.manager = manager;
        this.sound = sound;
        this.result = sound;
    }

    public ISound getSound() {
        return sound;
    }

    public ISound getResultSound() {
        return result;
    }

    public void setResultSound(ISound result) {
        this.result = result;
    }

    public SoundManager getManager() {
        return manager;
    }
}
