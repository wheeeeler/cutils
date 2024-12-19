package net.wheel.cutils.impl.module.RENDER;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import net.wheel.cutils.api.event.gui.EventRenderPotions;
import net.wheel.cutils.api.event.render.EventRender2D;
import net.wheel.cutils.api.gui.hud.component.DraggableHudComponent;
import net.wheel.cutils.api.gui.hud.component.HudComponent;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.gui.hud.GuiHudEditor;
import net.wheel.cutils.impl.gui.hud.anchor.AnchorPoint;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class HudModule extends Module {

    public final Value<Boolean> hidePotions = new Value<Boolean>("noPotions",
            new String[] { "HidePotions", "HidePots", "Hide_Potions" },
            "Hides the Vanilla potion hud (at the top right of the screen)", true);

    public final Value<Boolean> rainbow = new Value<Boolean>("Rainbow",
            new String[] { "HudRainbow", "Rainbow", "rb", "rain_bow" },
            "Enables rainbow color features across the hud if applicable", false);
    public final Value<Float> rainbowHueDifference = new Value<Float>("HueDifference",
            new String[] { "HudRainbowHueDiff", "HueDiff", "Hd", "RainbowHueDifference", "Rhd" },
            "Control the rainbow hue difference", 2.5f, 1.0f, 5.0f, 0.1f);
    public final Value<Float> rainbowHueSpeed = new Value<Float>("HueSpeed",
            new String[] { "HudRainbowHueSpeed", "Hs", "RainbowHueSpeed", "Rhs" }, "Control the rainbow hue speed",
            50.0f, 1.0f, 100.0f, 1.0f);
    public final Value<Float> rainbowSaturation = new Value<Float>("Saturation",
            new String[] { "HudRainbowSaturation", "sat", "str", "satur", "RainbowSaturation", "Rs" },
            "Control the rainbow saturation", 1.0f, 0.0f, 1.0f, 0.1f);
    public final Value<Float> rainbowBrightness = new Value<Float>("Brightness",
            new String[] { "HudRainbowBrightness", "bri", "bright", "RainbowBrightness", "Rb" },
            "Control the rainbow brightness", 1.0f, 0.0f, 1.0f, 0.1f);

    public HudModule() {
        super("Navigator", new String[] { "Overlay" }, "Renders hud components on the screen", "NONE", -1,
                ModuleType.RENDER);
        this.setHidden(true);
    }

    @Listener
    public void render(EventRender2D event) {
        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.gameSettings.showDebugInfo || mc.currentScreen instanceof GuiHudEditor || mc.player == null) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        for (HudComponent component : crack.INSTANCE.getHudManager().getComponentList()) {
            if (component.isVisible()) {
                if (component instanceof DraggableHudComponent) {
                    final DraggableHudComponent draggableComponent = (DraggableHudComponent) component;
                    if (draggableComponent.getAnchorPoint() != null
                            && draggableComponent.getAnchorPoint().getPoint() == AnchorPoint.Point.TOP_CENTER) {
                        if (!mc.gameSettings.keyBindPlayerList.isKeyDown()) {
                            draggableComponent.render(0, 0, mc.getRenderPartialTicks());
                        }
                    } else {
                        draggableComponent.render(0, 0, mc.getRenderPartialTicks());
                    }
                } else {
                    component.render(0, 0, mc.getRenderPartialTicks());
                }
            }
        }
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Listener
    public void renderPotions(EventRenderPotions event) {
        if (this.hidePotions.getValue()) {
            event.setCanceled(true);
        }
    }
}
