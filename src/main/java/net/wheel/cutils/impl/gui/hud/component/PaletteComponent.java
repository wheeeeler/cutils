package net.wheel.cutils.impl.gui.hud.component;

import java.awt.*;
import java.util.Map;
import java.util.logging.Level;

import com.mojang.realmsclient.gui.ChatFormatting;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.gui.hud.component.ColorComponent;
import net.wheel.cutils.api.gui.hud.component.ComponentListener;
import net.wheel.cutils.api.gui.hud.component.HudComponent;
import net.wheel.cutils.api.gui.hud.component.ResizableHudComponent;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.texture.Texture;
import net.wheel.cutils.api.util.ColorUtil;
import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.ColorConfig;
import net.wheel.cutils.impl.config.HudConfig;
import net.wheel.cutils.impl.config.ModuleConfig;
import net.wheel.cutils.impl.gui.hud.GuiHudEditor;

public final class PaletteComponent extends ResizableHudComponent {

    public int baseColor = 0xFFFFFFFF;
    public int selectedColor = 0xFFFFFFFF;
    @Getter
    @Setter
    private int scroll;
    @Getter
    private int totalHeight;
    @Getter
    @Setter
    private ColorComponent currentColorComponent = null;
    private int lastGradientMouseX = -1;
    private int lastGradientMouseY = -1;
    private int lastSpectrumMouseX = -1;
    private int lastSpectrumMouseY = -1;
    private final int SCROLL_WIDTH = 5;
    private final int BORDER = 2;
    private final int TEXT_GAP = 1;
    private final int TITLE_BAR_HEIGHT = mc.fontRenderer.FONT_HEIGHT + 1;
    private final Texture spectrum;

    public PaletteComponent() {
        super("Palette", 100, 120, 300, 1000);

        this.setSnappable(false);
        this.setW(120);
        this.setH(120);
        this.setX((mc.displayWidth / 2.0f) - (this.getW() / 2));
        this.setY((mc.displayHeight / 2.0f) - (this.getH() / 2));

        this.spectrum = new Texture("spectrum.jpg");
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
        mc.fontRenderer.drawStringWithShadow(this.getName(), this.getX() + BORDER, this.getY() + BORDER,
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
                        crack.INSTANCE.getColorManager().getColor("HoverGradientStart").getRGB(),
                        crack.INSTANCE.getColorManager().getColor("HoverGradientEnd").getRGB());
                RenderUtil.drawGradientRect(this.getX() + this.getW() - SCROLL_WIDTH,
                        this.getY() + this.getH() - 8 - BORDER, this.getX() + this.getW() - BORDER,
                        this.getY() + this.getH() - BORDER,
                        crack.INSTANCE.getColorManager().getColor("HoverGradientEnd").getRGB(),
                        crack.INSTANCE.getColorManager().getColor("ScrollbarHighlight").getRGB());
                float diffY = this.getY() + TITLE_BAR_HEIGHT + ((this.getH() - TITLE_BAR_HEIGHT) / 2);
                if (mouseY > diffY) {
                    RenderUtil.drawGradientRect(this.getX() + this.getW() - SCROLL_WIDTH,
                            this.getY() + (this.getH() / 2) + BORDER + BORDER, this.getX() + this.getW() - BORDER,
                            this.getY() + this.getH() - BORDER,
                            crack.INSTANCE.getColorManager().getColor("HoverGradientEnd").getRGB(),
                            crack.INSTANCE.getColorManager().getColor("ScrollbarHighlight").getRGB());
                } else {
                    RenderUtil.drawGradientRect(this.getX() + this.getW() - SCROLL_WIDTH,
                            this.getY() + offsetY + BORDER, this.getX() + this.getW() - BORDER,
                            this.getY() + (this.getH() / 2) + BORDER + BORDER,
                            crack.INSTANCE.getColorManager().getColor("ScrollbarHighlight").getRGB(),
                            crack.INSTANCE.getColorManager().getColor("HoverGradientEnd").getRGB());
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
            if (component == null)
                continue;

            if (component != this && !component.getValueList().isEmpty()) {
                for (Value value : component.getValueList()) {
                    if (value.getValue() != null) {
                        if (value.getValue().getClass() == Color.class) {
                            RenderUtil.drawRect(this.getX() + BORDER + TEXT_GAP,
                                    this.getY() + offsetY + BORDER + TEXT_GAP - this.scroll,
                                    this.getX() + BORDER + TEXT_GAP + this.getW() - BORDER - SCROLL_WIDTH - BORDER - 2,
                                    this.getY() + offsetY + BORDER + TEXT_GAP + mc.fontRenderer.FONT_HEIGHT
                                            - this.scroll,
                                    0x45303030);
                            final boolean insideComponent = mouseX >= (this.getX() + BORDER)
                                    && mouseX <= (this.getX() + this.getW() - BORDER - SCROLL_WIDTH)
                                    && mouseY >= (this.getY() + BORDER + mc.fontRenderer.FONT_HEIGHT + 1 + offsetY
                                            - this.scroll - mc.fontRenderer.FONT_HEIGHT + 1)
                                    && mouseY <= (this.getY() + BORDER + (mc.fontRenderer.FONT_HEIGHT) + 1 + offsetY
                                            - this.scroll);
                            if (insideComponent) {
                                RenderUtil.drawGradientRect(this.getX() + BORDER + TEXT_GAP,
                                        this.getY() + offsetY + BORDER + TEXT_GAP - this.scroll,
                                        this.getX() + BORDER + TEXT_GAP + this.getW() - BORDER - SCROLL_WIDTH - BORDER
                                                - 2,
                                        this.getY() + offsetY + BORDER + TEXT_GAP + mc.fontRenderer.FONT_HEIGHT
                                                - this.scroll,
                                        0x30909090, 0x00101010);
                            }

                            final int valueColor = ((Color) value.getValue()).getRGB();
                            mc.fontRenderer.drawStringWithShadow(
                                    ChatFormatting.GRAY + component.getName() + ": " + ChatFormatting.RESET
                                            + value.getName(),
                                    this.getX() + BORDER + TEXT_GAP + 1,
                                    this.getY() + offsetY + BORDER + TEXT_GAP - this.scroll, valueColor);

                            offsetY += mc.fontRenderer.FONT_HEIGHT + TEXT_GAP;
                        }
                    }
                }
            }
        }

        for (Module module : crack.INSTANCE.getModuleManager().getModuleList()) {
            if (module == null)
                continue;

            if (!module.getValueList().isEmpty()) {
                for (Value value : module.getValueList()) {
                    if (value.getValue() != null) {
                        if (value.getValue().getClass() == Color.class) {
                            RenderUtil.drawRect(this.getX() + BORDER + TEXT_GAP,
                                    this.getY() + offsetY + BORDER + TEXT_GAP - this.scroll,
                                    this.getX() + BORDER + TEXT_GAP + this.getW() - BORDER - SCROLL_WIDTH - BORDER - 2,
                                    this.getY() + offsetY + BORDER + TEXT_GAP + mc.fontRenderer.FONT_HEIGHT
                                            - this.scroll,
                                    0x45303030);
                            final boolean insideComponent = mouseX >= (this.getX() + BORDER)
                                    && mouseX <= (this.getX() + this.getW() - BORDER - SCROLL_WIDTH)
                                    && mouseY >= (this.getY() + BORDER + mc.fontRenderer.FONT_HEIGHT + 1 + offsetY
                                            - this.scroll - mc.fontRenderer.FONT_HEIGHT + 1)
                                    && mouseY <= (this.getY() + BORDER + (mc.fontRenderer.FONT_HEIGHT) + 1 + offsetY
                                            - this.scroll);
                            if (insideComponent) {
                                RenderUtil.drawGradientRect(this.getX() + BORDER + TEXT_GAP,
                                        this.getY() + offsetY + BORDER + TEXT_GAP - this.scroll,
                                        this.getX() + BORDER + TEXT_GAP + this.getW() - BORDER - SCROLL_WIDTH - BORDER
                                                - 2,
                                        this.getY() + offsetY + BORDER + TEXT_GAP + mc.fontRenderer.FONT_HEIGHT
                                                - this.scroll,
                                        0x30909090, 0x00101010);
                            }

                            final int valueColor = ((Color) value.getValue()).getRGB();
                            mc.fontRenderer.drawStringWithShadow(
                                    ChatFormatting.GRAY + module.getDisplayName() + ": " + ChatFormatting.RESET
                                            + value.getName(),
                                    this.getX() + BORDER + TEXT_GAP + 1,
                                    this.getY() + offsetY + BORDER + TEXT_GAP - this.scroll, valueColor);

                            offsetY += mc.fontRenderer.FONT_HEIGHT + TEXT_GAP;
                        }
                    }
                }
            }
        }

        for (Map.Entry<String, Color> entry : crack.INSTANCE.getColorManager().getColors().entrySet()) {
            String colorName = entry.getKey();
            Color colorValue = entry.getValue();

            RenderUtil.drawRect(this.getX() + BORDER + TEXT_GAP,
                    this.getY() + offsetY + BORDER + TEXT_GAP - this.scroll,
                    this.getX() + BORDER + TEXT_GAP + this.getW() - BORDER - SCROLL_WIDTH - BORDER - 2,
                    this.getY() + offsetY + BORDER + TEXT_GAP + mc.fontRenderer.FONT_HEIGHT - this.scroll,
                    0x45303030);

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
                        0x30909090, 0x00101010);
            }

            final int valueColor = colorValue.getRGB();
            mc.fontRenderer.drawStringWithShadow(
                    colorName,
                    this.getX() + BORDER + TEXT_GAP + 1,
                    this.getY() + offsetY + BORDER + TEXT_GAP - this.scroll, valueColor);

            offsetY += mc.fontRenderer.FONT_HEIGHT + TEXT_GAP;
        }

        if (this.currentColorComponent != null) {

            RenderUtil.drawRect(this.getX() + BORDER, this.getY() + BORDER + mc.fontRenderer.FONT_HEIGHT + 1,
                    this.getX() + this.getW() - BORDER, this.getY() + this.getH() - BORDER, 0xCC101010);

            RenderUtil.drawRect(this.getX() + BORDER + 3, this.getY() + BORDER + mc.fontRenderer.FONT_HEIGHT + 4,
                    this.getX() + BORDER + 14, this.getY() + BORDER + mc.fontRenderer.FONT_HEIGHT + 15, 0xFF101010);
            RenderUtil.drawRect(this.getX() + BORDER + 4, this.getY() + BORDER + mc.fontRenderer.FONT_HEIGHT + 5,
                    this.getX() + BORDER + 13, this.getY() + BORDER + mc.fontRenderer.FONT_HEIGHT + 14, 0xFF303030);
            RenderUtil.drawLine(this.getX() + BORDER + 6, this.getY() + BORDER + mc.fontRenderer.FONT_HEIGHT + 12,
                    this.getX() + BORDER + 11, this.getY() + BORDER + mc.fontRenderer.FONT_HEIGHT + 7, 1.0f,
                    0xFFFF0000);
            RenderUtil.drawLine(this.getX() + BORDER + 6, this.getY() + BORDER + mc.fontRenderer.FONT_HEIGHT + 7,
                    this.getX() + BORDER + 11, this.getY() + BORDER + mc.fontRenderer.FONT_HEIGHT + 12, 1.0f,
                    0xFFFF0000);

            RenderUtil.drawRect(this.getX() + (this.getW() / 2) - 10, this.getY() + (this.getH() / 4) - 10,
                    this.getX() + (this.getW() / 2) + 10, this.getY() + (this.getH() / 4) + 10,
                    this.currentColorComponent.getCurrentColor().getRGB());
            RenderUtil.drawBorderedRect(this.getX() + (this.getW() / 2) - 10, this.getY() + (this.getH() / 4) - 10,
                    this.getX() + (this.getW() / 2) + 10, this.getY() + (this.getH() / 4) + 10, 1.0f, 0x00000000,
                    0xFFAAAAAA);

            this.spectrum.bind();
            this.spectrum.render(this.getX() + this.getW() / 4 + this.getW() / 16, this.getY() + this.getH() / 2 + 10,
                    this.getW() / 2 - this.getW() / 16, this.getH() / 2 - 18);

            GlStateManager.enableBlend();
            RenderUtil.drawRect(this.getX() + this.getW() / 4, this.getY() + this.getH() / 2 + 10,
                    this.getX() + this.getW() / 4 + this.getW() / 16, this.getY() + this.getH() - 8, 0xFFFFFFFF);
            RenderUtil.drawGradientRect(this.getX() + this.getW() / 4, this.getY() + this.getH() / 2 + 10,
                    this.getX() + this.getW() / 4 + this.getW() / 16, this.getY() + this.getH() - 8, 0x00000000,
                    0xFF000000);
            RenderUtil.drawSideGradientRect(this.getX() + this.getW() / 4 + this.getW() / 16,
                    this.getY() + this.getH() / 2 + 10, this.getX() + this.getW() / 2 + this.getW() / 4,
                    this.getY() + this.getH() - 8, ColorUtil.changeAlpha(this.baseColor, 0xFF), 0x00000000);
            GlStateManager.disableBlend();

            RenderUtil.drawLine(this.getX() + this.getW() / 4 + this.getW() / 16, this.getY() + this.getH() / 2 + 10,
                    this.getX() + this.getW() / 4 + this.getW() / 16, this.getY() + this.getH() - 8, 1.0f, 0xFF000000);

            if (this.lastGradientMouseX != -1 && this.lastGradientMouseY != -1) {
                if (this.lastGradientMouseY - 4 > this.getY() + this.getH() / 2 + 10)
                    RenderUtil.drawLine(this.getX() + this.getW() / 4, this.lastGradientMouseY - 4,
                            this.getX() + this.getW() / 4 + this.getW() / 16, this.lastGradientMouseY - 4, 1f,
                            0xFF000000);

                if (this.lastGradientMouseY + 2 < this.getY() + this.getH() - 8)
                    RenderUtil.drawLine(this.getX() + this.getW() / 4, this.lastGradientMouseY + 2,
                            this.getX() + this.getW() / 4 + this.getW() / 16, this.lastGradientMouseY + 2, 1f,
                            0xFF000000);
            }
            if (this.lastSpectrumMouseX != -1 && this.lastSpectrumMouseY != -1) {
                final Color color = new Color(this.selectedColor).darker();
                RenderUtil.drawLine(this.lastSpectrumMouseX - 1, this.lastSpectrumMouseY - 1,
                        this.lastSpectrumMouseX + 1, this.lastSpectrumMouseY + 1, 1f, color.getRGB());
                RenderUtil.drawLine(this.lastSpectrumMouseX - 1, this.lastSpectrumMouseY + 1,
                        this.lastSpectrumMouseX + 1, this.lastSpectrumMouseY - 1, 1f, color.getRGB());
            }

            if (this.getW() > 180) {
                final String hexColor = "#" + Integer.toHexString(this.currentColorComponent.getCurrentColor().getRGB())
                        .toLowerCase().substring(2);
                final String rgbColor = String.format("r%s g%s b%s",
                        this.currentColorComponent.getCurrentColor().getRed(),
                        this.currentColorComponent.getCurrentColor().getGreen(),
                        this.currentColorComponent.getCurrentColor().getBlue());
                mc.fontRenderer.drawStringWithShadow(hexColor, this.getX() + (this.getW() / 2) + 12,
                        this.getY() + (this.getH() / 4) - 16 + mc.fontRenderer.FONT_HEIGHT, 0xFFAAAAAA);
                mc.fontRenderer.drawStringWithShadow(rgbColor, this.getX() + (this.getW() / 2) + 12,
                        this.getY() + (this.getH() / 4) - 16 + (mc.fontRenderer.FONT_HEIGHT * 2), 0xFFAAAAAA);
            }

            mc.fontRenderer.drawStringWithShadow(this.currentColorComponent.getName(),
                    this.getX() + (this.getW() / 2)
                            - mc.fontRenderer.getStringWidth(this.currentColorComponent.getName()) / 2.0f,
                    this.getY() + (this.getH() / 4) + 14, 0xFFFFFFFF);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if (this.currentColorComponent == null) {
            if (this.scroll > 6) {
                RenderUtil.drawGradientRect(this.getX() + BORDER, listTop,
                        this.getX() + this.getW() - SCROLL_WIDTH - BORDER, listTop + 8, 0xFF101010, 0x00000000);
            }
            if (this.getH() != this.getTotalHeight() && this.scroll != (this.totalHeight - this.getH())) {
                RenderUtil.drawGradientRect(this.getX() + BORDER, this.getY() + this.getH() - BORDER - 8,
                        this.getX() + this.getW() - SCROLL_WIDTH - BORDER, this.getY() + this.getH() - BORDER,
                        0x00000000, 0xFF101010);
            }
        }

        if (this.currentColorComponent != null) {
            this.currentColorComponent.setX(this.getX() + 20);
            this.currentColorComponent.setY(this.getY() + (this.getH() / 2));
            this.currentColorComponent.setW(this.getW() - 40);

            RenderUtil.drawRect(this.currentColorComponent.getX(), this.currentColorComponent.getY(),
                    this.currentColorComponent.getX() + this.currentColorComponent.getW(),
                    this.currentColorComponent.getY() + this.currentColorComponent.getH(), 0xFF101010);
            this.currentColorComponent.render(mouseX, mouseY, partialTicks);
        }

        this.totalHeight = BORDER + TEXT_GAP + offsetY + BORDER;
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int button) {
        try {
            final boolean inside = this.isMouseInside(mouseX, mouseY);
            final boolean insideTitlebar = mouseY <= this.getY() + BORDER + TITLE_BAR_HEIGHT;

            if (this.currentColorComponent != null) {
                if (this.currentColorComponent.isMouseInside(mouseX, mouseY)) {
                    this.currentColorComponent.mouseRelease(mouseX, mouseY, button);
                } else if (inside && !insideTitlebar) {
                    final boolean insideExit = mouseX <= this.getX() + BORDER + 14
                            && mouseY <= this.getY() + BORDER + TITLE_BAR_HEIGHT + 15;
                    if (insideExit) {
                        this.currentColorComponent = null;
                        this.removeSelections();
                        this.baseColor = 0xFFFFFFFF;
                        return;
                    }
                }
            }

            super.mouseRelease(mouseX, mouseY, button);

            if (inside && button == 0 && !insideTitlebar) {
                int offsetY = BORDER;

                for (HudComponent component : crack.INSTANCE.getHudManager().getComponentList()) {
                    if (component != this && !component.getValueList().isEmpty()) {
                        for (Value value : component.getValueList()) {
                            if (value.getValue().getClass() != Color.class)
                                continue;

                            final boolean insideComponent = mouseX >= (this.getX() + BORDER)
                                    && mouseX <= (this.getX() + this.getW() - BORDER - SCROLL_WIDTH)
                                    && mouseY >= (this.getY() + BORDER + mc.fontRenderer.FONT_HEIGHT + 1 + offsetY
                                            - this.scroll)
                                    && mouseY <= (this.getY() + BORDER + (mc.fontRenderer.FONT_HEIGHT * 2) + 1 + offsetY
                                            - this.scroll);
                            if (insideComponent && this.currentColorComponent == null) {
                                ColorComponent colorComponent = new ColorComponent(
                                        component.getName() + ": " + value.getName(),
                                        ((Color) value.getValue()).getRGB(), ChatFormatting.WHITE + "Edit...");
                                colorComponent.returnListener = new ComponentListener() {
                                    @Override
                                    public void onComponentEvent() {
                                        value.setValue(colorComponent.getCurrentColor());
                                        crack.INSTANCE.getConfigManager().save(HudConfig.class);
                                    }
                                };
                                this.currentColorComponent = colorComponent;
                            }

                            offsetY += mc.fontRenderer.FONT_HEIGHT + TEXT_GAP;
                        }
                    }
                }

                for (Module module : crack.INSTANCE.getModuleManager().getModuleList()) {
                    if (!module.getValueList().isEmpty()) {
                        for (Value value : module.getValueList()) {
                            if (value.getValue().getClass() != Color.class)
                                continue;

                            final boolean insideComponent = mouseX >= (this.getX() + BORDER)
                                    && mouseX <= (this.getX() + this.getW() - BORDER - SCROLL_WIDTH)
                                    && mouseY >= (this.getY() + BORDER + mc.fontRenderer.FONT_HEIGHT + 1 + offsetY
                                            - this.scroll)
                                    && mouseY <= (this.getY() + BORDER + (mc.fontRenderer.FONT_HEIGHT * 2) + 1 + offsetY
                                            - this.scroll);
                            if (insideComponent && this.currentColorComponent == null) {
                                ColorComponent colorComponent = new ColorComponent(
                                        module.getDisplayName() + ": " + value.getName(),
                                        ((Color) value.getValue()).getRGB(), ChatFormatting.WHITE + "Edit...");
                                colorComponent.returnListener = new ComponentListener() {
                                    @Override
                                    public void onComponentEvent() {
                                        value.setValue(colorComponent.getCurrentColor());
                                        crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
                                    }
                                };
                                this.currentColorComponent = colorComponent;
                            }

                            offsetY += mc.fontRenderer.FONT_HEIGHT + TEXT_GAP;
                        }
                    }
                }

                for (Map.Entry<String, Color> entry : crack.INSTANCE.getColorManager().getColors().entrySet()) {
                    String colorName = entry.getKey();
                    Color colorValue = entry.getValue();

                    final boolean insideComponent = mouseX >= (this.getX() + BORDER)
                            && mouseX <= (this.getX() + this.getW() - BORDER - SCROLL_WIDTH)
                            && mouseY >= (this.getY() + BORDER + mc.fontRenderer.FONT_HEIGHT + 1 + offsetY
                                    - this.scroll)
                            && mouseY <= (this.getY() + BORDER + (mc.fontRenderer.FONT_HEIGHT * 2) + 1 + offsetY
                                    - this.scroll);
                    if (insideComponent && this.currentColorComponent == null) {
                        ColorComponent colorComponent = new ColorComponent(
                                colorName,
                                colorValue.getRGB(), ChatFormatting.WHITE + "Edit...");
                        colorComponent.returnListener = new ComponentListener() {
                            @Override
                            public void onComponentEvent() {
                                crack.INSTANCE.getColorManager().setColor(colorName, colorComponent.getCurrentColor());
                                crack.INSTANCE.getConfigManager().save(ColorConfig.class);
                            }
                        };
                        this.currentColorComponent = colorComponent;
                    }

                    offsetY += mc.fontRenderer.FONT_HEIGHT + TEXT_GAP;
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
        } catch (Exception e) {
            System.out.println("err " + e.getMessage());
        }
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int button) {
        final boolean insideDragZone = mouseY <= this.getY() + TITLE_BAR_HEIGHT + BORDER
                || mouseY >= ((this.getY() + this.getH()) - CLICK_ZONE);
        if (insideDragZone) {
            super.mouseClick(mouseX, mouseY, button);
        }

        if (this.isDragging() || this.isResizeDragging()) {
            this.removeSelections();
            return;
        }

        if (this.isMouseInside(mouseX, mouseY) && this.currentColorComponent != null) {
            this.currentColorComponent.mouseClick(mouseX, mouseY, button);

            final boolean insideSpectrum = mouseX >= this.getX() + this.getW() / 4 + this.getW() / 16 &&
                    mouseY >= this.getY() + this.getH() / 2 + 10 &&
                    mouseX <= this.getX() + this.getW() / 4 + this.getW() / 16 + this.getW() / 2 - this.getW() / 16 &&
                    mouseY <= this.getY() + this.getH() / 2 + 10 + this.getH() / 2 - 18;
            final boolean insideGradient = mouseX >= this.getX() + this.getW() / 4 &&
                    mouseY >= this.getY() + this.getH() / 2 + 10 &&
                    mouseX <= this.getX() + this.getW() / 4 + this.getW() / 16 &&
                    mouseY <= this.getY() + this.getH() - 8;
            if (insideGradient || insideSpectrum) {

                if (insideGradient)
                    this.removeSelections();

                Robot robot = null;
                try {
                    robot = new Robot();
                } catch (AWTException e) {
                    crack.INSTANCE.getLogger().log(Level.WARNING, "no robot for " + this.getName());
                }

                if (robot != null) {
                    PointerInfo pointerInfo = MouseInfo.getPointerInfo();
                    Point point = pointerInfo.getLocation();
                    int mX = (int) point.getX();
                    int mY = (int) point.getY();
                    Color colorAtMouseClick = robot.getPixelColor(mX, mY);
                    if (insideSpectrum) {
                        this.selectedColor = colorAtMouseClick.getRGB();
                        this.currentColorComponent.setCurrentColor(colorAtMouseClick);
                        this.currentColorComponent.returnListener.onComponentEvent();
                        this.currentColorComponent
                                .setText("#" + Integer.toHexString(this.selectedColor).toLowerCase().substring(2));
                        this.lastSpectrumMouseX = mouseX;
                        this.lastSpectrumMouseY = mouseY;
                    } else {
                        this.baseColor = colorAtMouseClick.getRGB();
                        this.lastGradientMouseX = mouseX;
                        this.lastGradientMouseY = mouseY;
                    }
                }
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);

        if (this.currentColorComponent != null)
            this.currentColorComponent.keyTyped(typedChar, keyCode);
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

    private void removeSelections() {
        this.lastGradientMouseX = -1;
        this.lastGradientMouseY = -1;
        this.lastSpectrumMouseX = -1;
        this.lastSpectrumMouseY = -1;
    }

}
