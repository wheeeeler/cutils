package net.wheel.cutils.api.notification;

import net.wheel.cutils.api.util.MathUtil;
import net.wheel.cutils.api.util.Timer;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.gui.hud.component.NotificationsComponent;

public final class Notification {

    private final String title;
    private final Type type;
    private final int maxDuration;
    private final Timer timer = new Timer();
    private String text;
    private float x = 0, y = 0, width = 0, height = 0;
    private int duration;
    private float transitionX = 0, transitionY = 0;

    public Notification(String title, String text, Type type, int duration) {
        this.title = title;
        this.text = text;
        this.type = type;
        this.duration = duration;
        this.maxDuration = duration;

        final NotificationsComponent notificationsComponent = (NotificationsComponent) crack.INSTANCE.getHudManager()
                .findComponent(NotificationsComponent.class);
        if (notificationsComponent != null) {
            this.transitionX = notificationsComponent.getX();
            this.transitionY = notificationsComponent.getY();
            this.setX(notificationsComponent.getX());
            this.setY(notificationsComponent.getY());
        }

        this.timer.reset();
    }

    public Notification(String title, String text) {
        this(title, text, Type.INFO, 3000);
    }

    public Notification(String title, String text, Type type) {
        this(title, text, type, 3000);
    }

    public void update() {
        int incline = 16;
        this.transitionX = (float) MathUtil.parabolic(this.transitionX, this.x, incline);
        this.transitionY = (float) MathUtil.parabolic(this.transitionY, this.y, incline);
        if (this.timer.passed((this.duration))) {
            crack.INSTANCE.getNotificationManager().removeNotification(this);
        }
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Type getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public float getTransitionX() {
        return transitionX;
    }

    public float getTransitionY() {
        return transitionY;
    }

    public enum Type {
        INFO(0xFF909090, 0),
        SUCCESS(0xFF10FF10, 1),
        WARNING(0xFFFFFF10, 2),
        ERROR(0xFFFF1010, 3),
        QUESTION(0xFF10FFFF, 4),
        MISC(0xFFFFFFFF, 5);

        private final int color;

        private final int textureID;

        Type(int color, int textureID) {
            this.color = color;
            this.textureID = textureID;
        }

        public int getColor() {
            return color;
        }

        public int getTextureID() {
            return textureID;
        }
    }
}
