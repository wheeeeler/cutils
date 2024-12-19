package net.wheel.cutils.impl.management;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Getter;

import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.PacketCancelConfig;

@Getter
public final class PacketCancelManager {

    private final ObjectList<String> cancelledChannels = new ObjectArrayList<>();

    public void addChannel(String channel) {
        channel = channel.toLowerCase();
        if (!cancelledChannels.contains(channel)) {
            cancelledChannels.add(channel);
            crack.INSTANCE.getConfigManager().save(PacketCancelConfig.class);
        }
    }

    public void removeChannel(String channel) {
        cancelledChannels.remove(channel.toLowerCase());
        crack.INSTANCE.getConfigManager().save(PacketCancelConfig.class);
    }

    public void clearCancelledChannels() {
        cancelledChannels.clear();
        crack.INSTANCE.getConfigManager().save(PacketCancelConfig.class);
    }

    public void unload() {

    }
}
