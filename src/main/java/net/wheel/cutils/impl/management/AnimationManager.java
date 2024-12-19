package net.wheel.cutils.impl.management;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import lombok.Getter;

import net.wheel.cutils.api.animation.Animation;

@Getter
public final class AnimationManager {

    private final List<Animation> animations = new CopyOnWriteArrayList<>();

    public AnimationManager() {
        (new Thread(AnimationManager.this::update)).start();
    }

    private void update() {
        while (Thread.currentThread().isAlive()) {
            long beforeAnimation = System.nanoTime();

            if (!this.animations.isEmpty())
                this.animations.forEach(Animation::update);

            int milliseconds = (int) ((System.nanoTime() - beforeAnimation) / 1000000L);

            try {
                TimeUnit.MILLISECONDS.sleep((16 - milliseconds));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void unload() {
        this.animations.clear();
    }

    public void addAnimation(Animation animation) {
        this.animations.add(animation);
    }

}
