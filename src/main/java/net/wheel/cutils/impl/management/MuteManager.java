package net.wheel.cutils.impl.management;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Getter;

import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.MuteConfig;

@Getter
public final class MuteManager {

    private final List<String> mutedModIds = new CopyOnWriteArrayList<>();

    public void addMutedModId(String modId) {
        this.mutedModIds.add(modId.toLowerCase());
        crack.INSTANCE.getConfigManager().save(MuteConfig.class);
    }

    public void removeMutedModId(String modId) {
        this.mutedModIds.remove(modId.toLowerCase());
        crack.INSTANCE.getConfigManager().save(MuteConfig.class);
    }

    public void clearMutedModIds() {
        this.mutedModIds.clear();
        crack.INSTANCE.getConfigManager().save(MuteConfig.class);
    }

    public void unload() {

    }
}
