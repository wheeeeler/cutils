package net.wheel.cutils.api.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import net.wheel.cutils.api.util.RenderUtil;

public class Texture {

    private final ResourceLocation textureLocation;

    public Texture(String name) {
        this.textureLocation = new ResourceLocation("cutils", "textures/" + name);
        this.bind();
    }

    public void render(float x, float y, float width, float height, float u, float v, float t, float s) {
        this.bind();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtil.drawTexture(x, y, width, height, u, v, t, s);
    }

    public void render(float x, float y, float textureX, float textureY, float width, float height) {
        this.bind();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtil.drawTexture(x, y, textureX, textureY, width, height);
    }

    public void render(float x, float y, float width, float height) {
        this.render(x, y, width, height, 0, 0, 1, 1);
    }

    public void bind() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.textureLocation);
    }
}
