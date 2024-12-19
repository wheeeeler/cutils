package net.wheel.cutils.impl.gui.hud.component.module;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.event.gui.hud.EventUIValueChanged;
import net.wheel.cutils.api.gui.hud.component.*;
import net.wheel.cutils.api.gui.hud.component.TextComponent;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.texture.Texture;
import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.api.value.Regex;
import net.wheel.cutils.api.value.Shader;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.ModuleConfig;
import net.wheel.cutils.impl.gui.hud.GuiHudEditor;
import net.wheel.cutils.impl.module.ui.CrackHudModule;

public final class ModuleListComponent extends ResizableHudComponent {

    @Getter
    private final Module.ModuleType type;
    private final int SCROLL_WIDTH = 5;
    private final int BORDER = 2;
    private final int TEXT_GAP = 1;
    private final int TEXTURE_SIZE = 8;
    private final int TITLE_BAR_HEIGHT = mc.fontRenderer.FONT_HEIGHT + 1;
    private final CrackHudModule crackHudModule;
    private final Texture gearTexture;
    @Getter
    public ModuleSettingsComponent currentSettings;
    @Getter
    @Setter
    private int scroll = 0;
    @Getter
    @Setter
    private int oldScroll = 0;
    @Getter
    private int totalHeight;
    @Getter
    private String originalName = "";
    @Getter
    private String title = "";
    @Getter
    @Setter
    private ToolTipComponent currentToolTip;

    public ModuleListComponent(Module.ModuleType type) {
        super(StringUtils.capitalize(type.name().toLowerCase()), 100, 100, 200, 500);
        this.type = type;
        this.originalName = StringUtils.capitalize(type.name().toLowerCase());
        this.crackHudModule = (CrackHudModule) crack.INSTANCE.getModuleManager().find(CrackHudModule.class);
        this.gearTexture = new Texture("gear_wheel_modulelist.png");

        this.setSnappable(false);
        this.setLocked(true);
        this.setX(20);
        this.setY(20);
        this.setW(100);
        this.setH(100);
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

        if (this.isResizeDragging()) {
            if (this.getH() > this.getTotalHeight()) {
                this.setH(this.getTotalHeight());
                this.setResizeDragging(false);
            }
        } else if (!this.isLocked() && this.currentSettings == null && this.getH() > this.getTotalHeight()) {
            this.setH(this.getTotalHeight());
        } else if (this.currentSettings == null && this.getH() > this.getTotalHeight()
                && this.getTotalHeight() > this.getInitialHeight()) {
            this.setH(this.getTotalHeight());
        }

        RenderUtil.drawRect(this.getX() - 1, this.getY() - 1, this.getX() + this.getW() + 1,
                this.getY() + this.getH() + 1, crack.INSTANCE.getColorManager().getColor("Border").getRGB());
        RenderUtil.drawRect(this.getX(), this.getY(), this.getX() + this.getW(), this.getY() + this.getH(),
                crack.INSTANCE.getColorManager().getColor("Background").getRGB());
        GlStateManager.enableBlend();
        GlStateManager.disableBlend();
        mc.fontRenderer.drawStringWithShadow(this.title, this.getX() + BORDER + BORDER, this.getY() + BORDER,
                crack.INSTANCE.getColorManager().getColor("TitleText").getRGB());
        if (this.currentSettings == null) {
            final String modulesAmount = ChatFormatting.WHITE + ""
                    + crack.INSTANCE.getModuleManager().getModuleList(this.type).size();
            mc.fontRenderer.drawStringWithShadow(modulesAmount,
                    this.getX() + this.getW() - BORDER - mc.fontRenderer.getStringWidth(modulesAmount),
                    this.getY() + BORDER, crack.INSTANCE.getColorManager().getColor("TitleText").getRGB());
        }
        offsetY += mc.fontRenderer.FONT_HEIGHT + 1;

        final float listTop = this.getY() + offsetY + BORDER;
        RenderUtil.drawRect(this.getX() + BORDER, listTop, this.getX() + this.getW() - SCROLL_WIDTH - BORDER,
                this.getY() + this.getH() - BORDER,
                crack.INSTANCE.getColorManager().getColor("ListBackground").getRGB());

        RenderUtil.drawRect(this.getX() + this.getW() - SCROLL_WIDTH, this.getY() + offsetY + BORDER,
                this.getX() + this.getW() - BORDER, this.getY() + this.getH() - BORDER,
                crack.INSTANCE.getColorManager().getColor("ScrollbarBackground").getRGB());
        if (this.isMouseInside(mouseX, mouseY)) {
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
        if (this.currentSettings != null) {
            this.title = this.currentSettings.module.getDisplayName();
            this.currentSettings.setX(this.getX() + BORDER);
            this.currentSettings.setY(this.getY() + offsetY + BORDER - this.scroll);
            this.currentSettings.setW(this.getW() - BORDER - SCROLL_WIDTH - BORDER - 2);
            this.currentSettings.setH(this.getH() - BORDER);
            this.currentSettings.render(mouseX, mouseY, partialTicks);
            offsetY += this.currentSettings.getH();
            for (HudComponent settingComponent : this.currentSettings.components) {
                offsetY += settingComponent.getH();
            }
        } else {
            this.title = this.originalName;
            for (Module module : crack.INSTANCE.getModuleManager().getModuleList(this.type)) {
                RenderUtil.drawRect(this.getX() + BORDER + TEXT_GAP,
                        this.getY() + offsetY + BORDER + TEXT_GAP - this.scroll,
                        this.getX() + BORDER + TEXT_GAP + this.getW() - BORDER - SCROLL_WIDTH - BORDER - 2,
                        this.getY() + offsetY + BORDER + TEXT_GAP + mc.fontRenderer.FONT_HEIGHT - this.scroll,
                        module.isEnabled() ? crack.INSTANCE.getColorManager().getColor("ModuleEnabled").getRGB()
                                : crack.INSTANCE.getColorManager().getColor("ModuleDisabled").getRGB());

                final boolean insideModule = mouseX >= (this.getX() + BORDER)
                        && mouseX <= (this.getX() + this.getW() - BORDER - SCROLL_WIDTH - 1)
                        && mouseY >= (this.getY() + BORDER + mc.fontRenderer.FONT_HEIGHT + 1 + offsetY - this.scroll
                                - mc.fontRenderer.FONT_HEIGHT + 1)
                        && mouseY <= (this.getY() + BORDER + (mc.fontRenderer.FONT_HEIGHT) + 1 + offsetY - this.scroll);
                if (insideModule) {
                    final boolean isHoveringOptions = mouseX >= (this.getX() + this.getW() - BORDER - SCROLL_WIDTH - 12)
                            && mouseX <= (this.getX() + this.getW() - BORDER - SCROLL_WIDTH - 2)
                            && mouseY >= (this.getY() + BORDER + mc.fontRenderer.FONT_HEIGHT + 1 + offsetY - this.scroll
                                    - mc.fontRenderer.FONT_HEIGHT + 1)
                            && mouseY <= (this.getY() + BORDER + (mc.fontRenderer.FONT_HEIGHT) + 1 + offsetY
                                    - this.scroll);

                    RenderUtil.drawRect(
                            this.getX() + BORDER + TEXT_GAP + this.getW() - BORDER - SCROLL_WIDTH - BORDER - 12,
                            this.getY() + offsetY + BORDER + TEXT_GAP - this.scroll,
                            this.getX() + BORDER + TEXT_GAP + this.getW() - BORDER - SCROLL_WIDTH - BORDER - 2,
                            this.getY() + offsetY + BORDER + TEXT_GAP + mc.fontRenderer.FONT_HEIGHT - this.scroll,
                            0x45202020);
                    this.gearTexture.bind();
                    this.gearTexture.render(
                            this.getX() + BORDER + TEXT_GAP + this.getW() - BORDER - SCROLL_WIDTH - BORDER - 11,
                            this.getY() + offsetY + BORDER + TEXT_GAP - this.scroll + 0.5f, 8, 8);
                    if (isHoveringOptions) {
                        RenderUtil.drawGradientRect(
                                this.getX() + BORDER + TEXT_GAP + this.getW() - BORDER - SCROLL_WIDTH - BORDER - 12,
                                this.getY() + offsetY + BORDER + TEXT_GAP - this.scroll,
                                this.getX() + BORDER + TEXT_GAP + this.getW() - BORDER - SCROLL_WIDTH - BORDER - 2,
                                this.getY() + offsetY + BORDER + TEXT_GAP + mc.fontRenderer.FONT_HEIGHT - this.scroll,
                                crack.INSTANCE.getColorManager().getColor("HoverGradientStart").getRGB(),
                                crack.INSTANCE.getColorManager().getColor("HoverGradientEnd").getRGB());
                    }

                    RenderUtil.drawGradientRect(this.getX() + BORDER + TEXT_GAP,
                            this.getY() + offsetY + BORDER + TEXT_GAP - this.scroll,
                            this.getX() + BORDER + TEXT_GAP + this.getW() - BORDER - SCROLL_WIDTH - BORDER - 2,
                            this.getY() + offsetY + BORDER + TEXT_GAP + mc.fontRenderer.FONT_HEIGHT - this.scroll,
                            crack.INSTANCE.getColorManager().getColor("HoverGradientStart").getRGB(),
                            crack.INSTANCE.getColorManager().getColor("HoverGradientEnd").getRGB());
                }

                mc.fontRenderer.drawStringWithShadow(module.getDisplayName(), this.getX() + BORDER + TEXT_GAP + 1,
                        this.getY() + offsetY + BORDER + TEXT_GAP - this.scroll,
                        module.isEnabled() ? crack.INSTANCE.getColorManager().getColor("ModuleEnabledText").getRGB()
                                : crack.INSTANCE.getColorManager().getColor("ModuleDisabledText").getRGB());

                offsetY += mc.fontRenderer.FONT_HEIGHT + TEXT_GAP;
            }
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if (this.crackHudModule != null && this.crackHudModule.tooltips.getValue()) {
            if (this.isMouseInside(mouseX, mouseY)) {
                String tooltipText = "";
                int height = BORDER;

                if (this.currentSettings != null) {
                    for (HudComponent valueComponent : this.currentSettings.components) {
                        if (valueComponent.isMouseInside(mouseX, mouseY)) {
                            tooltipText = valueComponent.getTooltipText();
                        } else {
                            if (this.currentToolTip != null) {
                                if (this.currentToolTip.text.equals(valueComponent.getTooltipText())) {
                                    this.currentToolTip = null;
                                }
                            }
                        }
                        height += mc.fontRenderer.FONT_HEIGHT + TEXT_GAP;
                    }
                } else {
                    for (Module module : crack.INSTANCE.getModuleManager().getModuleList(this.type)) {
                        final boolean insideComponent = mouseX >= (this.getX() + BORDER)
                                && mouseX <= (this.getX() + this.getW() - BORDER - SCROLL_WIDTH)
                                && mouseY >= (this.getY() + BORDER + mc.fontRenderer.FONT_HEIGHT + 1 + height
                                        - this.scroll)
                                && mouseY <= (this.getY() + BORDER + (mc.fontRenderer.FONT_HEIGHT * 2) + 1 + height
                                        - this.scroll);
                        if (insideComponent) {
                            tooltipText = module.getDesc();
                        } else {
                            if (this.currentToolTip != null) {
                                if (this.currentToolTip.text.equals(module.getDesc())) {
                                    this.currentToolTip = null;
                                }
                            }
                        }
                        height += mc.fontRenderer.FONT_HEIGHT + TEXT_GAP;
                    }
                }

                if (!tooltipText.equals("")) {
                    if (this.currentToolTip == null) {
                        this.currentToolTip = new ToolTipComponent(tooltipText);
                    } else {

                    }
                } else {
                    this.removeTooltip();
                }
            } else {
                this.removeTooltip();
            }
        }

        this.totalHeight = BORDER + TEXT_GAP + offsetY + BORDER;
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int button) {
        super.mouseRelease(mouseX, mouseY, button);

        final boolean inside = this.isMouseInside(mouseX, mouseY);
        final int titleBarHeight = mc.fontRenderer.FONT_HEIGHT + 1;
        final boolean insideTitlebar = mouseY <= this.getY() + BORDER + titleBarHeight;

        if (inside && !insideTitlebar && !isResizeDragging()) {
            if (this.currentSettings != null) {
                this.currentSettings.mouseRelease(mouseX, mouseY, button);
            } else {
                int offsetY = BORDER;
                for (Module module : crack.INSTANCE.getModuleManager().getModuleList(this.type)) {
                    final boolean insideComponent = mouseX >= (this.getX() + BORDER)
                            && mouseX <= (this.getX() + this.getW() - BORDER - SCROLL_WIDTH - 1)
                            && mouseY >= (this.getY() + BORDER + mc.fontRenderer.FONT_HEIGHT + 1 + offsetY
                                    - this.scroll)
                            && mouseY <= (this.getY() + BORDER + (mc.fontRenderer.FONT_HEIGHT * 2) + 1 + offsetY
                                    - this.scroll);
                    if (insideComponent) {
                        switch (button) {
                            case 0:
                                if (mouseX >= (this.getX() + this.getW() - BORDER - SCROLL_WIDTH - 12)
                                        && mouseX <= (this.getX() + this.getW() - BORDER - SCROLL_WIDTH - 1)) {
                                    this.removeTooltip();
                                    this.currentSettings = new ModuleSettingsComponent(module, this);
                                    this.setOldScroll(this.getScroll());
                                    this.setScroll(0);
                                } else {
                                    module.toggle();
                                }
                                this.setDragging(false);
                                break;
                            case 1:
                                this.removeTooltip();
                                this.currentSettings = new ModuleSettingsComponent(module, this);
                                this.setOldScroll(this.getScroll());
                                this.setScroll(0);
                                break;
                        }
                    }
                    offsetY += mc.fontRenderer.FONT_HEIGHT + TEXT_GAP;
                }
            }

            if (button == 0) {
                if (mouseX >= (this.getX() + this.getW() - SCROLL_WIDTH)
                        && mouseX <= (this.getX() + this.getW() - BORDER)) {
                    float diffY = this.getY() + TITLE_BAR_HEIGHT + ((this.getH() - TITLE_BAR_HEIGHT) / 2);
                    if (mouseY > diffY) {
                        scroll += 10;
                    } else {
                        scroll -= 10;
                    }
                } else {

                }
            }
        }
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int button) {
        final boolean insideDragZone = mouseY <= this.getY() + TITLE_BAR_HEIGHT + BORDER
                || mouseY >= ((this.getY() + this.getH()) - CLICK_ZONE);
        if (insideDragZone) {
            super.mouseClick(mouseX, mouseY, button);
        } else {
            if (this.currentSettings != null) {
                this.currentSettings.mouseClick(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);

        if (this.currentSettings != null) {
            this.currentSettings.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void onClosed() {
        super.onClosed();

        if (this.currentToolTip != null) {
            this.currentToolTip = null;
        }
    }

    private void handleScrolling(int mouseX, int mouseY) {
        if (this.isMouseInside(mouseX, mouseY) && Mouse.hasWheel()) {
            this.scroll += -(Mouse.getDWheel() / 5);

            if (this.scroll < 0) {
                this.scroll = 0;
            }

            if (this.scroll > this.totalHeight - this.getH()) {
                this.scroll = this.totalHeight - (int) this.getH();
            }

            if (this.getOldScroll() != 0) {
                if (this.currentSettings == null) {
                    this.setScroll(this.getOldScroll());
                    this.setOldScroll(0);
                }
            }
        }
    }

    public void removeTooltip() {
        if (this.currentToolTip != null)
            this.currentToolTip = null;
    }

    public static class BackButtonComponent extends HudComponent {
        public final ModuleListComponent parentModuleList;

        public BackButtonComponent(ModuleListComponent parentModuleList) {
            super("Back", "Go back");
            this.parentModuleList = parentModuleList;
        }

        @Override
        public void render(int mouseX, int mouseY, float partialTicks) {
            super.render(mouseX, mouseY, partialTicks);

            if (isMouseInside(mouseX, mouseY))
                RenderUtil.drawGradientRect(this.getX(), this.getY(), this.getX() + this.getW(),
                        this.getY() + this.getH(), 0x30909090, 0x00101010);

            RenderUtil.drawRect(this.getX(), this.getY(), this.getX() + this.getW(), this.getY() + this.getH(),
                    0x45303030);
            Minecraft.getMinecraft().fontRenderer.drawString(this.getName(), (int) this.getX() + 1,
                    (int) this.getY() + 1, -1);
        }

        @Override
        public void mouseRelease(int mouseX, int mouseY, int button) {
            super.mouseRelease(mouseX, mouseY, button);

            if (!this.isMouseInside(mouseX, mouseY) || button != 0)
                return;

            for (HudComponent component : crack.INSTANCE.getHudManager().getComponentList()) {
                if (component instanceof ModuleListComponent) {
                    ModuleListComponent moduleList = (ModuleListComponent) component;
                    if (moduleList.getName().equals(parentModuleList.getName())) {
                        moduleList.currentSettings = null;
                        moduleList.removeTooltip();
                    }
                }
            }
        }
    }

    public static class ModuleSettingsComponent extends HudComponent {
        public final Module module;
        public final List<HudComponent> components;
        private final ModuleListComponent parentModuleList;

        public ModuleSettingsComponent(Module module, ModuleListComponent parentModuleList) {
            super(module.getDisplayName());

            this.module = module;
            this.components = new ArrayList<>();
            this.parentModuleList = parentModuleList;

            components.add(new BackButtonComponent(parentModuleList));

            TextComponent keybindText = new TextComponent("Keybind", module.getKey().toLowerCase(), false);
            keybindText.setTooltipText("The current key for toggling this module");
            keybindText.textListener = new TextComponent.TextComponentListener() {
                @Override
                public void onKeyTyped(int keyCode) {
                    if (keyCode == Keyboard.KEY_ESCAPE) {
                        module.setKey("NONE");
                        keybindText.setText("none");
                        keybindText.focused = false;

                        final CrackHudModule crackHudModule = (CrackHudModule) crack.INSTANCE.getModuleManager()
                                .find(CrackHudModule.class);
                        if (crackHudModule != null) {
                            crackHudModule.displayHudEditor();
                        }
                    } else {
                        String newKey = Keyboard.getKeyName(keyCode);
                        module.setKey(newKey);
                        keybindText.setText(
                                newKey.length() == 1 ? newKey.substring(1) : newKey.toLowerCase());
                        keybindText.focused = false;
                    }
                }
            };
            components.add(keybindText);

            ButtonComponent enabledButton = new ButtonComponent("Enabled");
            enabledButton.setTooltipText("Enables this module");
            enabledButton.enabled = module.isEnabled();
            enabledButton.mouseClickListener = new ComponentListener() {
                @Override
                public void onComponentEvent() {
                    module.toggle();
                }
            };
            components.add(enabledButton);

            ButtonComponent hiddenButton = new ButtonComponent("Hidden");
            hiddenButton.setTooltipText("Hides this module from the enabled mods list");
            hiddenButton.enabled = module.isHidden();
            hiddenButton.mouseClickListener = new ComponentListener() {
                @Override
                public void onComponentEvent() {
                    module.setHidden(hiddenButton.enabled);
                }
            };
            components.add(hiddenButton);

            ColorComponent colorComponent = new ColorComponent("List Color", module.getColor());
            colorComponent.setTooltipText("The color for this module in the enabled mods list");
            colorComponent.returnListener = new ComponentListener() {
                @Override
                public void onComponentEvent() {
                    module.setColor(colorComponent.getCurrentColor().getRGB());
                    crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
                }
            };
            components.add(colorComponent);

            for (Value value : module.getValueList()) {
                if (value.getValue() instanceof Boolean) {
                    ButtonComponent valueButton = new ButtonComponent(value.getName());
                    valueButton.setTooltipText(value.getDesc());
                    valueButton.enabled = (Boolean) value.getValue();
                    valueButton.mouseClickListener = new ComponentListener() {
                        @Override
                        public void onComponentEvent() {
                            value.setValue(valueButton.enabled);
                            crack.INSTANCE.getEventManager().dispatchEvent(new EventUIValueChanged(value));
                        }
                    };
                    components.add(valueButton);
                    this.addComponentToButtons(valueButton);
                } else if (value.getValue() instanceof Number) {
                    /*
                     * TextComponent valueNumberText = new TextComponent(value.getName(), value.getValue().toString(),
                     * true); valueNumberText.setTooltipText(value.getDesc() + " " + ChatFormatting.GRAY + "(" +
                     * value.getMin() + " - " + value.getMax() + ")"); valueNumberText.returnListener = new
                     * ComponentListener() {
                     * 
                     * @Override public void onComponentEvent() { try { if (value.getValue() instanceof Integer) {
                     * value.setValue(Integer.parseInt(valueNumberText.displayValue)); } else if (value.getValue()
                     * instanceof Double) { value.setValue(Double.parseDouble(valueNumberText.displayValue)); } else if
                     * (value.getValue() instanceof Float) {
                     * value.setValue(Float.parseFloat(valueNumberText.displayValue)); } else if (value.getValue()
                     * instanceof Long) { value.setValue(Long.parseLong(valueNumberText.displayValue)); } else if
                     * (value.getValue() instanceof Byte) {
                     * value.setValue(Byte.parseByte(valueNumberText.displayValue)); }
                     * crack.INSTANCE.getConfigManager().save(ModuleConfig.class); } catch (NumberFormatException e) {
                     * crack.INSTANCE.logfChat("%s - %s: Invalid number format", module.getDisplayName(),
                     * value.getName()); } } }; components.add(valueNumberText);
                     * this.addComponentToButtons(valueNumberText);
                     */

                    SliderComponent sliderComponent = new SliderComponent(value.getName(), value);
                    sliderComponent.setTooltipText(value.getDesc() + " " + ChatFormatting.GRAY + "(" + value.getMin()
                            + " - " + value.getMax() + ")");
                    components.add(sliderComponent);
                    this.addComponentToButtons(sliderComponent);
                } else if (value.getValue() instanceof Enum) {
                    final Enum val = (Enum) value.getValue();
                    final int size = val.getClass().getEnumConstants().length;
                    final StringBuilder options = new StringBuilder();

                    for (int i = 0; i < size; i++) {
                        final Enum option = val.getClass().getEnumConstants()[i];
                        options.append(option.name().toLowerCase()).append((i == size - 1) ? "" : ", ");
                    }

                    /*
                     * TextComponent valueText = new TextComponent(value.getName(),
                     * value.getValue().toString().toLowerCase(), false); valueText.setTooltipText(value.getDesc() + " "
                     * + ChatFormatting.GRAY + "(" + options.toString() + ")"); valueText.returnListener = new
                     * ComponentListener() {
                     * 
                     * @Override public void onComponentEvent() { if (value.getEnum(valueText.getText()) != -1) {
                     * value.setEnumValue(valueText.getText());
                     * crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
                     * crack.INSTANCE.getEventManager().dispatchEvent(new EventUIValueChanged(value)); } else {
                     * crack.INSTANCE.logfChat("%s - %s: Invalid entry", module.getDisplayName(), value.getName()); } }
                     * }; components.add(valueText); this.addComponentToButtons(valueText);
                     */

                    CarouselComponent carouselComponent = new CarouselComponent(value.getName(), value);
                    carouselComponent.setTooltipText(value.getDesc() + " " + ChatFormatting.GRAY + "(" + options + ")");
                    components.add(carouselComponent);
                    this.addComponentToButtons(carouselComponent);
                } else if (value.getValue() instanceof String) {
                    TextComponent valueText = new TextComponent(value.getName(),
                            value.getValue().toString().toLowerCase(), false);
                    valueText.setTooltipText(value.getDesc());
                    valueText.returnListener = new ComponentListener() {
                        @Override
                        public void onComponentEvent() {
                            if (!valueText.getText().isEmpty()) {
                                value.setValue(valueText.getText());
                                crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
                                crack.INSTANCE.getEventManager().dispatchEvent(new EventUIValueChanged(value));
                            } else {
                                crack.INSTANCE.logfChat("%s - %s: Not enough input", module.getDisplayName(),
                                        value.getName());
                            }
                        }
                    };
                    components.add(valueText);
                    this.addComponentToButtons(valueText);
                } else if (value.getValue() instanceof Color) {
                    ColorComponent valueColor = new ColorComponent(value.getName(),
                            ((Color) value.getValue()).getRGB());
                    valueColor.setTooltipText("Edit the color of: " + value.getName());
                    valueColor.returnListener = new ComponentListener() {
                        @Override
                        public void onComponentEvent() {
                            value.setValue(valueColor.getCurrentColor());
                            crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
                            crack.INSTANCE.getEventManager().dispatchEvent(new EventUIValueChanged(value));
                        }
                    };
                    components.add(valueColor);
                    this.addComponentToButtons(valueColor);
                } else if (value.getValue() instanceof List) {
                    final List<?> valueList = ((List<?>) value.getValue());
                    if (!valueList.isEmpty()) {
                        if (valueList.get(0) instanceof Item) {
                            ItemsComponent itemsComponent = new ItemsComponent(value);
                            components.add(itemsComponent);
                            this.addComponentToButtons(itemsComponent);
                        } else if (valueList.get(0) instanceof Block) {
                            BlocksComponent blocksComponent = new BlocksComponent(value);
                            components.add(blocksComponent);
                            this.addComponentToButtons(blocksComponent);
                        }
                    }
                } else if (value.getValue() instanceof Regex) {
                    TextComponent valueText = new TextComponent(value.getName(), value.getValue().toString(), false);
                    valueText.setTooltipText(value.getDesc());
                    valueText.returnListener = new ComponentListener() {
                        @Override
                        public void onComponentEvent() {
                            final Regex regex = (Regex) value.getValue();
                            regex.setPatternString(valueText.getText());
                            if (regex.getPattern() == null)
                                crack.INSTANCE.logfChat(
                                        "%s - %s: Invalid or empty regular expression; no input will match with pattern",
                                        module.getDisplayName(), value.getName());
                            crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
                            crack.INSTANCE.getEventManager().dispatchEvent(new EventUIValueChanged(value));
                        }
                    };
                    components.add(valueText);
                    this.addComponentToButtons(valueText);
                } else if (value.getValue() instanceof Shader) {
                    CarouselComponent carouselComponent = new CarouselComponent(value.getName(), value);
                    carouselComponent.setTooltipText(value.getDesc());
                    components.add(carouselComponent);
                    this.addComponentToButtons(carouselComponent);
                }
            }
        }

        @Override
        public void render(int mouseX, int mouseY, float partialTicks) {
            super.render(mouseX, mouseY, partialTicks);

            int offsetY = 1;
            for (HudComponent component : this.components) {
                int offsetX = 0;

                boolean skipRendering = false;
                for (HudComponent otherComponent : this.components) {
                    if (otherComponent == component || otherComponent.getName().equals(component.getName()))
                        continue;

                    boolean isChildComponent = component.getName().toLowerCase()
                            .startsWith(otherComponent.getName().toLowerCase());
                    if (isChildComponent) {
                        if (!otherComponent.rightClickEnabled) {
                            skipRendering = true;
                        }

                        offsetX += 4;
                    }
                }

                if (skipRendering) {
                    component.setX(0);
                    component.setY(0);
                    component.setW(0);
                    component.setH(0);
                    continue;
                }

                component.setX(this.getX() + 1 + offsetX);
                component.setY(this.getY() + offsetY);
                component.setW(this.getW() - offsetX);
                component.setH(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);
                component.render(mouseX, mouseY, partialTicks);

                if (offsetX > 0) {
                    RenderUtil.drawLine(component.getX() - offsetX + 1, component.getY(),
                            component.getX() - offsetX + 1, component.getY() + component.getH(), 2.0f, 0x90707070);
                    RenderUtil.drawLine(component.getX() - offsetX + 1.5f, component.getY() + component.getH() / 2,
                            component.getX() - 0.5f, component.getY() + component.getH() / 2, 2.0f, 0x90707070);
                }

                offsetY += component.getH() + 1;
            }
        }

        @Override
        public void mouseClick(int mouseX, int mouseY, int button) {
            super.mouseClick(mouseX, mouseY, button);
            for (HudComponent component : this.components) {
                component.mouseClick(mouseX, mouseY, button);
            }
        }

        @Override
        public void mouseClickMove(int mouseX, int mouseY, int button) {
            super.mouseClickMove(mouseX, mouseY, button);
            for (HudComponent component : this.components) {
                component.mouseClickMove(mouseX, mouseY, button);
            }
        }

        @Override
        public void mouseRelease(int mouseX, int mouseY, int button) {
            super.mouseRelease(mouseX, mouseY, button);
            for (HudComponent component : this.components) {
                component.mouseRelease(mouseX, mouseY, button);
            }
        }

        @Override
        public void keyTyped(char typedChar, int keyCode) {
            super.keyTyped(typedChar, keyCode);
            for (HudComponent component : this.components) {
                component.keyTyped(typedChar, keyCode);
            }
        }

        private void addComponentToButtons(HudComponent hudComponent) {
            for (HudComponent component : this.components) {
                if (component == hudComponent)
                    continue;

                boolean similarName = hudComponent.getName().toLowerCase()
                        .startsWith(component.getName().toLowerCase());
                if (similarName) {
                    component.subComponents++;
                    hudComponent.setDisplayName(hudComponent.getName().substring(component.getName().length()));
                }
            }
        }
    }
}
