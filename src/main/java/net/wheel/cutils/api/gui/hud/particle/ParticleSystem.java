package net.wheel.cutils.api.gui.hud.particle;

import java.awt.*;

import javax.vecmath.Vector2f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.util.ColorUtil;
import net.wheel.cutils.api.util.MathUtil;
import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.impl.gui.hud.GuiHudEditor;

public final class ParticleSystem {

    private final int PARTS = 120;
    private final Particle[] particles = new Particle[PARTS];

    @Setter
    @Getter
    private ScaledResolution scaledResolution;

    private static final float RAINBOW_HUE_SPEED = 1.0F;
    private static final float RAINBOW_SATURATION = 1.0F;
    private static final float RAINBOW_BRIGHTNESS = 1.0F;

    public ParticleSystem(ScaledResolution scaledResolution) {
        this.scaledResolution = scaledResolution;
        for (int i = 0; i < PARTS; i++) {
            this.particles[i] = new Particle(new Vector2f((float) (Math.random() * scaledResolution.getScaledWidth()),
                    (float) (Math.random() * scaledResolution.getScaledHeight())));
        }
    }

    public void update() {
        for (int i = 0; i < PARTS; i++) {
            final Particle particle = this.particles[i];
            if (this.scaledResolution != null) {
                final boolean isOffScreenX = particle.getPos().x > this.scaledResolution.getScaledWidth()
                        || particle.getPos().x < 0;
                final boolean isOffScreenY = particle.getPos().y > this.scaledResolution.getScaledHeight()
                        || particle.getPos().y < 0;
                if (isOffScreenX || isOffScreenY) {
                    particle.respawn(this.scaledResolution);
                }
            }
            particle.update();
        }
    }

    public void render(int mouseX, int mouseY) {
        final boolean isInHudEditor = Minecraft.getMinecraft().currentScreen instanceof GuiHudEditor
                && Minecraft.getMinecraft().player != null;

        long currentTime = System.currentTimeMillis();
        float hue = (float) ((currentTime % 10000L) / 10000.0D * RAINBOW_HUE_SPEED) % 1.0F;
        Color rainbow = new Color(Color.HSBtoRGB(hue, RAINBOW_SATURATION, RAINBOW_BRIGHTNESS));

        int rainbowColorWithAlpha = ColorUtil.changeAlpha(rainbow.getRGB(), 127);
        for (int i = 0; i < PARTS; i++) {
            final Particle particle = this.particles[i];

            if (isInHudEditor) {
                for (int j = 1; j < PARTS; j++) {
                    if (i != j) {
                        final Particle otherParticle = this.particles[j];
                        final Vector2f diffPos = new Vector2f(particle.getPos());
                        diffPos.sub(otherParticle.getPos());
                        final float diff = diffPos.length();
                        final int distance = 150
                                / (scaledResolution.getScaleFactor() <= 1 ? 3 : scaledResolution.getScaleFactor());
                        if (diff < distance) {
                            final int lineAlpha = (int) MathUtil.map(diff, distance, 0, 0, 127);
                            if (lineAlpha > 8) {
                                int lineColorWithAlpha = ColorUtil.changeAlpha(rainbow.getRGB(), lineAlpha);
                                RenderUtil.drawLine(
                                        particle.getPos().x + particle.getSize() / 2.0f,
                                        particle.getPos().y + particle.getSize() / 2.0f,
                                        otherParticle.getPos().x + otherParticle.getSize() / 2.0f,
                                        otherParticle.getPos().y + otherParticle.getSize() / 2.0f,
                                        1.0f,
                                        lineColorWithAlpha);
                            }
                        }
                    }
                }
            }

            particle.render(mouseX, mouseY);
        }
    }

}
