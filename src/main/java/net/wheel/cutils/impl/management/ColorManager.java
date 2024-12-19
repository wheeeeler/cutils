package net.wheel.cutils.impl.management;

import java.awt.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;

import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.ColorConfig;

@Getter
public final class ColorManager {

    private final Map<String, Color> colors = new ConcurrentHashMap<>();

    public Color getColor(String key) {
        return colors.getOrDefault(key, Color.BLACK);
    }

    public void setColor(String key, Color color) {
        colors.put(key, color);
    }

    public void load() {
        crack.INSTANCE.getConfigManager().load(ColorConfig.class);
    }

    public void unload() {
        colors.clear();
    }
}
