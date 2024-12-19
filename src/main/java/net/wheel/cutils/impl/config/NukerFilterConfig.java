package net.wheel.cutils.impl.config;

import java.io.File;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.block.Block;

import net.wheel.cutils.api.config.Configurable;
import net.wheel.cutils.api.util.FileUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.module.MISC.NukeModule;

public class NukerFilterConfig extends Configurable {
    private final NukeModule nukeModule;

    public NukerFilterConfig(File dir) {
        super(FileUtil.createJsonFile(dir, "NukerFilter"));
        this.nukeModule = (NukeModule) crack.INSTANCE.getModuleManager().find(NukeModule.class);
    }

    @Override
    public void onLoad(JsonObject jsonObject) {
        super.onLoad(jsonObject);

        if (this.nukeModule == null)
            return;

        JsonArray xrayIdsJsonArray = null;

        final JsonElement blockIds = this.getJsonObject().get("NukerFilterIds");
        if (blockIds != null)
            xrayIdsJsonArray = blockIds.getAsJsonArray();

        final NukeModule nukeModule = (NukeModule) crack.INSTANCE.getModuleManager().find("Nuker");
        if (nukeModule != null) {
            if (xrayIdsJsonArray != null) {
                for (JsonElement jsonElement : xrayIdsJsonArray) {
                    nukeModule.add(jsonElement.getAsInt());
                }
            }
        }
    }

    @Override
    public void onSave() {
        if (this.nukeModule == null)
            return;

        if (this.nukeModule.getFilter().getValue() == null)
            return;

        if (this.nukeModule.getFilter().getValue().isEmpty())
            return;

        JsonObject save = new JsonObject();

        JsonArray xrayIdsJsonArray = new JsonArray();
        for (Block block : this.nukeModule.getFilter().getValue())
            xrayIdsJsonArray.add(Block.getIdFromBlock(block));

        save.add("NukerFilterIds", xrayIdsJsonArray);

        this.saveJsonObjectToFile(save);
    }
}
