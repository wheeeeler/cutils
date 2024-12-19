package net.wheel.cutils.impl.config;

import java.io.File;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.wheel.cutils.api.config.Configurable;
import net.wheel.cutils.api.friend.Friend;
import net.wheel.cutils.api.util.FileUtil;
import net.wheel.cutils.crack;

public final class FriendConfig extends Configurable {

    public FriendConfig(File dir) {
        super(FileUtil.createJsonFile(dir, "Friends"));
    }

    @Override
    public void onLoad(JsonObject jsonObject) {
        super.onLoad(jsonObject);

        this.getJsonObject().entrySet().forEach(entry -> {
            final String name = entry.getKey();

            String alias = "";
            String uuid = "";
            JsonArray jsonArray = entry.getValue().getAsJsonArray();

            if (jsonArray != null) {
                if (jsonArray.size() > 0) {
                    alias = jsonArray.get(0).getAsString();
                    if (jsonArray.get(1).isJsonNull()) {
                        uuid = "";
                    } else {
                        uuid = jsonArray.get(1).getAsString();
                    }
                }
            }

            crack.INSTANCE.getFriendManager().getFriendList().add(new Friend(name, uuid, alias));
        });
    }

    @Override
    public void onSave() {
        JsonObject friendsListJsonObject = new JsonObject();
        crack.INSTANCE.getFriendManager().getFriendList().forEach(friend -> {
            JsonArray array = new JsonArray();
            array.add(friend.getAlias());
            array.add(friend.getUuid());
            friendsListJsonObject.add(friend.getName(), array);
        });
        this.saveJsonObjectToFile(friendsListJsonObject);
    }
}
