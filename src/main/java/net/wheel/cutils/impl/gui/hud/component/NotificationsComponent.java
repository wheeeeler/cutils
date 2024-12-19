package net.wheel.cutils.impl.gui.hud.component;

import net.wheel.cutils.api.gui.hud.component.DraggableHudComponent;
import net.wheel.cutils.api.notification.Notification;
import net.wheel.cutils.api.texture.Texture;
import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.gui.hud.GuiHudEditor;
import net.wheel.cutils.impl.gui.hud.anchor.AnchorPoint;

public final class NotificationsComponent extends DraggableHudComponent {

    private final Texture[] textures = new Texture[6];

    public NotificationsComponent(AnchorPoint anchorPoint) {
        super("NotificationPanel");
        this.setAnchorPoint(anchorPoint);
        this.setVisible(true);
        this.init();
    }

    public NotificationsComponent() {
        super("NotificationPanel");
        this.setVisible(true);
        this.init();
    }

    private void init() {
        this.textures[0] = new Texture("info.png");
        this.textures[1] = new Texture("success.png");
        this.textures[2] = new Texture("warning.png");
        this.textures[3] = new Texture("error.png");
        this.textures[4] = new Texture("question.png");
        this.textures[5] = new Texture("crack.png");
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        int offsetY = 0;
        float maxWidth = 0;

        for (Notification notification : crack.INSTANCE.getNotificationManager().getNotifications()) {

            float offsetX = 0;

            if (this.getAnchorPoint() != null) {
                switch (this.getAnchorPoint().getPoint()) {
                    case TOP_CENTER:
                    case BOTTOM_CENTER:
                        offsetX = (this.getW() - 16 - mc.fontRenderer.getStringWidth(notification.getText())) / 2;
                        break;
                    case TOP_LEFT:
                    case BOTTOM_LEFT:
                        offsetX = 0;
                        break;
                    case TOP_RIGHT:
                    case BOTTOM_RIGHT:
                        offsetX = this.getW() - 16 - mc.fontRenderer.getStringWidth(notification.getText());
                        break;
                }
            }

            notification.setX(this.getX() + offsetX);
            notification.setY(this.getY() + offsetY);
            notification.setWidth(16 + mc.fontRenderer.getStringWidth(notification.getText()));
            notification.setHeight(mc.fontRenderer.FONT_HEIGHT + 5);

            RenderUtil.drawRect(notification.getTransitionX() - 1, notification.getTransitionY(),
                    notification.getTransitionX() + notification.getWidth() + 1,
                    notification.getTransitionY() + notification.getHeight(), 0x75101010);

            RenderUtil.drawRect(notification.getTransitionX() + 16 - 1, notification.getTransitionY(),
                    notification.getTransitionX() + notification.getWidth() + 1, (notification.getTransitionY() + 1),
                    notification.getType().getColor());

            this.textures[notification.getType().getTextureID()].render(notification.getTransitionX() - 1,
                    notification.getTransitionY() - 1, 16, 16);

            mc.fontRenderer.drawStringWithShadow(notification.getText(), notification.getTransitionX() + 16,
                    notification.getTransitionY() + 4.0F, 0xFFFFFFFF);

            final float width = notification.getWidth();
            if (width >= maxWidth) {
                maxWidth = width;
            }

            offsetY += notification.getHeight();
        }

        if (crack.INSTANCE.getNotificationManager().getNotifications().isEmpty()) {
            if (mc.currentScreen instanceof GuiHudEditor) {
                final String placeholder = "(notifications)";
                maxWidth = mc.fontRenderer.getStringWidth(placeholder);
                offsetY = mc.fontRenderer.FONT_HEIGHT;
                mc.fontRenderer.drawStringWithShadow(placeholder, this.getX(), this.getY(), 0xFFAAAAAA);
            } else {
                maxWidth = 0;
                offsetY = 0;
                this.setEmptyH(mc.fontRenderer.FONT_HEIGHT);
            }
        }

        this.setW(maxWidth);
        this.setH(offsetY);
    }

}
