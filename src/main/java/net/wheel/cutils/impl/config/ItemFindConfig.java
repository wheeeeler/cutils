package net.wheel.cutils.impl.config;

import java.io.File;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.block.Block;

import net.wheel.cutils.api.config.Configurable;
import net.wheel.cutils.api.util.FileUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.module.GLOBAL.ItemFinderModule;

public final class ItemFindConfig extends Configurable {

    private final ItemFinderModule itemFinderModule;

    public ItemFindConfig(File dir) {
        super(FileUtil.createJsonFile(dir, "ItemFindIds"));
        this.itemFinderModule = (ItemFinderModule) crack.INSTANCE.getModuleManager().find("ItemFind");
    }

    @Override
    public void onLoad(JsonObject jsonObject) {
        super.onLoad(jsonObject);

        if (this.itemFinderModule == null)
            return;

        JsonArray searchIdsJsonArray = null;

        final JsonElement blockIds = this.getJsonObject().get("ItemFindBlockIds");
        if (blockIds != null)
            searchIdsJsonArray = blockIds.getAsJsonArray();

        final ItemFinderModule itemFinderModule = (ItemFinderModule) crack.INSTANCE.getModuleManager().find("ItemFind");
        if (itemFinderModule != null) {
            if (searchIdsJsonArray != null) {
                for (JsonElement jsonElement : searchIdsJsonArray) {
                    itemFinderModule.add(jsonElement.getAsInt());
                }
            }
            if (itemFinderModule.getBlockIds().getValue().isEmpty()) {
                itemFinderModule.add("furnace");
            }
        }
    }

    @Override
    public void onSave() {
        if (this.itemFinderModule == null)
            return;

        JsonObject save = new JsonObject();

        JsonArray searchIdsJsonArray = new JsonArray();
        for (Block block : this.itemFinderModule.getBlockIds().getValue())
            searchIdsJsonArray.add(Block.getIdFromBlock(block));

        save.add("ItemFindBlockIds", searchIdsJsonArray);

        this.saveJsonObjectToFile(save);
    }
}
