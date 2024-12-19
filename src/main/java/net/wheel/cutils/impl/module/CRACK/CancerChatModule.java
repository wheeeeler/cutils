package net.wheel.cutils.impl.module.CRACK;

import java.util.Random;

import net.minecraft.network.play.client.CPacketChatMessage;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventSendPacket;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.module.hidden.CommandsModule;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class CancerChatModule extends Module {

    public final Value<Mode> mode = new Value("Mode", new String[] { "Mode", "M" }, "The chat mutator mode to use",
            Mode.LEET);

    public CancerChatModule() {
        super("CancerChat", new String[] { "cancerChat", "cancerM" }, "Modify your outgoing chat messages", "NONE", -1,
                ModuleType.CRACK);
    }

    @Override
    public String getMetaData() {
        return this.mode.getValue().name();
    }

    @Listener
    public void sendPacket(EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof CPacketChatMessage) {
                final CPacketChatMessage packet = (CPacketChatMessage) event.getPacket();

                final CommandsModule cmds = (CommandsModule) crack.INSTANCE.getModuleManager()
                        .find(CommandsModule.class);

                if (cmds != null) {
                    if (packet.getMessage().startsWith("/") || packet.getMessage().startsWith(cmds.prefix.getValue())) {
                        return;
                    }

                    switch (this.mode.getValue()) {
                        case LEET:
                            packet.message = this.leetSpeak(packet.message);
                            break;
                        case FANCY:
                            packet.message = this.fancy(packet.message);
                            break;
                        case RETARD:
                            packet.message = this.retard(packet.message);
                            break;
                        case PIGLATIN:
                            packet.message = this.pigLatin(packet.message);
                            break;
                        case CONSOLE:
                            packet.message = this.console(packet.message);
                            break;
                    }
                }
            }
        }
    }

    public boolean isVowel(char c) {
        return (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U' ||
                c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u');
    }

    public String leetSpeak(String input) {
        input = input.toLowerCase().replace("a", "4");
        input = input.toLowerCase().replace("e", "3");
        input = input.toLowerCase().replace("g", "9");
        input = input.toLowerCase().replace("h", "1");
        input = input.toLowerCase().replace("o", "0");
        input = input.toLowerCase().replace("s", "5");
        input = input.toLowerCase().replace("t", "7");
        input = input.toLowerCase().replace("i", "1");
        return input;
    }

    public String fancy(String input) {
        final StringBuilder sb = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (c >= 0x21 && c <= 0x80) {
                sb.append(Character.toChars(c + 0xFEE0));
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public String retard(String input) {
        final StringBuilder sb = new StringBuilder(input);

        for (int i = 0; i < sb.length(); i += 2) {
            sb.replace(i, i + 1, sb.substring(i, i + 1).toUpperCase());
        }

        return sb.toString();
    }

    public String pigLatin(String s) {

        int len = s.length();
        int index = -1;
        for (int i = 0; i < len; i++) {
            if (isVowel(s.charAt(i))) {
                index = i;
                break;
            }
        }

        if (index == -1)
            return "-1";

        return s.substring(index) +
                s.substring(0, index) + "ay";
    }

    public String console(String input) {
        String ret = "";

        final char[] unicodeChars = new char[] { '\u2E3B',
                '\u26D0',
                '\u26E8',
                '\u26BD',
                '\u26BE',
                '\u26F7',
                '\u23EA',
                '\u23E9',
                '\u23EB',
                '\u23EC',
                '\u2705',
                '\u274C',
                '\u26C4' };

        final int length = input.length();

        for (int i = 1, current = 0; i <= length || current < length; current = i, i += 1) {
            if (current != 0) {
                final Random random = new Random();

                for (int j = 0; j <= 2; j++) {
                    ret += unicodeChars[random.nextInt(unicodeChars.length)];
                }
            }
            if (i <= length) {
                ret += input.substring(current, i);
            } else {
                ret += input.substring(current);
            }
        }

        return ret;
    }

    private enum Mode {
        LEET, FANCY, RETARD, PIGLATIN, CONSOLE
    }

}
