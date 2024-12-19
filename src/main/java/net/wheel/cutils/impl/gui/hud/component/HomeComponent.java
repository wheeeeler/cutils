package net.wheel.cutils.impl.gui.hud.component;

import com.mojang.realmsclient.gui.ChatFormatting;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.event.gui.hud.EventHubComponentClick;
import net.wheel.cutils.api.gui.hud.component.HudComponent;
import net.wheel.cutils.api.gui.hud.component.ResizableHudComponent;
import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.gui.hud.GuiHudEditor;

public final class HomeComponent extends ResizableHudComponent {

    private final int SCROLL_WIDTH = 5;
    private final int BORDER = 2;
    private final int TEXT_GAP = 1;
    private final int TEXTURE_SIZE = 8;
    private final int TITLE_BAR_HEIGHT = mc.fontRenderer.FONT_HEIGHT + 1;
    @Getter
    @Setter
    private int scroll;
    @Getter
    private int totalHeight;

    public HomeComponent() {
        super("Home", 100, 120, 150, 1000);

        this.setVisible(true);
        this.setSnappable(false);
        this.setW(100);
        this.setH(120);
        this.setX((mc.displayWidth / 2.0f) - (this.getW() / 2));
        this.setY((mc.displayHeight / 2.0f) - (this.getH() / 2));

    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (!(mc.currentScreen instanceof GuiHudEditor))
            return;

        final ScaledResolution sr = new ScaledResolution(mc);

        int offsetY = 0;

        this.handleScrolling(mouseX, mouseY);

        final boolean insideTitlebar = mouseY <= this.getY() + TITLE_BAR_HEIGHT + BORDER;
        if (!insideTitlebar) {
            this.setDragging(false);
        }

        final boolean mouseInside = this.isMouseInside(mouseX, mouseY);

        if (this.isResizeDragging()) {
            if (this.getH() > this.getTotalHeight()) {
                this.setH(this.getTotalHeight());
                this.setResizeDragging(false);
            }
        }

        RenderUtil.drawRect(this.getX() - 1, this.getY() - 1, this.getX() + this.getW() + 1,
                this.getY() + this.getH() + 1, crack.INSTANCE.getColorManager().getColor("Border").getRGB());
        RenderUtil.drawRect(this.getX(), this.getY(), this.getX() + this.getW(), this.getY() + this.getH(),
                crack.INSTANCE.getColorManager().getColor("Background").getRGB());

        mc.fontRenderer.drawStringWithShadow(this.getName(), this.getX() + BORDER + BORDER, this.getY() + BORDER,
                crack.INSTANCE.getColorManager().getColor("TitleText").getRGB());
        final String components = ChatFormatting.WHITE + ""
                + (crack.INSTANCE.getHudManager().getComponentList().size() - 1);
        mc.fontRenderer.drawStringWithShadow(components,
                this.getX() + this.getW() - BORDER - mc.fontRenderer.getStringWidth(components), this.getY() + BORDER,
                crack.INSTANCE.getColorManager().getColor("TitleText").getRGB());
        offsetY += mc.fontRenderer.FONT_HEIGHT + 1;

        final float listTop = this.getY() + offsetY + BORDER;

        RenderUtil.drawRect(this.getX() + BORDER, listTop, this.getX() + this.getW() - SCROLL_WIDTH - BORDER,
                this.getY() + this.getH() - BORDER,
                crack.INSTANCE.getColorManager().getColor("ListBackground").getRGB());

        RenderUtil.drawRect(this.getX() + this.getW() - SCROLL_WIDTH, this.getY() + offsetY + BORDER,
                this.getX() + this.getW() - BORDER, this.getY() + this.getH() - BORDER,
                crack.INSTANCE.getColorManager().getColor("ScrollbarBackground").getRGB());

        if (mouseInside) {
            if (mouseX >= (this.getX() + this.getW() - SCROLL_WIDTH)
                    && mouseX <= (this.getX() + this.getW() - BORDER)) {
                RenderUtil.drawGradientRect(this.getX() + this.getW() - SCROLL_WIDTH, this.getY() + offsetY + BORDER,
                        this.getX() + this.getW() - BORDER, this.getY() + offsetY + 8 + BORDER,
                        crack.INSTANCE.getColorManager().getColor("ScrollbarHighlight").getRGB(),
                        crack.INSTANCE.getColorManager().getColor("Scrollbar").getRGB());
                RenderUtil.drawGradientRect(this.getX() + this.getW() - SCROLL_WIDTH,
                        this.getY() + this.getH() - 8 - BORDER, this.getX() + this.getW() - BORDER,
                        this.getY() + this.getH() - BORDER,
                        crack.INSTANCE.getColorManager().getColor("Scrollbar").getRGB(),
                        crack.INSTANCE.getColorManager().getColor("ScrollbarHighlight").getRGB());
                float diffY = this.getY() + TITLE_BAR_HEIGHT + ((this.getH() - TITLE_BAR_HEIGHT) / 2);
                if (mouseY > diffY) {
                    RenderUtil.drawGradientRect(this.getX() + this.getW() - SCROLL_WIDTH,
                            this.getY() + (this.getH() / 2) + BORDER + BORDER, this.getX() + this.getW() - BORDER,
                            this.getY() + this.getH() - BORDER,
                            crack.INSTANCE.getColorManager().getColor("Scrollbar").getRGB(),
                            crack.INSTANCE.getColorManager().getColor("ScrollbarHighlight").getRGB());
                } else {
                    RenderUtil.drawGradientRect(this.getX() + this.getW() - SCROLL_WIDTH,
                            this.getY() + offsetY + BORDER, this.getX() + this.getW() - BORDER,
                            this.getY() + (this.getH() / 2) + BORDER + BORDER,
                            crack.INSTANCE.getColorManager().getColor("ScrollbarHighlight").getRGB(),
                            crack.INSTANCE.getColorManager().getColor("Scrollbar").getRGB());
                }
            }
        }

        RenderUtil.drawRect(this.getX() + this.getW() - SCROLL_WIDTH,
                MathHelper.clamp((this.getY() + offsetY + BORDER) + ((this.getH() * this.scroll) / this.totalHeight),
                        (this.getY() + offsetY + BORDER), (this.getY() + this.getH() - BORDER)),
                this.getX() + this.getW() - BORDER,
                MathHelper.clamp(
                        (this.getY() + this.getH() - BORDER)
                                - (this.getH() * (this.totalHeight - this.getH() - this.scroll) / this.totalHeight),
                        (this.getY() + offsetY + BORDER), (this.getY() + this.getH() - BORDER)),
                crack.INSTANCE.getColorManager().getColor("ScrollbarHighlight").getRGB());

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.glScissor(this.getX() + BORDER, this.getY() + offsetY + BORDER,
                this.getX() + this.getW() - BORDER - SCROLL_WIDTH, this.getY() + this.getH() - BORDER, sr);

        for (HudComponent component : crack.INSTANCE.getHudManager().getComponentList()) {
            if (component != this) {
                RenderUtil.drawRect(this.getX() + BORDER + TEXT_GAP,
                        this.getY() + offsetY + BORDER + TEXT_GAP - this.scroll,
                        this.getX() + BORDER + TEXT_GAP + this.getW() - BORDER - SCROLL_WIDTH - BORDER - 2,
                        this.getY() + offsetY + BORDER + TEXT_GAP + mc.fontRenderer.FONT_HEIGHT - this.scroll,
                        component.isVisible() ? crack.INSTANCE.getColorManager().getColor("ModuleEnabled").getRGB()
                                : crack.INSTANCE.getColorManager().getColor("ModuleDisabled").getRGB());
                final boolean insideComponent = mouseX >= (this.getX() + BORDER)
                        && mouseX <= (this.getX() + this.getW() - BORDER - SCROLL_WIDTH)
                        && mouseY >= (this.getY() + BORDER + mc.fontRenderer.FONT_HEIGHT + 1 + offsetY - this.scroll
                                - mc.fontRenderer.FONT_HEIGHT + 1)
                        && mouseY <= (this.getY() + BORDER + (mc.fontRenderer.FONT_HEIGHT) + 1 + offsetY - this.scroll);
                if (insideComponent) {
                    RenderUtil.drawGradientRect(this.getX() + BORDER + TEXT_GAP,
                            this.getY() + offsetY + BORDER + TEXT_GAP - this.scroll,
                            this.getX() + BORDER + TEXT_GAP + this.getW() - BORDER - SCROLL_WIDTH - BORDER - 2,
                            this.getY() + offsetY + BORDER + TEXT_GAP + mc.fontRenderer.FONT_HEIGHT - this.scroll,
                            crack.INSTANCE.getColorManager().getColor("HoverGradientStart").getRGB(),
                            crack.INSTANCE.getColorManager().getColor("HoverGradientEnd").getRGB());
                }

                mc.fontRenderer.drawStringWithShadow(
                        component.getName(),
                        this.getX() + BORDER + TEXT_GAP + 1,
                        this.getY() + offsetY + BORDER + TEXT_GAP - this.scroll,
                        component.isVisible() ? crack.INSTANCE.getColorManager().getColor("ModuleEnabledText").getRGB()
                                : crack.INSTANCE.getColorManager().getColor("ModuleDisabledText").getRGB());

                offsetY += mc.fontRenderer.FONT_HEIGHT + TEXT_GAP;
            }
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        this.totalHeight = BORDER + TEXT_GAP + offsetY + BORDER;

    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int button) {
        super.mouseRelease(mouseX, mouseY, button);

        final boolean inside = this.isMouseInside(mouseX, mouseY);
        final boolean insideTitlebar = mouseY <= this.getY() + BORDER + TITLE_BAR_HEIGHT;

        if (inside && button == 0 && !insideTitlebar) {
            int offsetY = BORDER;

            for (HudComponent component : crack.INSTANCE.getHudManager().getComponentList()) {
                if (component != this) {
                    final boolean insideComponent = mouseX >= (this.getX() + BORDER)
                            && mouseX <= (this.getX() + this.getW() - BORDER - SCROLL_WIDTH)
                            && mouseY >= (this.getY() + BORDER + mc.fontRenderer.FONT_HEIGHT + 1 + offsetY
                                    - this.scroll)
                            && mouseY <= (this.getY() + BORDER + (mc.fontRenderer.FONT_HEIGHT * 2) + 1 + offsetY
                                    - this.scroll);
                    if (insideComponent) {
                        component.setVisible(!component.isVisible());
                        crack.INSTANCE.getEventManager()
                                .dispatchEvent(new EventHubComponentClick(component.getName(), component.isVisible()));
                    }
                    offsetY += mc.fontRenderer.FONT_HEIGHT + TEXT_GAP;
                }
            }

            if (mouseX >= (this.getX() + this.getW() - SCROLL_WIDTH)
                    && mouseX <= (this.getX() + this.getW() - BORDER)) {
                float diffY = this.getY() + TITLE_BAR_HEIGHT + ((this.getH() - TITLE_BAR_HEIGHT) / 2);
                if (mouseY > diffY) {
                    scroll += 10;
                } else {
                    scroll -= 10;
                }
                this.clampScroll();
            }
        }
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int button) {
        final boolean insideDragZone = mouseY <= this.getY() + TITLE_BAR_HEIGHT + BORDER
                || mouseY >= ((this.getY() + this.getH()) - CLICK_ZONE);
        if (insideDragZone) {
            super.mouseClick(mouseX, mouseY, button);
        }
    }

    private void clampScroll() {
        if (this.scroll < 0) {
            this.scroll = 0;
        }
        if (this.scroll > this.totalHeight - this.getH()) {
            this.scroll = this.totalHeight - (int) this.getH();
        }
    }

    private void handleScrolling(int mouseX, int mouseY) {
        if (this.isMouseInside(mouseX, mouseY) && Mouse.hasWheel()) {
            this.scroll += -(Mouse.getDWheel() / 5);
            this.clampScroll();
        }
    }

}
