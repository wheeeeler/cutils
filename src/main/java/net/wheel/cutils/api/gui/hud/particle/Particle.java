package net.wheel.cutils.api.gui.hud.particle;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

import javax.vecmath.Vector2f;

import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.ScaledResolution;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.util.ColorUtil;
import net.wheel.cutils.api.util.RenderUtil;

public class Particle {

    private final int maxAlpha;
    @Setter
    @Getter
    private Vector2f pos;
    @Setter
    @Getter
    private Vector2f velocity;
    @Setter
    @Getter
    private Vector2f acceleration;
    @Setter
    @Getter
    private int alpha;
    @Setter
    @Getter
    private float size;
    private static final float RAINBOW_HUE_SPEED = 1.0F;
    private static final float RAINBOW_SATURATION = 1.0F;
    private static final float RAINBOW_BRIGHTNESS = 1.0F;

    public Particle(Vector2f pos) {
        this.pos = pos;
        int lowVel = -1;
        int highVel = 1;
        float resultXVel = lowVel + ThreadLocalRandom.current().nextFloat() * (highVel - lowVel);
        float resultYVel = lowVel + ThreadLocalRandom.current().nextFloat() * (highVel - lowVel);
        this.velocity = new Vector2f(resultXVel, resultYVel);
        this.acceleration = new Vector2f(0, 0.35f);
        this.alpha = 0;
        this.maxAlpha = ThreadLocalRandom.current().nextInt(64, 192);
        this.size = 1.0f + ThreadLocalRandom.current().nextFloat() * (2.0f - 0.5f);

    }

    public void respawn(ScaledResolution scaledResolution) {
        this.pos = new Vector2f((float) (Math.random() * scaledResolution.getScaledWidth()),
                (float) (Math.random() * scaledResolution.getScaledHeight()));
    }

    public void update() {
        if (this.alpha < this.maxAlpha) {
            this.alpha += 8;
        }

        if (this.acceleration.getX() > 0.35f) {
            this.acceleration.setX(this.acceleration.getX() * 0.975f);
        } else if (this.acceleration.getX() < -0.35f) {
            this.acceleration.setX(this.acceleration.getX() * 0.975f);
        }

        if (this.acceleration.getY() > 0.35f) {
            this.acceleration.setY(this.acceleration.getY() * 0.975f);
        } else if (this.acceleration.getY() < -0.35f) {
            this.acceleration.setY(this.acceleration.getY() * 0.975f);
        }

        this.pos.add(acceleration);
        this.pos.add(velocity);
    }

    public void render(int mouseX, int mouseY) {
        if (Mouse.isButtonDown(0)) {
            float deltaXToMouse = mouseX - this.pos.getX();
            float deltaYToMouse = mouseY - this.pos.getY();
            if (Math.abs(deltaXToMouse) < 50 && Math.abs(deltaYToMouse) < 50) {
                this.acceleration.setX(this.acceleration.getX() + (deltaXToMouse * 0.0015f));
                this.acceleration.setY(this.acceleration.getY() + (deltaYToMouse * 0.0015f));
            }
        }

        long currentTime = System.currentTimeMillis();
        float hue = (float) ((currentTime % 10000L) / 10000.0D * RAINBOW_HUE_SPEED) % 1.0F;

        Color rainbow = new Color(Color.HSBtoRGB(hue, RAINBOW_SATURATION, RAINBOW_BRIGHTNESS));

        int colorWithAlpha = ColorUtil.changeAlpha(rainbow.getRGB(), this.alpha);

        RenderUtil.drawRect(this.pos.x, this.pos.y, this.pos.x + this.size, this.pos.y + this.size, colorWithAlpha);
    }

}
