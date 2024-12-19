package net.wheel.cutils.impl.gui.menu;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.GuiModList;

import net.wheel.cutils.api.event.minecraft.EventDisplayGui;
import net.wheel.cutils.api.gui.hud.particle.ParticleSystem;
import net.wheel.cutils.api.gui.menu.MainMenuButton;
import net.wheel.cutils.api.texture.RandomTexture;
import net.wheel.cutils.api.texture.Texture;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.fml.cUtils;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class GuiCutilsMainMenu extends GuiScreen {

    private MainMenuButton singlePlayer;
    private MainMenuButton multiPlayer;
    private MainMenuButton options;
    private MainMenuButton hudEditor;
    private MainMenuButton mods;
    private MainMenuButton quit;

    private Texture cutilsLogo;

    private RandomTexture cutilsBackground;

    private ScaledResolution scaledResolution;

    private ParticleSystem particleSystem;

    public GuiCutilsMainMenu() {
        crack.INSTANCE.getEventManager().addEventListener(this);
    }

    @Override
    public void initGui() {
        super.initGui();

        final GuiCutilsMainMenu menu = this;
        final Minecraft mc = Minecraft.getMinecraft();
        this.scaledResolution = new ScaledResolution(mc);

        if (this.cutilsLogo == null)
            this.cutilsLogo = new Texture("cutils-logo.png");

        if (this.particleSystem == null)
            this.particleSystem = new ParticleSystem(this.scaledResolution);

        if (this.cutilsBackground == null)
            this.cutilsBackground = new RandomTexture("bgs/");

        crack.INSTANCE.getHudEditor().onResize(mc, this.scaledResolution.getScaledWidth(),
                this.scaledResolution.getScaledHeight());

        float height = (this.scaledResolution.getScaledHeight() / 4.0f) + mc.fontRenderer.FONT_HEIGHT / 2.0f + 18;

        this.singlePlayer = new MainMenuButton(this.scaledResolution.getScaledWidth() / 2.0f - 70, height,
                "Singleplayer") {
            @Override
            public void action() {
                mc.displayGuiScreen(new GuiWorldSelection(menu));
            }
        };

        height += 20;

        this.multiPlayer = new MainMenuButton(this.scaledResolution.getScaledWidth() / 2.0f - 70, height,
                "Multiplayer") {
            @Override
            public void action() {
                mc.displayGuiScreen(new GuiMultiplayer(menu));
            }
        };

        height += 20;

        this.options = new MainMenuButton(this.scaledResolution.getScaledWidth() / 2.0f - 70, height, "Options") {
            @Override
            public void action() {
                mc.displayGuiScreen(new GuiOptions(menu, mc.gameSettings));
            }
        };

        height += 20;

        this.mods = new MainMenuButton(this.scaledResolution.getScaledWidth() / 2.0f - 70, height, "Mods") {
            @Override
            public void action() {
                mc.displayGuiScreen(new GuiModList(menu));
            }
        };

        height += 20;

        this.hudEditor = new MainMenuButton(this.scaledResolution.getScaledWidth() / 2.0f - 70, height,
                ChatFormatting.GRAY + "GUI") {
            @Override
            public void action() {
                mc.displayGuiScreen(crack.INSTANCE.getHudEditor());
            }
        };

        height += 20;

        this.quit = new MainMenuButton(this.scaledResolution.getScaledWidth() / 2.0f - 70, height, "Quit") {
            @Override
            public void action() {
                mc.shutdown();
            }
        };
    }

    @Listener
    public void displayScreen(EventDisplayGui event) {
        if (crack.INSTANCE.getConfigManager().isCustomMainMenuHidden())
            return;

        if (Minecraft.getMinecraft().world != null)
            return;

        if (event.getScreen() == null) {
            event.setCanceled(true);
            Minecraft.getMinecraft().displayGuiScreen(this);
        }

        if (Minecraft.getMinecraft().currentScreen instanceof GuiCutilsMainMenu && event.getScreen() == null) {
            event.setCanceled(true);
        }

        if (event.getScreen() != null) {
            if (event.getScreen() instanceof GuiMainMenu) {
                event.setCanceled(true);
                Minecraft.getMinecraft().displayGuiScreen(this);
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (this.particleSystem != null) {
            this.particleSystem.setScaledResolution(this.scaledResolution);
            this.particleSystem.update();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.cutilsBackground != null) {
            this.cutilsBackground.bind();
            this.cutilsBackground.render(0, 0, this.scaledResolution.getScaledWidth(),
                    this.scaledResolution.getScaledHeight());
        }

        if (this.particleSystem != null)
            this.particleSystem.render(mouseX, mouseY);

        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        this.cutilsLogo.bind();
        this.cutilsLogo.render((this.scaledResolution.getScaledWidth() / 2.0f) - 120,
                (this.scaledResolution.getScaledHeight() / 8.0f), 240, 38);

        this.drawSplashText();

        this.singlePlayer.render(mouseX, mouseY, partialTicks);
        this.multiPlayer.render(mouseX, mouseY, partialTicks);
        this.options.render(mouseX, mouseY, partialTicks);
        this.mods.render(mouseX, mouseY, partialTicks);
        this.hudEditor.render(mouseX, mouseY, partialTicks);
        this.quit.render(mouseX, mouseY, partialTicks);

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        try {
            this.singlePlayer.mouseClicked(mouseX, mouseY, mouseButton);
            this.multiPlayer.mouseClicked(mouseX, mouseY, mouseButton);
            this.options.mouseClicked(mouseX, mouseY, mouseButton);
            this.mods.mouseClicked(mouseX, mouseY, mouseButton);
            this.hudEditor.mouseClicked(mouseX, mouseY, mouseButton);
            this.quit.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.singlePlayer.mouseRelease(mouseX, mouseY, state);
        this.multiPlayer.mouseRelease(mouseX, mouseY, state);
        this.options.mouseRelease(mouseX, mouseY, state);
        this.mods.mouseRelease(mouseX, mouseY, state);
        this.hudEditor.mouseRelease(mouseX, mouseY, state);
        this.quit.mouseRelease(mouseX, mouseY, state);
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h) {
        super.onResize(mcIn, w, h);

        this.particleSystem = new ParticleSystem(new ScaledResolution(mcIn));
        crack.INSTANCE.getHudEditor().onResize(mcIn, w, h);
    }

    private void drawSplashText() {
        final Minecraft mc = Minecraft.getMinecraft();
        final ScaledResolution res = new ScaledResolution(mc);

        float logoY = (this.scaledResolution.getScaledHeight() / 8.0f);
        float singlePlayerY = (this.scaledResolution.getScaledHeight() / 4.0f) + mc.fontRenderer.FONT_HEIGHT / 2.0f
                + 18;
        float splashY = (logoY + singlePlayerY) / 2 + 10;

        final String spash = ChatFormatting.RED + "ohayo, " + ChatFormatting.RED + ChatFormatting.BOLD
                + mc.getSession().getUsername();
        this.drawString(this.fontRenderer, spash,
                (this.scaledResolution.getScaledWidth() / 2) - (mc.fontRenderer.getStringWidth(spash) / 2),
                (int) splashY, -1);

        final String version = ChatFormatting.RED + "Version " + cUtils.VERSION + " for "
                + Minecraft.getMinecraft().getVersion();
        this.drawString(this.fontRenderer, version, res.getScaledWidth() - mc.fontRenderer.getStringWidth(version) - 1,
                res.getScaledHeight() - mc.fontRenderer.FONT_HEIGHT, -1);

        final String copyright = ChatFormatting.RED + "(C) " + ChatFormatting.ITALIC + "CrackInc";
        this.drawString(this.fontRenderer, copyright,
                res.getScaledWidth() - mc.fontRenderer.getStringWidth(copyright) - 1,
                res.getScaledHeight() - (mc.fontRenderer.FONT_HEIGHT * 3), -1);
    }

    public void unload() {
        crack.INSTANCE.getEventManager().removeEventListener(this);
    }
}
