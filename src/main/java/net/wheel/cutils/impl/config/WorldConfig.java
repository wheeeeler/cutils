package net.wheel.cutils.impl.config;

import java.io.File;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.wheel.cutils.api.config.Configurable;
import net.wheel.cutils.api.util.FileUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.WorldManager;

public final class WorldConfig extends Configurable {

    public WorldConfig(File dir) {
        super(FileUtil.createJsonFile(dir, "Worlds"));
    }

    @Override
    public void onLoad(JsonObject jsonObject) {
        super.onLoad(jsonObject);

        this.getJsonObject().entrySet().forEach(entry -> {
            final String host = entry.getKey();
            final String seed = entry.getValue().getAsJsonArray().get(0).getAsString();
            crack.INSTANCE.getWorldManager().getWorldDataList()
                    .add(new WorldManager.WorldData(host, Long.parseLong(seed)));
        });
    }

    @Override
    public void onSave() {
        JsonObject worldListJsonObject = new JsonObject();
        crack.INSTANCE.getWorldManager().getWorldDataList().forEach(worldData -> {
            final JsonArray array = new JsonArray();
            array.add(worldData.getSeed());
            worldListJsonObject.add(worldData.getHost(), array);
        });
        this.saveJsonObjectToFile(worldListJsonObject);
    }
}
