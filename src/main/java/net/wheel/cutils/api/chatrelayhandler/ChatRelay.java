package net.wheel.cutils.api.chatrelayhandler;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

import javax.net.ssl.SSLContext;

import com.mojang.authlib.GameProfile;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import net.wheel.cutils.crack;

public class ChatRelay extends WebSocketClient {

    private boolean isAuthenticated = false;
    private final ChatRelayManager manager;
    private static final Minecraft mc = Minecraft.getMinecraft();

    public ChatRelay(URI serverUri, ChatRelayManager manager, SSLContext sslContext) {
        super(serverUri);
        if (sslContext != null) {
            this.setSocketFactory(sslContext.getSocketFactory());
        }
        this.manager = manager;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        sendAuthInfo();
    }

    @Override
    public void onMessage(String message) {
        try {
            if (message.startsWith("AUTH_SUCCESS")) {
                isAuthenticated = true;
                crack.INSTANCE.logChat(TextFormatting.GREEN + "Authenticated " + TextFormatting.RESET + "as "
                        + mc.getSession().getUsername());
            } else if (message.startsWith("AUTH_FAILED")) {
                crack.INSTANCE.logChat("Authentication failed.");
                this.close();
            } else {
                manager.receiveMessageFromWebSocket(message);
            }
        } catch (Exception e) {
            crack.INSTANCE.logChat("Error: " + e.getMessage());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        isAuthenticated = false;
        manager.onRelayClosed(reason);
    }

    @Override
    public void onError(Exception ex) {
        isAuthenticated = false;
        manager.onRelayError(ex.getMessage());
    }

    public void sendAuthInfo() {
        GameProfile profile = mc.getSession().getProfile();
        UUID playerUUID = profile.getId();
        String currentServerName = getServerName();
        String authMessage = "AUTH:" + playerUUID + ":" + currentServerName;
        this.send(authMessage);
    }

    public void sendMessage(String message) {
        if (isAuthenticated) {
            this.send("MSG:" + message);
        }
    }

    private String getServerName() {
        String ip;
        try {
            ip = Objects.requireNonNull(mc.getCurrentServerData()).serverIP.toLowerCase();
        } catch (NullPointerException e) {
            return "singleplayer";
        }
        switch (ip) {
            case "play.siriusmc.net":
                return "Hub";
            case "tekkitsmp.siriusmc.net":
                return "SMP";
            case "tekkit.siriusmc.net":
                return "T2";
            case "1122.siriusmc.net":
                return "1122";
            case "rl.siriusmc.net":
                return "RL";
            case "atm9.siriusmc.net":
                return "ATM9";
            default:
                return "unknown";
        }
    }
}
