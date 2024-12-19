package net.wheel.cutils.impl.command;

import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.PacketCancelConfig;

public final class PacketCancelCommand extends Command {

    private final String[] addAlias = new String[] { "add" };
    private final String[] removeAlias = new String[] { "remove" };
    private final String[] listAlias = new String[] { "list" };
    private final String[] clearAlias = new String[] { "clear" };

    public PacketCancelCommand() {
        super("PacketCancel", new String[] { "pc" }, "Manage packet channels to cancel",
                "PacketCancel Add <Channel>\n" +
                        "PacketCancel Remove <Channel>\n" +
                        "PacketCancel List\n" +
                        "PacketCancel Clear");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 3)) {
            this.printUsage();
            return;
        }

        String[] split = input.split(" ");
        String subCommand = split[1].toLowerCase();

        if (equals(addAlias, subCommand)) {
            if (split.length != 3) {
                this.printUsage();
                return;
            }
            String channel = split[2];
            crack.INSTANCE.getPacketCancelManager().addChannel(channel);
            crack.INSTANCE.getConfigManager().save(PacketCancelConfig.class);
            crack.INSTANCE.logChat("Added \u00A7c" + channel + "\u00A7f to the packet cancel list");

        } else if (equals(removeAlias, subCommand)) {
            if (split.length != 3) {
                this.printUsage();
                return;
            }
            String channel = split[2];
            crack.INSTANCE.getPacketCancelManager().removeChannel(channel);
            crack.INSTANCE.getConfigManager().save(PacketCancelConfig.class);
            crack.INSTANCE.logChat("Removed \u00A7c" + channel + "\u00A7f from the packet cancel list");

        } else if (equals(listAlias, subCommand)) {
            if (split.length != 2) {
                this.printUsage();
                return;
            }
            int size = crack.INSTANCE.getPacketCancelManager().getCancelledChannels().size();
            if (size > 0) {
                TextComponentString msg = new TextComponentString(
                        "\u00A77Packet cancel channels [" + size + "]\u00A7f: ");
                crack.INSTANCE.getPacketCancelManager().getCancelledChannels().forEach(
                        channel -> msg.appendSibling(new TextComponentString("\u00A7a" + channel + "\u00A7f, ")));
                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(msg);
            } else {
                crack.INSTANCE.logChat("No channels in packet cancel list");
            }

        } else if (equals(clearAlias, subCommand)) {
            if (split.length != 2) {
                this.printUsage();
                return;
            }
            crack.INSTANCE.getPacketCancelManager().clearCancelledChannels();
            crack.INSTANCE.getConfigManager().save(PacketCancelConfig.class);
            crack.INSTANCE.logChat("Cleared packet cancel list");

        } else {
            crack.INSTANCE.errorChat("Unknown input \u00A7f\"" + input + "\"");
            this.printUsage();
        }
    }

    @Override
    public ObjectArrayList<String> getCommandArgs(String[] args) {
        ObjectArrayList<String> suggestions = new ObjectArrayList<>();
        suggestions.addAll(Arrays.asList(addAlias));
        suggestions.addAll(Arrays.asList(removeAlias));
        suggestions.addAll(Arrays.asList(listAlias));
        suggestions.addAll(Arrays.asList(clearAlias));
        return suggestions;
    }
}
