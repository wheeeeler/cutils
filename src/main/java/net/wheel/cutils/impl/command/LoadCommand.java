package net.wheel.cutils.impl.command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.*;

public final class LoadCommand extends Command {

    public LoadCommand() {
        super("Load", new String[] { "Lode" }, "Load a config from your profile on crack's website", "Load <pin>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 2)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        JsonObject configJson = null;
        try {
            final String stringUrl = "" + Minecraft.getMinecraft().player.getUniqueID().toString().replace("-", "")
                    + "/" + split[1];
            URL url = new URL(stringUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.addRequestProperty("User-Agent", "Mozilla/4.76");
            final BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("<pre>") && !line.startsWith("</pre>")) {
                    stringBuilder.append(line);
                }
            }
            reader.close();
            if (!stringBuilder.toString().isEmpty()) {
                configJson = new JsonParser().parse(stringBuilder.toString()).getAsJsonObject();
            }

        } catch (Exception e) {
            e.printStackTrace();
            crack.INSTANCE.logChat("\247c" + "Error loading config from server");
        }

        if (configJson != null) {
            configJson.entrySet().forEach(entry -> {
                if (entry.getKey().equalsIgnoreCase("Client")) {
                    this.loadConfigForClass(ClientConfig.class, entry.getValue().getAsJsonObject());
                }
                if (entry.getKey().equalsIgnoreCase("Xray")) {
                    this.loadConfigForClass(XrayConfig.class, entry.getValue().getAsJsonObject());
                }
                if (entry.getKey().equalsIgnoreCase("Search")) {
                    this.loadConfigForClass(ItemFindConfig.class, entry.getValue().getAsJsonObject());
                }
                crack.INSTANCE.getModuleManager().getModuleList().forEach(module -> {
                    if (entry.getKey().equalsIgnoreCase("Module" + module.getDisplayName())) {
                        this.loadModuleConfigForClass(ModuleConfig.class, entry.getValue().getAsJsonObject(),
                                module.getDisplayName());
                    }
                });
                crack.INSTANCE.getHudManager().getComponentList().forEach(hudComponent -> {
                    if (entry.getKey().equalsIgnoreCase("HudComponent" + hudComponent.getName())) {
                        this.loadHudConfigForClass(HudConfig.class, entry.getValue().getAsJsonObject(),
                                hudComponent.getName());
                    }
                });
            });

            crack.INSTANCE.logChat("\247c" + "Loaded config from server");
        }
    }

    private void loadConfigForClass(Class configClass, JsonObject jsonObject) {
        crack.INSTANCE.getConfigManager().getConfigurableList().stream()
                .filter(configurable -> configurable.getClass().equals(configClass)).forEach(configurable -> {
                    configurable.onLoad(jsonObject);
                });
    }

    private void loadModuleConfigForClass(Class configClass, JsonObject jsonObject, String displayName) {
        crack.INSTANCE.getConfigManager().getConfigurableList().stream()
                .filter(configurable -> configurable.getClass().equals(ModuleConfig.class)).forEach(configurable -> {
                    final ModuleConfig moduleConfig = (ModuleConfig) configurable;
                    if (moduleConfig.getModule().getDisplayName().equalsIgnoreCase(displayName)) {
                        moduleConfig.onLoad(jsonObject);
                    }
                });
    }

    private void loadHudConfigForClass(Class configClass, JsonObject jsonObject, String name) {
        crack.INSTANCE.getConfigManager().getConfigurableList().stream()
                .filter(configurable -> configurable.getClass().equals(HudConfig.class)).forEach(configurable -> {
                    final HudConfig hudConfig = (HudConfig) configurable;
                    if (hudConfig.getHudComponent().getName().equalsIgnoreCase(name)) {
                        hudConfig.onLoad(jsonObject);
                    }
                });
    }

}
