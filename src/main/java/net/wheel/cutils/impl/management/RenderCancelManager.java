package net.wheel.cutils.impl.management;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Getter;

import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.RenderCancelConfig;

@Getter
public final class RenderCancelManager {

    private final ObjectList<String> renderManagedModIds = new ObjectArrayList<>();

    public void addModId(String modId) {
        modId = modId.toLowerCase();
        if (!renderManagedModIds.contains(modId)) {
            renderManagedModIds.add(modId);
            crack.INSTANCE.getConfigManager().save(RenderCancelConfig.class);
        }
    }

    public void removeModId(String modId) {
        renderManagedModIds.remove(modId.toLowerCase());
        crack.INSTANCE.getConfigManager().save(RenderCancelConfig.class);
    }

    public void clearRenderManagedModIds() {
        renderManagedModIds.clear();
        crack.INSTANCE.getConfigManager().save(RenderCancelConfig.class);
    }

    public void unload() {

    }
}
