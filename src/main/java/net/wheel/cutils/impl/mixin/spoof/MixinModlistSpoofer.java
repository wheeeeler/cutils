package net.wheel.cutils.impl.mixin.spoof;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.buffer.ByteBuf;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.handshake.FMLHandshakeMessage;

import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.ConfigManager;

@Mixin(FMLHandshakeMessage.ModList.class)
public abstract class MixinModlistSpoofer {

    @Shadow(remap = false)
    private Map<String, String> modTags;

    private static final String SPOOFED_MODLIST_FILE = "spoofed_modlist.txt";
    private static final String HIDDEN_MODID = "cutils";

    @Inject(method = "toBytes", at = @At("HEAD"), cancellable = true, remap = false)
    public void handleModList(ByteBuf buffer, CallbackInfo callbackInfo) {
        if (Minecraft.getMinecraft().isSingleplayer()) {
            crack.INSTANCE.getLogger().config("SP world, running original");
            return;
        }

        Map<String, String> spoofedModTags = loadSpoofedModList();

        if (spoofedModTags.isEmpty()) {
            crack.INSTANCE.getLogger().config("Empty modlist, running original");
            spoofedModTags = new HashMap<>(this.modTags);
        } else {
            crack.INSTANCE.getLogger().config("Using spoofed modlist");
        }

        if (spoofedModTags.remove(HIDDEN_MODID) != null) {
            crack.INSTANCE.errorChat("Cutils hidden in Forge Handshake");
        }

        ByteBufUtils.writeVarInt(buffer, spoofedModTags.size(), 2);

        for (Map.Entry<String, String> modTag : spoofedModTags.entrySet()) {
            ByteBufUtils.writeUTF8String(buffer, modTag.getKey());
            ByteBufUtils.writeUTF8String(buffer, modTag.getValue());
            crack.INSTANCE.getLogger()
                    .config(String.format("Mod Spoof -> %s : %s", modTag.getKey(), modTag.getValue()));
        }

        callbackInfo.cancel();
    }

    private Map<String, String> loadSpoofedModList() {
        Map<String, String> spoofedModTags = new HashMap<>();
        File modlistFile = new File(new File(new ConfigManager().getConfigDir(), "modlists"), SPOOFED_MODLIST_FILE);

        if (!modlistFile.exists()) {
            crack.INSTANCE.getLogger().config("No modlist found: " + modlistFile.getPath());
            return spoofedModTags;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(modlistFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    spoofedModTags.put(parts[0].trim(), parts[1].trim());
                } else {
                    crack.INSTANCE.getLogger().config("Bad format in modlist line: " + line);
                }
            }
        } catch (IOException e) {
            crack.INSTANCE.getLogger().config("Err: reading modlist: " + e.getMessage());
        }

        return spoofedModTags;
    }
}
