package net.wheel.cutils.impl.config;

import java.awt.Color;
import java.io.File;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import net.wheel.cutils.api.config.Configurable;
import net.wheel.cutils.api.util.FileUtil;
import net.wheel.cutils.crack;

public final class ColorConfig extends Configurable {

    public static final Object2IntOpenHashMap<String> DEFAULT_COLORS;

    static {
        DEFAULT_COLORS = new Object2IntOpenHashMap<>();
        DEFAULT_COLORS.put("Background", 0xFF130F0F);
        DEFAULT_COLORS.put("Border", 0xFFF500DF);
        DEFAULT_COLORS.put("TitleText", 0xFFF500DF);
        DEFAULT_COLORS.put("ListBackground", 0xFF060304);
        DEFAULT_COLORS.put("ScrollbarBackground", 0xFF1C0C1E);
        DEFAULT_COLORS.put("ScrollbarHighlight", 0xC46BC2);
        DEFAULT_COLORS.put("Scrollbar", 0xFF080303);
        DEFAULT_COLORS.put("ModuleEnabled", 0xFF130F0F);
        DEFAULT_COLORS.put("ModuleDisabled", 0xFF130F0F);
        DEFAULT_COLORS.put("ModuleEnabledText", 0xFFF500DF);
        DEFAULT_COLORS.put("ModuleDisabledText", 0xFF8B7F8E);
        DEFAULT_COLORS.put("HoverGradientStart", 0xFFCB00FA);
        DEFAULT_COLORS.put("HoverGradientEnd", 0xFFC678BC);
        DEFAULT_COLORS.put("Hover", 0xC46BC2);
        DEFAULT_COLORS.put("Anchor", 0x75909090);
        DEFAULT_COLORS.put("GridLine", 0x75909090);
        DEFAULT_COLORS.put("CollisionBorder", 0x00000000);
        DEFAULT_COLORS.put("CollisionBackground", 0x3500FF00);
        DEFAULT_COLORS.put("SnapInactiveBackground", 0x00000000);
        DEFAULT_COLORS.put("SnapActiveBackground", 0x35FFFFFF);
        DEFAULT_COLORS.put("SnapBorder", 0x90FFFFFF);
        DEFAULT_COLORS.put("SnapCorner", 0x90FFFFFF);
    }

    public ColorConfig(File dir) {
        super(FileUtil.createJsonFile(dir, "Colors"));
    }

    @Override
    public void onLoad(JsonObject jsonObject) {
        super.onLoad(jsonObject);

        JsonObject colorsObject = this.getJsonObject().getAsJsonObject("Colors");
        if (colorsObject != null) {
            colorsObject.entrySet().forEach(entry -> {
                String colorName = entry.getKey();
                JsonElement colorValue = entry.getValue();
                if (colorValue.isJsonPrimitive()) {
                    crack.INSTANCE.getColorManager().setColor(colorName, new Color(colorValue.getAsInt(), true));
                }
            });
        }

        addDefault();
        onSave();
    }

    @Override
    public void onSave() {
        JsonObject save = new JsonObject();
        JsonObject colorsObject = new JsonObject();

        crack.INSTANCE.getColorManager().getColors().forEach((name, color) -> {
            colorsObject.addProperty(name, color.getRGB());
        });

        save.add("Colors", colorsObject);
        this.saveJsonObjectToFile(save);
    }

    private void addDefault() {
        DEFAULT_COLORS.forEach((name, colorValue) -> {
            crack.INSTANCE.getColorManager().getColors().putIfAbsent(name, new Color(colorValue, true));
        });
    }
}
