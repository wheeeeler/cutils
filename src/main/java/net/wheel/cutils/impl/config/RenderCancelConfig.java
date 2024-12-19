package net.wheel.cutils.impl.config;

import java.io.File;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.wheel.cutils.api.config.Configurable;
import net.wheel.cutils.api.util.FileUtil;
import net.wheel.cutils.crack;

public final class RenderCancelConfig extends Configurable {

    public RenderCancelConfig(File dir) {
        super(FileUtil.createJsonFile(dir, "RenderCancel"));
    }

    @Override
    public void onLoad(JsonObject jsonObject) {
        super.onLoad(jsonObject);

        this.getJsonObject().entrySet().forEach(entry -> {
            if ("RenderCancel".equals(entry.getKey()) && entry.getValue().isJsonArray()) {
                final JsonArray renderCancelJsonArray = entry.getValue().getAsJsonArray();
                for (JsonElement jsonElement : renderCancelJsonArray) {
                    if (jsonElement.isJsonPrimitive()) {
                        final String renderCancelModId = jsonElement.getAsString().toLowerCase();
                        crack.INSTANCE.getRenderCancelManager().addModId(renderCancelModId);
                    } else {
                        System.err.println("Invalid jsonElement in RenderCancelConfig.onLoad");
                    }
                }
            } else {
                System.err.println("RenderCancel key is missing or not an array in RenderCancelConfig.onLoad");
            }
        });
    }

    @Override
    public void onSave() {
        JsonObject save = new JsonObject();
        JsonArray renderCancelJsonArray = new JsonArray();

        crack.INSTANCE.getRenderCancelManager().getRenderManagedModIds().forEach(renderCancelJsonArray::add);

        save.add("RenderCancel", renderCancelJsonArray);
        this.saveJsonObjectToFile(save);
    }
}
