package net.wheel.cutils.impl.command;

import java.io.File;

import com.google.gson.JsonObject;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.config.Configurable;
import net.wheel.cutils.api.gui.hud.component.HudComponent;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.util.FileUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.*;

public final class ExportCommand extends Command {
    public ExportCommand() {
        super("Export", new String[] { "Exprt" },
                "Export all Module & HUD configs into a single json for upload on crack's website",
                "Export <config_name>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 2)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");
        final String configName = split[1];
        final File file = FileUtil.createJsonFile(crack.INSTANCE.getConfigManager().getConfigDir(), configName);

        JsonObject endJson = new JsonObject();
        for (Configurable cfg : crack.INSTANCE.getConfigManager().getConfigurableList()) {
            if (cfg.getClass().equals(ClientConfig.class)) {
                final JsonObject clientJson = cfg.convertJsonObjectFromFile();
                endJson.add("Client", clientJson);
            }
            if (cfg.getClass().equals(XrayConfig.class)) {
                final JsonObject xrayJson = cfg.convertJsonObjectFromFile();
                endJson.add("Xray", xrayJson);
            }
            if (cfg.getClass().equals(ItemFindConfig.class)) {
                final JsonObject searchJson = cfg.convertJsonObjectFromFile();
                endJson.add("Search", searchJson);
            }
            if (cfg.getClass().equals(ModuleConfig.class)) {
                final JsonObject moduleJson = cfg.convertJsonObjectFromFile();
                final ModuleConfig moduleConfig = (ModuleConfig) cfg;
                final Module module = moduleConfig.getModule();
                endJson.add("Module" + module.getDisplayName(), moduleJson);
            }
            if (cfg.getClass().equals(HudConfig.class)) {
                final JsonObject hudJson = cfg.convertJsonObjectFromFile();
                final HudConfig hudConfig = (HudConfig) cfg;
                final HudComponent hudComponent = hudConfig.getHudComponent();
                endJson.add("HudComponent" + hudComponent.getName(), hudJson);
            }
        }

        FileUtil.saveJsonFile(file, endJson);

        crack.INSTANCE.logChat("\247c" + "Exported config " + configName + ".json into the crack directory");
    }

}
