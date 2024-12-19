package net.wheel.cutils.impl.config;

import java.io.File;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.wheel.cutils.api.config.Configurable;
import net.wheel.cutils.api.util.FileUtil;
import net.wheel.cutils.crack;

public final class IgnoreConfig extends Configurable {

    public IgnoreConfig(File dir) {
        super(FileUtil.createJsonFile(dir, "Ignored"));
    }

    @Override
    public void onLoad(JsonObject jsonObject) {
        super.onLoad(jsonObject);

        final JsonArray ignoredJsonArray = this.getJsonObject().get("Ignored").getAsJsonArray();

        for (JsonElement jsonElement : ignoredJsonArray) {
            final String blacklistedName = jsonElement.getAsString();
            crack.INSTANCE.getIgnoredManager().add(blacklistedName);
        }
    }

    @Override
    public void onSave() {
        JsonObject save = new JsonObject();
        JsonArray ignoredJsonArray = new JsonArray();
        crack.INSTANCE.getIgnoredManager().getIgnoredList().forEach(ignored -> ignoredJsonArray.add(ignored.getName()));
        save.add("Ignored", ignoredJsonArray);
        this.saveJsonObjectToFile(save);
    }
}
