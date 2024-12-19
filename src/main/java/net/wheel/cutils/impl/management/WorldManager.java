package net.wheel.cutils.impl.management;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class WorldManager {

    private List<WorldData> worldDataList = new ArrayList<>();

    public WorldManager() {

    }

    public WorldData find(String host) {
        for (WorldData worldData : this.worldDataList) {
            if (worldData.getHost().equalsIgnoreCase(host)) {
                return worldData;
            }
        }
        return null;
    }

    @Getter
    @Setter
    public static class WorldData {
        private String host;
        private long seed;

        public WorldData() {

        }

        public WorldData(String host, long seed) {
            this.host = host;
            this.seed = seed;
        }

    }

}
