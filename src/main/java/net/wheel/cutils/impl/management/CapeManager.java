package net.wheel.cutils.impl.management;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import lombok.Getter;

import net.wheel.cutils.api.cape.CapeUser;
import net.wheel.cutils.api.event.player.EventCapeLocation;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

@Getter
public final class CapeManager {

    private static final String BASE_URL = "";
    private static final String MAPPINGS_ENDPOINT = BASE_URL + "/cape_mappings";

    private final List<CapeUser> capeUserList = new ArrayList<>();
    private final HashMap<String, ResourceLocation> capesMap = new HashMap<>();

    public CapeManager() {
        this.downloadCapeUsers();
        this.downloadCapes();
        crack.INSTANCE.getEventManager().addEventListener(this);
    }

    @Listener
    public void displayCape(EventCapeLocation event) {
        if (event.getPlayer() != null && Minecraft.getMinecraft().player != null
                && event.getPlayer() != Minecraft.getMinecraft().player) {
            ResourceLocation cape = this.getCape(event.getPlayer());
            if (cape != null) {
                event.setLocation(cape);
                event.setCanceled(true);
            }
        }
    }

    public void downloadCapeUsers() {
        try {
            URL url = new URL(MAPPINGS_ENDPOINT);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }

                JSONObject jsonResponse = new JSONObject(jsonBuilder.toString());
                jsonResponse.keySet().forEach(uuid -> {
                    try {
                        String capePath = jsonResponse.getString(uuid);
                        if (capePath.toLowerCase().endsWith(".png")) {
                            String fullCapeUrl = BASE_URL + capePath;
                            this.capeUserList.add(new CapeUser(uuid, fullCapeUrl));
                        }
                    } catch (Exception e) {
                        System.err.println("Err: failed to parse cape for UUID " + uuid);
                    }
                });
            }

        } catch (Exception e) {
            System.err.println("Err: downloading cape mappings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void downloadCapes() {
        for (CapeUser capeUser : this.capeUserList) {
            if (!this.capesMap.containsKey(capeUser.getCape())) {
                try {
                    URL url = new URL(capeUser.getCape());
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.addRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);

                    BufferedImage image = ImageIO.read(connection.getInputStream());
                    if (image != null && image.getWidth() <= 2048 && image.getHeight() <= 1024) {
                        DynamicTexture texture = new DynamicTexture(image);
                        ResourceLocation location = Minecraft.getMinecraft().getTextureManager()
                                .getDynamicTextureLocation("cutils/capes", texture);
                        this.capesMap.put(capeUser.getCape(), location);
                    }

                } catch (Exception e) {
                    System.err.println("Err: downloading cape: " + capeUser.getCape() + " - " + e.getMessage());
                }
            }
        }
    }

    public ResourceLocation findResource(String key) {
        return this.capesMap.get(key);
    }

    public ResourceLocation getCape(AbstractClientPlayer player) {
        CapeUser user = this.find(player);
        return user != null ? this.findResource(user.getCape()) : null;
    }

    public CapeUser find(AbstractClientPlayer player) {
        String playerUUID = player.getUniqueID().toString().replace("-", "");
        return this.capeUserList.stream()
                .filter(user -> user.getUuid().equals(playerUUID))
                .findFirst()
                .orElse(null);
    }

    public boolean hasCape() {
        String currentPlayerUUID = Minecraft.getMinecraft().session.getProfile().getId().toString().replace("-", "");
        return this.capeUserList.stream().anyMatch(user -> user.getUuid().equals(currentPlayerUUID));
    }

    public void unload() {
        this.capeUserList.clear();
        crack.INSTANCE.getEventManager().removeEventListener(this);
    }

}
