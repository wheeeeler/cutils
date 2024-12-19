package net.wheel.cutils.impl.config;

import java.io.File;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.block.Block;

import net.wheel.cutils.api.config.Configurable;
import net.wheel.cutils.api.util.FileUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.module.RENDER.XrayModule;

public final class XrayConfig extends Configurable {

    private final XrayModule xrayModule;

    public XrayConfig(File dir) {
        super(FileUtil.createJsonFile(dir, "XrayIds"));
        this.xrayModule = (XrayModule) crack.INSTANCE.getModuleManager().find("Xray");
    }

    @Override
    public void onLoad(JsonObject jsonObject) {
        super.onLoad(jsonObject);

        if (this.xrayModule == null)
            return;

        JsonArray xrayIdsJsonArray = null;

        final JsonElement blockIds = this.getJsonObject().get("XrayBlockIds");
        if (blockIds != null)
            xrayIdsJsonArray = blockIds.getAsJsonArray();

        final XrayModule xrayModule = (XrayModule) crack.INSTANCE.getModuleManager().find("Xray");
        if (xrayModule != null) {
            if (xrayIdsJsonArray != null) {
                for (JsonElement jsonElement : xrayIdsJsonArray) {
                    xrayModule.add(jsonElement.getAsInt());
                }
            }
            if (xrayModule.getBlocks().getValue().isEmpty()) {
                xrayModule.add("diamond_ore");
            }
        }
    }

    @Override
    public void onSave() {
        if (this.xrayModule == null)
            return;

        JsonObject save = new JsonObject();

        JsonArray xrayIdsJsonArray = new JsonArray();
        for (Block block : this.xrayModule.getBlocks().getValue())
            xrayIdsJsonArray.add(Block.getIdFromBlock(block));

        save.add("XrayBlockIds", xrayIdsJsonArray);

        this.saveJsonObjectToFile(save);
    }
}
