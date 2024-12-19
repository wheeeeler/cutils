package net.wheel.cutils.impl.config;

import java.io.File;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.wheel.cutils.api.config.Configurable;
import net.wheel.cutils.api.util.FileUtil;
import net.wheel.cutils.crack;

public final class PacketCancelConfig extends Configurable {

    public PacketCancelConfig(File dir) {
        super(FileUtil.createJsonFile(dir, "PacketCancel"));
    }

    @Override
    public void onLoad(JsonObject jsonObject) {
        super.onLoad(jsonObject);

        this.getJsonObject().entrySet().forEach(entry -> {
            if ("PacketCancel".equals(entry.getKey()) && entry.getValue().isJsonArray()) {
                JsonArray packetCancelJsonArray = entry.getValue().getAsJsonArray();
                for (JsonElement jsonElement : packetCancelJsonArray) {
                    if (jsonElement.isJsonPrimitive()) {
                        String packetChannel = jsonElement.getAsString();
                        crack.INSTANCE.getPacketCancelManager().addChannel(packetChannel);
                    } else {
                        System.err.println("Invalid jsonElement in PacketCancelConfig.onLoad");
                    }
                }
            } else {
                System.err.println("PacketCancel key is missing or not an array in PacketCancelConfig.onLoad");
            }
        });
    }

    @Override
    public void onSave() {
        JsonObject save = new JsonObject();
        JsonArray packetCancelJsonArray = new JsonArray();

        crack.INSTANCE.getPacketCancelManager().getCancelledChannels().forEach(packetCancelJsonArray::add);

        save.add("PacketCancel", packetCancelJsonArray);
        this.saveJsonObjectToFile(save);
    }
}
