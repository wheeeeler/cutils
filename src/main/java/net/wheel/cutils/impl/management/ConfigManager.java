package net.wheel.cutils.impl.management;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.config.Configurable;
import net.wheel.cutils.api.event.client.EventLoadConfig;
import net.wheel.cutils.api.event.client.EventSaveConfig;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.*;

public final class ConfigManager {

    public static final String CONFIG_PATH = "crack/Config/%s/";
    public String activeConfig;
    @Getter
    private File configDir;
    @Getter
    private File moduleConfigDir;
    @Getter
    private File hudComponentConfigDir;
    @Getter
    @Setter
    private boolean firstLaunch = false;
    @Getter
    @Setter
    private boolean customMainMenuHidden = false;
    @Getter
    @Setter
    private List<Configurable> configurableList = new ArrayList<>();
    @Getter
    private ChatGameConfig chatGameConfig;

    public ConfigManager() {
        this.activeConfig = readActiveConfig();
        this.generateDirectories();
    }

    public void switchToConfig(final String config) {
        this.saveAll();

        this.activeConfig = config;
        this.writeActiveConfig(config);

        crack.INSTANCE.unloadSimple();
        crack.INSTANCE.init();
    }

    public String readActiveConfig() {
        try {
            final byte[] bytes = Files.readAllBytes(Paths.get("crack/Config/ACTIVE.txt"));
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "base";
        }
    }

    public void writeActiveConfig(final String config) {
        try {
            final FileOutputStream fos = new FileOutputStream("crack/Config/ACTIVE.txt");
            fos.write(config.getBytes());
            fos.close();
        } catch (IOException e) {
            System.err.println("Could not create file ACTIVE.txt in config directory.");
        }
    }

    private void generateDirectories() {
        this.configDir = new File(String.format(CONFIG_PATH, activeConfig));
        if (!this.configDir.exists()) {
            this.setFirstLaunch(true);
            this.configDir.mkdirs();
        }

        this.moduleConfigDir = new File(String.format(CONFIG_PATH, activeConfig) + "Modules" + "/");
        if (!this.moduleConfigDir.exists()) {
            this.moduleConfigDir.mkdirs();
        }

        this.hudComponentConfigDir = new File(String.format(CONFIG_PATH, activeConfig) + "HudComponents" + "/");
        if (!this.hudComponentConfigDir.exists()) {
            this.hudComponentConfigDir.mkdirs();
        }
    }

    public void init() {

        crack.INSTANCE.getModuleManager().getModuleList().forEach(module -> {
            this.configurableList.add(new ModuleConfig(this.moduleConfigDir, module));
        });

        crack.INSTANCE.getHudManager().getComponentList().forEach(hudComponent -> {
            this.configurableList.add(new HudConfig(this.hudComponentConfigDir, hudComponent));
        });

        this.configurableList.add(new ClientConfig(configDir));
        this.configurableList.add(new FriendConfig(configDir));
        this.configurableList.add(new XrayConfig(configDir));
        this.configurableList.add(new ItemFindConfig(configDir));
        this.configurableList.add(new MacroConfig(configDir));
        this.configurableList.add(new WorldConfig(configDir));
        this.configurableList.add(new IgnoreConfig(configDir));
        this.configurableList.add(new NukerFilterConfig(configDir));
        this.configurableList.add(new FilterConfig(configDir));
        this.configurableList.add(new MuteConfig(configDir));
        this.configurableList.add(new RenderCancelConfig(configDir));
        this.configurableList.add(new PacketCancelConfig(configDir));
        this.configurableList.add(new ColorConfig(configDir));

        this.chatGameConfig = new ChatGameConfig(configDir);
        this.configurableList.add(this.chatGameConfig);

        if (this.firstLaunch) {
            this.saveAll();
        } else {
            this.loadAll();
        }
    }

    public void save(Class configurableClassType) {
        for (Configurable cfg : configurableList) {
            if (cfg.getClass().isAssignableFrom(configurableClassType)) {
                cfg.onSave();
            }
        }

        crack.INSTANCE.getEventManager().dispatchEvent(new EventSaveConfig());
    }

    public void saveAll() {
        for (Configurable cfg : configurableList) {
            cfg.onSave();
        }
        crack.INSTANCE.getEventManager().dispatchEvent(new EventSaveConfig());
    }

    public void load(Class configurableClassType) {
        for (Configurable cfg : configurableList) {
            if (cfg.getClass().isAssignableFrom(configurableClassType)) {
                cfg.onLoad(null);
            }
        }
        crack.INSTANCE.getEventManager().dispatchEvent(new EventLoadConfig());
    }

    public void loadAll() {
        for (Configurable cfg : configurableList) {
            cfg.onLoad(null);
        }
        crack.INSTANCE.getEventManager().dispatchEvent(new EventLoadConfig());
    }

    public void addConfigurable(Configurable configurable) {
        this.configurableList.add(configurable);
    }

}
