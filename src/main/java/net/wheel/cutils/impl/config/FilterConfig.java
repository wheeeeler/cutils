package net.wheel.cutils.impl.config;

import java.io.File;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.wheel.cutils.api.config.Configurable;
import net.wheel.cutils.api.util.FileUtil;
import net.wheel.cutils.crack;

public final class FilterConfig extends Configurable {

    public FilterConfig(File dir) {
        super(FileUtil.createJsonFile(dir, "Filtered"));
    }

    @Override
    public void onLoad(JsonObject jsonObject) {
        super.onLoad(jsonObject);

        this.getJsonObject().entrySet().forEach(entry -> {
            if ("Filtered".equals(entry.getKey()) && entry.getValue().isJsonArray()) {
                final JsonArray filteredJsonArray = entry.getValue().getAsJsonArray();
                for (JsonElement jsonElement : filteredJsonArray) {
                    if (jsonElement.isJsonPrimitive()) {
                        final String filteredWord = jsonElement.getAsString();
                        crack.INSTANCE.getFilterManager().addFilteredWord(filteredWord);
                    } else {
                        System.err.println("Invalid jsonElement in FilterConfig.onLoad");
                    }
                }
            } else {
                System.err.println("Filtered key is missing or not an array in FilterConfig.onLoad");
            }
        });
    }

    @Override
    public void onSave() {
        JsonObject save = new JsonObject();
        JsonArray filteredJsonArray = new JsonArray();
        crack.INSTANCE.getFilterManager().getFilteredWords().forEach(filteredJsonArray::add);
        save.add("Filtered", filteredJsonArray);
        this.saveJsonObjectToFile(save);
    }
}
