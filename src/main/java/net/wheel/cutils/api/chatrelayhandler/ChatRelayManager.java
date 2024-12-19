package net.wheel.cutils.api.chatrelayhandler;

import java.net.URI;

import javax.net.ssl.SSLContext;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import net.wheel.cutils.crack;

public class ChatRelayManager {

    private static ChatRelayManager instance;
    private ChatRelay relay;
    private boolean isRunning = false;
    private boolean isConnected = false;
    private boolean isDisconnecting = false;
    private final Object lock = new Object();

    private static final String WEBSOCKET_URL = "";
    private static final Minecraft mc = Minecraft.getMinecraft();

    private long lastStartTime = 0;
    private long lastStopTime = 0;
    private static final long DELAY = 5000;

    private static final String LOGIC = "LOGICNFACTS";
    private static final String SERNAM = "Sername";
    private static final String LNF = "LOGICNFACTS";
    private static final String SER = "Sername";

    private ChatRelayManager() {
    }

    public static ChatRelayManager getInstance() {
        if (instance == null) {
            synchronized (ChatRelayManager.class) {
                if (instance == null) {
                    instance = new ChatRelayManager();
                }
            }
        }
        return instance;
    }

    public boolean isConnected() {
        synchronized (lock) {
            return isConnected;
        }
    }

    public void start() {
        synchronized (lock) {
            if (isRunning || isConnected) {
                return;
            }
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastStartTime < DELAY) {
                return;
            }
            lastStartTime = currentTime;
            try {
                URI serverUri = new URI(WEBSOCKET_URL);
                SSLContext sslContext = createSSLContext();
                relay = new ChatRelay(serverUri, this, sslContext);
                relay.connect();
                isRunning = true;
                isConnected = true;
            } catch (Exception e) {
                e.printStackTrace();
                crack.INSTANCE.logChat("Failed to start ChatRelay: " + e.getMessage());
            }
        }
    }

    public void stop() {
        synchronized (lock) {
            if (!isConnected || isDisconnecting) {
                return;
            }
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastStopTime < DELAY) {
                return;
            }
            lastStopTime = currentTime;
            if (relay != null && relay.isOpen()) {
                relay.close();
                isDisconnecting = true;
            }
        }
    }

    public void sendMessage(String message) {
        synchronized (lock) {
            if (relay != null && relay.isOpen()) {
                String playerName = mc.getSession().getUsername();
                String formattedMessage = playerName + " > " + message;
                relay.sendMessage(formattedMessage);
                String displayMessage = formatUsername(playerName) + " > " + message;
                crack.INSTANCE.errorChat(displayMessage);
            } else {
                crack.INSTANCE.logChat("No active relay connection.");
            }
        }
    }

    public void receiveMessageFromWebSocket(String message) {
        if (message.startsWith("[D]")) {
            message = message.replaceFirst("\\[D] MSG:", "[D]").trim();
            String formattedMessage = formatFunnyUsernames(message);
            crack.INSTANCE.errorChat(formattedMessage);
        } else {
            if (message.contains(" > ")) {
                String[] parts = message.split(" > ", 2);
                if (parts.length == 2 && parts[0].equals(parts[1].split(" ")[0])) {
                    message = parts[0] + " > " + parts[1].substring(parts[0].length()).trim();
                }
            }
            crack.INSTANCE.errorChat(formatFunnyUsernames(message));
        }
    }

    private String formatFunnyUsernames(String message) {
        return message
                .replaceAll(LOGIC,
                        TextFormatting.DARK_RED + TextFormatting.BOLD.toString() + "L" + TextFormatting.RESET
                                + TextFormatting.WHITE + "OGICNFACTS")
                .replaceAll(SERNAM, TextFormatting.WHITE + "Sernam" + TextFormatting.DARK_RED + TextFormatting.BOLD
                        + "e" + TextFormatting.RESET);
    }

    private String formatUsername(String username) {
        if (username.equalsIgnoreCase(SER)) {
            return TextFormatting.WHITE + "Sernam" + TextFormatting.DARK_RED + TextFormatting.BOLD + "e"
                    + TextFormatting.RESET;
        } else if (username.equalsIgnoreCase(LNF)) {
            return TextFormatting.DARK_RED + TextFormatting.BOLD.toString() + "L" + TextFormatting.RESET
                    + TextFormatting.WHITE + "OGICNFACTS";
        }
        return username;
    }

    public void onRelayClosed(String reason) {
        synchronized (lock) {
            isConnected = false;
            isDisconnecting = false;
            isRunning = false;
            relay = null;
            crack.INSTANCE.logChat("Relay closed: " + reason);
        }
    }

    public void onRelayError(String errorMessage) {
        synchronized (lock) {
            isConnected = false;
            isDisconnecting = false;
            isRunning = false;
            relay = null;
            crack.INSTANCE.logChat("Relay error: " + errorMessage);
        }

    }

    private SSLContext createSSLContext() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, null, null);
        return sslContext;
    }

}
