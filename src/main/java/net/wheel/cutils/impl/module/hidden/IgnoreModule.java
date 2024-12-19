package net.wheel.cutils.impl.module.hidden;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextComponentString;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventReceivePacket;
import net.wheel.cutils.api.friend.Friend;
import net.wheel.cutils.api.ignore.Ignored;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class IgnoreModule extends Module {

    public final Value<Boolean> allowFriends = new Value<Boolean>("AllowFriends",
            new String[] { "AllowF", "Friends", "AF", "F" },
            "If enabled, any friend's message will not be auto-ignored", true);
    private final String REGEX_NAME = "<(\\S+)\\s*(\\S+?)?>\\s(.*)";

    public IgnoreModule() {
        super("Ignore", new String[] { "Ignor" }, "Allows you to ignore people client-side", "NONE", -1,
                ModuleType.HIDDEN);
        this.setHidden(true);
        this.toggle();
    }

    @Listener
    public void receivePacket(EventReceivePacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof SPacketChat && Minecraft.getMinecraft().player != null) {
                final SPacketChat packet = (SPacketChat) event.getPacket();
                if (packet.getChatComponent() instanceof TextComponentString) {
                    final TextComponentString component = (TextComponentString) packet.getChatComponent();
                    final boolean serverMessage = component.getFormattedText().startsWith("\247c")
                            || component.getFormattedText().startsWith("\2474")
                            || component.getFormattedText().startsWith("\2475");
                    final String message = StringUtils.stripControlCodes(component.getFormattedText());
                    if (!serverMessage && !message.isEmpty()) {
                        Pattern chatUsernamePattern = Pattern.compile(REGEX_NAME);
                        Matcher chatUsernameMatcher = chatUsernamePattern.matcher(message);
                        if (chatUsernameMatcher.find()) {
                            String username = chatUsernameMatcher.group(1).replaceAll(">", "").toLowerCase();

                            if (this.allowFriends.getValue()) {
                                final Friend friend = crack.INSTANCE.getFriendManager().find(username);
                                if (friend != null) {
                                    return;
                                }
                            }

                            final Ignored ignored = crack.INSTANCE.getIgnoredManager().find(username);
                            if (ignored != null
                                    && !username.equalsIgnoreCase(Minecraft.getMinecraft().session.getUsername())) {
                                event.setCanceled(true);
                            }
                        }
                    }
                }
            }
        }
    }
}
