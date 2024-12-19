package net.wheel.cutils.api.texture;

import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.cutils.api.util.RenderUtil;

public class RandomTexture {

    private final ResourceLocation textureLocation;
    private static final Random RANDOM = new Random();

    public RandomTexture(String directory) {
        String randomTexture = getRandomTexture(directory);
        this.textureLocation = new ResourceLocation("cutils", "bgs/" + randomTexture);
        this.bind();
    }

    private String getRandomTexture(String directory) {
        List<String> textures = new ObjectArrayList<>(23);
        IntArrayList indices = new IntArrayList(23);
        for (int i = 1; i <= 23; i++) {
            indices.add(i);
        }

        for (int index : indices) {
            textures.add("w" + index + ".png");
        }

        return textures.get(RANDOM.nextInt(textures.size()));
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
