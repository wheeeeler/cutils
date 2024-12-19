package net.wheel.cutils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import org.lwjgl.opengl.Display;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.event.client.EventLoad;
import net.wheel.cutils.api.event.client.EventReload;
import net.wheel.cutils.api.event.client.EventUnload;
import net.wheel.cutils.api.gui.hud.component.ConsoleComponent;
import net.wheel.cutils.api.logging.cutilsFormatter;
import net.wheel.cutils.impl.gui.hud.GuiHudEditor;
import net.wheel.cutils.impl.gui.menu.GuiCutilsMainMenu;
import net.wheel.cutils.impl.management.*;

import hand.interactor.voodoo.EventManager;
import hand.interactor.voodoo.impl.annotated.AnnotatedEventManager;

public final class crack {

    public static final crack INSTANCE = new crack();

    private String prevTitle;

    @Getter
    @Setter
    private static boolean consoleOpen = false;

    private Logger logger;

    private EventManager eventManager;

    private APIManager apiManager;

    private ModuleManager moduleManager;

    private CommandManager commandManager;

    private FriendManager friendManager;

    private ConfigManager configManager;

    private RotationManager rotationManager;

    private MacroManager macroManager;

    private TickRateManager tickRateManager;

    private ChatManager chatManager;

    private WorldManager worldManager;

    private IgnoredManager ignoredManager;

    private CapeManager capeManager;

    private PositionManager positionManager;

    private JoinLeaveManager joinLeaveManager;

    private HudManager hudManager;

    private AnimationManager animationManager;

    private NotificationManager notificationManager;

    private GuiHudEditor hudEditor;

    private GuiCutilsMainMenu cutilsMainMenu;

    private CameraManager cameraManager;

    private ShaderManager shaderManager;

    private FilterManager filterManager;

    private MuteManager muteManager;

    private ColorManager colorManager;

    @Getter
    private ConsoleComponent consoleComponent;

    private RenderCancelManager renderCancelManager;

    private PacketCancelManager packetCancelManager;

    public void init() {
        CrackInit.CPU();
        this.eventManager = new AnnotatedEventManager();
        this.apiManager = new APIManager();
        this.configManager = new ConfigManager();
        this.colorManager = new ColorManager();
        this.ignoredManager = new IgnoredManager();
        this.friendManager = new FriendManager();
        this.rotationManager = new RotationManager();
        this.macroManager = new MacroManager();
        this.tickRateManager = new TickRateManager();
        this.chatManager = new ChatManager();
        this.worldManager = new WorldManager();
        this.capeManager = new CapeManager();
        this.positionManager = new PositionManager();
        this.joinLeaveManager = new JoinLeaveManager();
        this.animationManager = new AnimationManager();
        this.notificationManager = new NotificationManager();
        this.moduleManager = new ModuleManager();
        this.commandManager = new CommandManager();
        this.cameraManager = new CameraManager();
        this.shaderManager = new ShaderManager();
        this.hudManager = new HudManager();
        this.hudEditor = new GuiHudEditor();
        this.cutilsMainMenu = new GuiCutilsMainMenu();
        this.filterManager = new FilterManager();
        this.muteManager = new MuteManager();
        this.renderCancelManager = new RenderCancelManager();
        this.packetCancelManager = new PacketCancelManager();

        this.consoleComponent = new ConsoleComponent();

        this.configManager.init();

        this.prevTitle = Display.getTitle();
        Display.setTitle("cutils 1.12.2");

        this.getEventManager().dispatchEvent(new EventLoad());

        Runtime.getRuntime().addShutdownHook(new Thread("CrackShutdownHook") {

            @Override
            public void run() {
                getConfigManager().saveAll();
            }
        });
    }

    public void errorChat(String message) {
        if (consoleOpen) {
            String colorCode = "\u00A7f";
            ConsoleComponent.addConsoleOutput(message, colorCode);
        } else {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(
                    new TextComponentString("\u00a7f\u00a7l[\u00a7c\u00a7lCU\u00a7f\u00a7l]\u00a7r " + message));
        }
    }

    public void logChat(String message) {
        if (consoleOpen) {
            String colorCode = "\u00A7f";
            ConsoleComponent.addConsoleOutput(message, colorCode);
        } else {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(
                    new TextComponentString("\u00a7f\u00a7l[\u00a7c\u00a7lCU\u00a7f\u00a7l]\u00a7r " + message));
        }
    }

    public void logcChat(ITextComponent textComponent) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI()
                .printChatMessage(new TextComponentString("\u00a7f\u00a7l[\u00a7c\u00a7lCU\u00a7f\u00a7l]\u00a7r ")
                        .appendSibling(textComponent));
    }

    public void logfChat(String format, Object... objects) {
        logChat(String.format(format, objects));
    }

    public void unloadSimple() {
        this.moduleManager.unload();
        this.apiManager.unload();
        this.commandManager.unload();
        this.friendManager.unload();
        this.macroManager.unload();
        this.tickRateManager.unload();
        this.chatManager.unload();
        this.ignoredManager.unload();
        this.capeManager.unload();
        this.joinLeaveManager.unload();
        this.hudManager.unload();
        this.animationManager.unload();
        this.notificationManager.unload();
        this.hudEditor.unload();
        this.cutilsMainMenu.unload();
        this.cameraManager.unload();
        this.shaderManager.unload();
        this.muteManager.unload();
        this.filterManager.unload();
        this.renderCancelManager.unload();
        this.packetCancelManager.unload();
        this.colorManager.unload();
    }

    public void unload() {
        unloadSimple();

        this.getEventManager().dispatchEvent(new EventUnload());

        ModContainer crackContainer = null;

        for (ModContainer modContainer : Loader.instance().getActiveModList()) {
            if (modContainer.getModId().equals("cutils")) {
                crackContainer = modContainer;
            }
        }

        if (crackContainer != null) {
            Loader.instance().getActiveModList().remove(crackContainer);
        }

        Display.setTitle(this.prevTitle);
        Minecraft.getMinecraft().ingameGUI.getChatGUI().clearChatMessages(true);
        System.gc();
    }

    public void reload() {
        this.friendManager.getFriendList().clear();
        this.macroManager.getMacroList().clear();
        this.worldManager.getWorldDataList().clear();
        this.ignoredManager.getIgnoredList().clear();
        this.shaderManager.reload();
        this.capeManager.getCapesMap().clear();
        this.capeManager = new CapeManager();
        this.configManager.getConfigurableList().clear();
        this.configManager = new ConfigManager();
        this.configManager.init();
        this.getEventManager().dispatchEvent(new EventReload());
    }

    private void initLogger() {
        this.logger = Logger.getLogger(crack.class.getName());
        logger.setUseParentHandlers(false);
        final ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new cutilsFormatter());
        logger.addHandler(handler);
    }

    public Logger getLogger() {
        if (this.logger == null) {
            this.initLogger();
        }

        return this.logger;
    }

    public EventManager getEventManager() {
        if (this.eventManager == null) {
            this.eventManager = new AnnotatedEventManager();
        }

        return this.eventManager;
    }

    public APIManager getApiManager() {
        if (this.apiManager == null) {
            this.apiManager = new APIManager();
        }
        return this.apiManager;
    }

    public ModuleManager getModuleManager() {
        if (this.moduleManager == null) {
            this.moduleManager = new ModuleManager();
        }
        return this.moduleManager;
    }

    public CommandManager getCommandManager() {
        if (this.commandManager == null) {
            this.commandManager = new CommandManager();
        }
        return this.commandManager;
    }

    public FriendManager getFriendManager() {
        if (this.friendManager == null) {
            this.friendManager = new FriendManager();
        }
        return this.friendManager;
    }

    public ConfigManager getConfigManager() {
        if (this.configManager == null) {
            this.configManager = new ConfigManager();
        }
        return this.configManager;
    }

    public RotationManager getRotationManager() {
        if (this.rotationManager == null) {
            this.rotationManager = new RotationManager();
        }
        return this.rotationManager;
    }

    public MacroManager getMacroManager() {
        if (this.macroManager == null) {
            this.macroManager = new MacroManager();
        }
        return this.macroManager;
    }

    public TickRateManager getTickRateManager() {
        if (this.tickRateManager == null) {
            this.tickRateManager = new TickRateManager();
        }
        return this.tickRateManager;
    }

    public ChatManager getChatManager() {
        if (this.chatManager == null) {
            this.chatManager = new ChatManager();
        }
        return this.chatManager;
    }

    public WorldManager getWorldManager() {
        if (this.worldManager == null) {
            this.worldManager = new WorldManager();
        }
        return this.worldManager;
    }

    public IgnoredManager getIgnoredManager() {
        if (this.ignoredManager == null) {
            this.ignoredManager = new IgnoredManager();
        }
        return this.ignoredManager;
    }

    public CapeManager getCapeManager() {
        if (this.capeManager == null) {
            this.capeManager = new CapeManager();
        }
        return this.capeManager;
    }

    public PositionManager getPositionManager() {
        if (this.positionManager == null) {
            this.positionManager = new PositionManager();
        }
        return this.positionManager;
    }

    public JoinLeaveManager getJoinLeaveManager() {
        if (this.joinLeaveManager == null) {
            this.joinLeaveManager = new JoinLeaveManager();
        }
        return this.joinLeaveManager;
    }

    public HudManager getHudManager() {
        if (this.hudManager == null) {
            this.hudManager = new HudManager();
        }
        return this.hudManager;
    }

    public AnimationManager getAnimationManager() {
        if (this.animationManager == null) {
            this.animationManager = new AnimationManager();
        }
        return this.animationManager;
    }

    public NotificationManager getNotificationManager() {
        if (this.notificationManager == null) {
            this.notificationManager = new NotificationManager();
        }
        return this.notificationManager;
    }

    public GuiHudEditor getHudEditor() {
        if (this.hudEditor == null) {
            this.hudEditor = new GuiHudEditor();
        }
        return this.hudEditor;
    }

    public GuiCutilsMainMenu getCutilsMainMenu() {
        if (this.cutilsMainMenu == null) {
            this.cutilsMainMenu = new GuiCutilsMainMenu();
        }
        return this.cutilsMainMenu;
    }

    public CameraManager getCameraManager() {
        if (this.cameraManager == null) {
            this.cameraManager = new CameraManager();
        }
        return this.cameraManager;
    }

    public ShaderManager getShaderManager() {
        if (this.shaderManager == null) {
            this.shaderManager = new ShaderManager();
        }
        return this.shaderManager;
    }

    public FilterManager getFilterManager() {
        if (this.filterManager == null) {
            this.filterManager = new FilterManager();
        }
        return this.filterManager;
    }

    public MuteManager getMuteManager() {
        if (this.muteManager == null) {
            this.muteManager = new MuteManager();
        }
        return this.muteManager;
    }

    public RenderCancelManager getRenderCancelManager() {
        if (this.renderCancelManager == null) {
            this.renderCancelManager = new RenderCancelManager();
        }
        return this.renderCancelManager;
    }

    public PacketCancelManager getPacketCancelManager() {
        if (this.packetCancelManager == null) {
            this.packetCancelManager = new PacketCancelManager();
        }
        return this.packetCancelManager;
    }

    public ColorManager getColorManager() {
        if (this.colorManager == null) {
            this.colorManager = new ColorManager();
        }
        return this.colorManager;
    }
}
