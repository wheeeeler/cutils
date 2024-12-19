package net.wheel.cutils.impl.module.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;

public final class CrackHudModule extends Module {

    public final Value<Boolean> blur = new Value<Boolean>("Blur", new String[] { "b" },
            "Apply a blur effect to the Hud Editor's background", false);
    public final Value<Boolean> tooltips = new Value<Boolean>("ToolTips", new String[] { "TT", "Tool" },
            "Displays tooltips for modules", true);

    @Getter
    @Setter
    private boolean open;

    public CrackHudModule() {
        super("CrackMenu", new String[] { "HudEdit", "HEdit", "GUI", "ClickGUI" }, "Displays a menu to modify the hud",
                "RCONTROL", -1, ModuleType.UI);
        this.setHidden(true);
    }

    @Override
    public void onToggle() {
        super.onToggle();
        this.displayHudEditor();
    }

    public void displayHudEditor() {
        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.world != null) {
            mc.displayGuiScreen(crack.INSTANCE.getHudEditor());

            if (this.blur.getValue()) {
                if (OpenGlHelper.shadersSupported) {
                    mc.entityRenderer.loadShader(new ResourceLocation("minecraft", "shaders/post/blur.json"));
                }
            }

            this.open = true;

            this.setEnabled(false);
        }
    }

}
