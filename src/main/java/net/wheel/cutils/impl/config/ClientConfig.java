package net.wheel.cutils.impl.config;

import java.io.File;

import com.google.gson.JsonObject;

import net.wheel.cutils.api.config.Configurable;
import net.wheel.cutils.api.util.FileUtil;
import net.wheel.cutils.crack;

public final class ClientConfig extends Configurable {

    public ClientConfig(File dir) {
        super(FileUtil.createJsonFile(dir, "Client"));
    }

    @Override
    public void onLoad(JsonObject jsonObject) {
        super.onLoad(jsonObject);

        this.getJsonObject().entrySet().forEach(entry -> {
            if (entry.getKey().equalsIgnoreCase("CustomMainMenuHidden")) {
                crack.INSTANCE.getConfigManager().setCustomMainMenuHidden(entry.getValue().getAsBoolean());
            }
        });
    }

    @Override
    public void onSave() {
        JsonObject clientConfigJsonObject = new JsonObject();
        clientConfigJsonObject.addProperty("CustomMainMenuHidden",
                crack.INSTANCE.getConfigManager().isCustomMainMenuHidden());
        this.saveJsonObjectToFile(clientConfigJsonObject);
    }
}
