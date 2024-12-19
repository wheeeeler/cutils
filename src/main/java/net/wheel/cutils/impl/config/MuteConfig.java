package net.wheel.cutils.impl.config;

import java.io.File;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.wheel.cutils.api.config.Configurable;
import net.wheel.cutils.api.util.FileUtil;
import net.wheel.cutils.crack;

public final class MuteConfig extends Configurable {

    public MuteConfig(File dir) {
        super(FileUtil.createJsonFile(dir, "Muted"));
    }

    @Override
    public void onLoad(JsonObject jsonObject) {
        super.onLoad(jsonObject);

        this.getJsonObject().entrySet().forEach(entry -> {
            if ("Muted".equals(entry.getKey()) && entry.getValue().isJsonArray()) {
                final JsonArray mutedJsonArray = entry.getValue().getAsJsonArray();
                for (JsonElement jsonElement : mutedJsonArray) {
                    if (jsonElement.isJsonPrimitive()) {
                        final String mutedModId = jsonElement.getAsString();
                        crack.INSTANCE.getMuteManager().addMutedModId(mutedModId);
                    } else {
                        System.err.println("Invalid jsonElement in MuteConfig.onLoad");
                    }
                }
            } else {
                System.err.println("Muted key is missing or not an array in MuteConfig.onLoad");

            }
        });
    }

    @Override
    public void onSave() {
        JsonObject save = new JsonObject();
        JsonArray mutedJsonArray = new JsonArray();
        crack.INSTANCE.getMuteManager().getMutedModIds().forEach(mutedJsonArray::add);
        save.add("Muted", mutedJsonArray);
        this.saveJsonObjectToFile(save);
    }
}
