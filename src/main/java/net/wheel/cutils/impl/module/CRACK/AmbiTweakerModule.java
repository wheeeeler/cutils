package net.wheel.cutils.impl.module.CRACK;

import java.awt.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import net.wheel.cutils.api.event.render.EventRenderSky;
import net.wheel.cutils.api.event.world.EventRainStrength;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.texture.Texture;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public class AmbiTweakerModule extends Module {

    private static final ResourceLocation END_SKY_TEXTURES = new ResourceLocation("textures/environment/end_sky.png");
    public final Value<SkyMode> skyMode = new Value<SkyMode>("SkyMode",
            new String[] { "Sky", "Sm", "SkieMode", "Skie", "Skies" }, "Edit the skybox", SkyMode.CUTILS);
    public final Value<Color> skyColor = new Value<Color>("SkyColor",
            new String[] { "SkyCol", "Sc", "SkieColor", "SkieCol", "Color", "C" },
            "Edit the skybox color (COLOR mode only)", new Color(0, 127, 255));
    public final Value<Integer> skyGamma = new Value<Integer>("SkyGamma",
            new String[] { "SkyGam", "SkyG", "Sg", "Gamma", "G" }, "Edit the skybox gamma", 128, 1, 255, 1);
    public final Value<Integer> skyGammaEnd = new Value<Integer>("SkyGammaEnd",
            new String[] { "SkyGamEnd", "SkyGe", "Sge", "GammaEnd", "GamEnd", "Ge" },
            "Edit the skybox gamma (END mode only)", 40, 1, 255, 1);
    public final Value<Boolean> weather = new Value<Boolean>("Weather", new String[] { "Rain", "R" },
            "Edit the weather", false);

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Texture cutilsSkyTexture;

    public AmbiTweakerModule() {
        super("EnvTweaker", new String[] { "comfy", "CustomSky", "CustomSound", "CustomSounds" },
                "Edit ambient parts of the game. (Sky, sounds, etc.)", "NONE", -1, ModuleType.CRACK);
        this.cutilsSkyTexture = new Texture("cutils_sky.jpg");
    }

    @Listener
    public void onRenderSky(EventRenderSky event) {
        if (this.skyMode.getValue() != SkyMode.NORMAL) {
            event.setCanceled(true);
            this.renderSky();
        }
    }

    @Listener
    public void onRainStrength(EventRainStrength event) {
        if (this.weather.getValue() && Minecraft.getMinecraft().world != null) {
            event.setCanceled(true);
        }
    }

    private void renderSky() {
        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.depthMask(false);
        boolean needsTexture = false;
        switch (this.skyMode.getValue()) {
            case CUTILS:
                this.cutilsSkyTexture.bind();
                needsTexture = true;
                break;
            case END:
                this.mc.getRenderManager().renderEngine.bindTexture(END_SKY_TEXTURES);
                needsTexture = true;
                break;
        }
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        if (needsTexture) {
            GlStateManager.enableTexture2D();
        } else {
            GlStateManager.disableTexture2D();
        }

        for (int k1 = 0; k1 < 6; ++k1) {
            GlStateManager.pushMatrix();
            if (k1 == 1) {
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (k1 == 2) {
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (k1 == 3) {
                GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            }

            if (k1 == 4) {
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            }

            if (k1 == 5) {
                GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
            }

            if (needsTexture) {
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            } else {
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
            }

            switch (this.skyMode.getValue()) {
                case CUTILS:
                case COLOR:
                    bufferbuilder
                            .pos(-100.0D, -100.0D, -100.0D).color(this.skyColor.getValue().getRed(),
                                    this.skyColor.getValue().getGreen(), this.skyColor.getValue().getBlue(), 255)
                            .endVertex();
                    bufferbuilder
                            .pos(-100.0D, -100.0D, 100.0D).color(this.skyColor.getValue().getRed(),
                                    this.skyColor.getValue().getGreen(), this.skyColor.getValue().getBlue(), 255)
                            .endVertex();
                    bufferbuilder
                            .pos(100.0D, -100.0D, 100.0D).color(this.skyColor.getValue().getRed(),
                                    this.skyColor.getValue().getGreen(), this.skyColor.getValue().getBlue(), 255)
                            .endVertex();
                    bufferbuilder
                            .pos(100.0D, -100.0D, -100.0D).color(this.skyColor.getValue().getRed(),
                                    this.skyColor.getValue().getGreen(), this.skyColor.getValue().getBlue(), 255)
                            .endVertex();
                    break;
                case END:
                    bufferbuilder.pos(-100.0D, -100.0D, -100.0D).tex(0.0D, 0.0D).color(this.skyGammaEnd.getValue(),
                            this.skyGammaEnd.getValue(), this.skyGammaEnd.getValue(), 255).endVertex();
                    bufferbuilder.pos(-100.0D, -100.0D, 100.0D).tex(0.0D, 16.0D).color(this.skyGammaEnd.getValue(),
                            this.skyGammaEnd.getValue(), this.skyGammaEnd.getValue(), 255).endVertex();
                    bufferbuilder.pos(100.0D, -100.0D, 100.0D).tex(16.0D, 16.0D).color(this.skyGammaEnd.getValue(),
                            this.skyGammaEnd.getValue(), this.skyGammaEnd.getValue(), 255).endVertex();
                    bufferbuilder.pos(100.0D, -100.0D, -100.0D).tex(16.0D, 0.0D).color(this.skyGammaEnd.getValue(),
                            this.skyGammaEnd.getValue(), this.skyGammaEnd.getValue(), 255).endVertex();
                    break;
                case NONE:
                    bufferbuilder.pos(-100.0D, -100.0D, -100.0D).color(10, 10, 10, 255).endVertex();
                    bufferbuilder.pos(-100.0D, -100.0D, 100.0D).color(10, 10, 10, 255).endVertex();
                    bufferbuilder.pos(100.0D, -100.0D, 100.0D).color(10, 10, 10, 255).endVertex();
                    bufferbuilder.pos(100.0D, -100.0D, -100.0D).color(10, 10, 10, 255).endVertex();
                    break;
            }

            tessellator.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
    }

    public enum SkyMode {
        NORMAL, COLOR, CUTILS, END, NONE
    }
}
