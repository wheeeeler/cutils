package net.wheel.cutils.impl.management;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Getter;

import net.wheel.cutils.api.animation.Animation;
import net.wheel.cutils.api.notification.Notification;
import net.wheel.cutils.crack;

@Getter
public final class NotificationManager implements Animation {

    private final List<Notification> notifications = new CopyOnWriteArrayList<>();

    public NotificationManager() {
        crack.INSTANCE.getAnimationManager().addAnimation(this);
    }

    public void update() {
        for (Notification notification : getNotifications()) {
            notification.update();
        }
    }

    public void unload() {
        this.notifications.clear();
    }

    public void addNotification(Notification notification) {
        this.notifications.add(notification);
    }

    public void addNotification(String title, String text, Notification.Type type, int duration) {
        this.notifications.add(new Notification(title, text, type, duration));
    }

    public void addNotification(String title, String text) {
        this.notifications.add(new Notification(title, text));
    }

    public void removeNotification(Notification notification) {
        this.notifications.remove(notification);
    }

}
