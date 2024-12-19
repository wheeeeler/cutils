package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.util.StringUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.WorldConfig;
import net.wheel.cutils.impl.management.WorldManager;

public final class SeedCommand extends Command {

    public SeedCommand() {
        super("Seed", new String[] { "RandomSeed" }, "Sets the client-side seed used by certain features",
                "Seed <Number>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 2)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        if (StringUtil.isLong(split[1], 10)) {
            final ServerData serverData = Minecraft.getMinecraft().getCurrentServerData();
            if (serverData != null) {
                final WorldManager.WorldData worldData = crack.INSTANCE.getWorldManager().find(serverData.serverIP);
                final long seed = Long.parseLong(split[1]);
                if (worldData != null) {
                    worldData.setSeed(seed);
                } else {
                    crack.INSTANCE.getWorldManager().getWorldDataList()
                            .add(new WorldManager.WorldData(serverData.serverIP, seed));
                }
                crack.INSTANCE.logChat("Set " + serverData.serverIP + "'s seed to " + seed);
                crack.INSTANCE.getConfigManager().save(WorldConfig.class);
            } else {
                crack.INSTANCE.errorChat("Cannot set seed for localhost");
            }
        } else {
            crack.INSTANCE.errorChat("Unknown number " + "\247f\"" + split[1] + "\"");
        }
    }
}
