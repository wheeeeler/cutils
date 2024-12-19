package net.wheel.cutils.impl.module.CRACK;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import net.wheel.cutils.api.chatrelayhandler.ChatRelayManager;
import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventReceivePacket;
import net.wheel.cutils.api.event.render.EventRender2D;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.api.util.StringUtil;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class ChatTweaksModule extends Module {

    private final Value<Boolean> relay = new Value<>("CrackRelay", new String[] { "Relay" }, "Relay chat messages",
            false);
    private final Value<Boolean> sc = new Value<>("Staffchat", new String[] { "SC" }, "Prefix chat messages with @",
            false);
    public final Value<Boolean> ignored = new Value<>("Ignored", new String[] { "ig" },
            "Hide messages from ignored users", true);
    public final Value<Boolean> filter = new Value<>("Filter", new String[] { "fl" },
            "Hide messages containing filtered words or phrases", true);
    public final Value<Boolean> unicode = new Value<>("Fix unicode", new String[] { "uc" },
            "Reverts \"Fancy Chat\" characters back into normal ones", true);
    public final Value<Boolean> spam = new Value<>("Remove spam", new String[] { "sp", "s" },
            "Attempts to prevent spam by checking recent chat messages for duplicates", true);
    public final Value<Boolean> death = new Value<>("Remove deaths", new String[] { "dead", "d" },
            "Attempts to prevent death messages", false);
    public final Value<Mode> mode = new Value<>("AutoCon", new String[] { "sock", "s" },
            "enable/disable websocket connection", Mode.Disabled);

    private final List<String> cache = new ArrayList<>();
    private static final Minecraft mc = Minecraft.getMinecraft();

    public ChatTweaksModule() {
        super("ChatTweaks", new String[] { "ChatOption" }, "tweak da chat", "NONE", -1, ModuleType.CRACK);
        MinecraftForge.EVENT_BUS.register(this);

    }

    @Listener
    public void onRender2D(EventRender2D event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || !(mc.currentScreen instanceof GuiChat))
            return;

        GuiChat chatGui = (GuiChat) mc.currentScreen;
        GuiTextField inputField = chatGui.inputField;

        int x = inputField.x - 2;
        int y = inputField.y - 2;
        int width = inputField.width;
        int height = inputField.height;

        int backgroundColor = 0xAA1E1E1E;

        if (relay.getValue()) {
            backgroundColor = 0xFFCD00CD;
        } else if (sc.getValue()) {
            backgroundColor = 0xFF910B10;
        }

        RenderUtil.drawRect(x, y, x + width, y + height, backgroundColor);
    }

    @SubscribeEvent
    public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        handleConnectionEvent(event);
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        handleConnectionEvent(event);
    }

    private void handleConnectionEvent(Event event) {
        if (this.mode.getValue() == Mode.Enabled) {
            if (event instanceof FMLNetworkEvent.ClientConnectedToServerEvent
                    && !ChatRelayManager.getInstance().isConnected()) {
                ChatRelayManager.getInstance().start();
            }
            if (event instanceof FMLNetworkEvent.ClientDisconnectionFromServerEvent) {
                ChatRelayManager.getInstance().stop();
            }
        }
    }

    @SubscribeEvent
    public void sendChatMessage(ClientChatEvent event) {
        String message = event.getMessage();

        if (!isEnabled()) {
            return;
        }

        if (message.startsWith(".")) {
            return;
        }

        if (message.startsWith("/")) {
            return;
        }

        if (message.startsWith("@")) {
            return;
        }

        if (message.startsWith("!")) {
            return;
        }

        if (message.startsWith("#")) {
            event.setMessage(message.substring(1));
            return;
        }

        if (relay.getValue()) {
            ChatRelayManager.getInstance().sendMessage(message);
            event.setCanceled(true);
            return;
        }

        if (sc.getValue()) {
            event.setMessage("@" + message);
        }
    }

    @Listener
    public void receivePacket(EventReceivePacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof SPacketChat) {
                final SPacketChat packet = (SPacketChat) event.getPacket();
                final String chatMessage = packet.getChatComponent().getUnformattedText();
                final String senderName = extractSenderName(chatMessage);

                if (senderName != null && senderName.equals(Minecraft.getMinecraft().player.getName())) {
                    return;
                }

                if (this.ignored.getValue()) {
                    if (senderName != null && crack.INSTANCE.getIgnoredManager().find(senderName) != null) {
                        event.setCanceled(true);
                        return;
                    }
                }

                if (this.filter.getValue()) {
                    for (String word : crack.INSTANCE.getFilterManager().getFilteredWords()) {
                        if (chatMessage.toLowerCase().contains(word)) {
                            event.setCanceled(true);
                            return;
                        }
                    }
                }

                if (this.death.getValue()) {
                    if (packet.getChatComponent().getFormattedText().contains("\2474")
                            || packet.getChatComponent().getFormattedText().contains("\247c")) {
                        event.setCanceled(true);
                        return;
                    }
                }

                if (this.spam.getValue()) {
                    if (!this.cache.isEmpty()) {
                        for (String s : this.cache) {
                            final double diff = StringUtil.levenshteinDistance(s, chatMessage);

                            if (diff >= 0.75f) {
                                event.setCanceled(true);
                                return;
                            }
                        }
                    }

                    this.cache.add(chatMessage);

                    if (this.cache.size() >= 10) {
                        this.cache.remove(0);
                    }
                }

                if (this.unicode.getValue()) {
                    if (packet.getChatComponent() instanceof TextComponentString) {
                        final TextComponentString component = (TextComponentString) packet.getChatComponent();

                        final StringBuilder sb = new StringBuilder();

                        boolean containsUnicode = false;

                        for (String s : component.getFormattedText().split(" ")) {
                            StringBuilder line = new StringBuilder();
                            for (char c : s.toCharArray()) {
                                if (c >= 0xFEE0) {
                                    c -= 0xFEE0;
                                    containsUnicode = true;
                                }
                                line.append(c);
                            }
                            sb.append(line).append(" ");
                        }

                        if (containsUnicode) {
                            packet.chatComponent = new TextComponentString(sb.toString());
                        }
                    }
                }
            }
        }
    }

    private String extractSenderName(String chatMessage) {
        if (chatMessage.startsWith("<") && chatMessage.contains(">")) {
            int endIndex = chatMessage.indexOf(">");
            if (endIndex > 1) {
                return chatMessage.substring(1, endIndex).trim();
            }
        }
        return null;
    }

    private enum Mode {
        Enabled, Disabled
    }
}
