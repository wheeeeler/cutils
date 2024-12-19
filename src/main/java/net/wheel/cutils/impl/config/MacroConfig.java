package net.wheel.cutils.impl.config;

import java.io.File;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.wheel.cutils.api.config.Configurable;
import net.wheel.cutils.api.macro.Macro;
import net.wheel.cutils.api.util.FileUtil;
import net.wheel.cutils.crack;

public final class MacroConfig extends Configurable {

    public MacroConfig(File dir) {
        super(FileUtil.createJsonFile(dir, "Macros"));
    }

    @Override
    public void onLoad(JsonObject jsonObject) {
        super.onLoad(jsonObject);

        this.getJsonObject().entrySet().forEach(entry -> {
            final String name = entry.getKey();
            final String key = entry.getValue().getAsJsonArray().get(0).getAsString();
            final String macro = entry.getValue().getAsJsonArray().get(1).getAsString();
            crack.INSTANCE.getMacroManager().getMacroList().add(new Macro(name, key, macro));
        });
    }

    @Override
    public void onSave() {
        JsonObject macroListObject = new JsonObject();
        crack.INSTANCE.getMacroManager().getMacroList().forEach(macro -> {
            JsonArray array = new JsonArray();
            array.add(macro.getKey());
            array.add(macro.getMacro());
            macroListObject.add(macro.getName(), array);
        });
        this.saveJsonObjectToFile(macroListObject.getAsJsonObject());
    }
}
