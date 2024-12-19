package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.friend.Friend;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.FriendConfig;

public final class FriendCommand extends Command {

    private final String[] addAlias = new String[] { "Add" };
    private final String[] removeAlias = new String[] { "Remove" };
    private final String[] listAlias = new String[] { "List" };
    private final String[] clearAlias = new String[] { "Clear" };

    public FriendCommand() {
        super("crack", new String[] { "F" }, "Allows you to add or remove crack (friends)", "crack Add <Username>\n" +
                "crack Add <Username> <Alias>\n" +
                "crack Remove <Username>\n" +
                "crack List\n" +
                "crack Clear");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 4)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        if (equals(addAlias, split[1])) {
            if (!this.clamp(input, 3, 4)) {
                this.printUsage();
                return;
            }

            final String username = split[2];
            final Friend friend = crack.INSTANCE.getFriendManager().find(username);

            if (friend != null) {
                crack.INSTANCE.logChat("\u00A7c" + username + " \u00A7fis already your friend");
            } else {
                if (split.length > 3) {
                    final String alias = split[3];
                    crack.INSTANCE.logChat("Added \u00A7c" + username + " \u00A7fas \u00A7c" + alias + "\u00A7f");
                    crack.INSTANCE.getFriendManager().add(username, alias, true);
                } else {
                    crack.INSTANCE.logChat("Added \u00A7c" + username + " \u00A7f");
                    crack.INSTANCE.getFriendManager().add(username, username, true);
                }
                crack.INSTANCE.getConfigManager().save(FriendConfig.class);
            }
        } else if (equals(removeAlias, split[1])) {
            if (!this.clamp(input, 3, 3)) {
                this.printUsage();
                return;
            }

            final int friends = crack.INSTANCE.getFriendManager().getFriendList().size();
            if (friends == 0) {
                crack.INSTANCE.logChat("\u00A7cYou don't have any friends :(");
                return;
            }

            final String username = split[2];
            final Friend friend = crack.INSTANCE.getFriendManager().find(username);

            if (friend != null) {
                crack.INSTANCE.logChat("Removed \u00A7c" + friend.getAlias() + " \u00A7f");
                crack.INSTANCE.getFriendManager().getFriendList().remove(friend);
                crack.INSTANCE.getConfigManager().save(FriendConfig.class);
            } else {
                crack.INSTANCE.logChat("\u00A7c" + username + " \u00A7fis not your friend");
            }
        } else if (equals(listAlias, split[1])) {
            if (!this.clamp(input, 2, 2)) {
                this.printUsage();
                return;
            }

            final int size = crack.INSTANCE.getFriendManager().getFriendList().size();
            if (size > 0) {
                final TextComponentString msg = new TextComponentString("\u00A7cFriends [" + size + "]\u00A7f ");

                for (int i = 0; i < size; i++) {
                    final Friend friend = crack.INSTANCE.getFriendManager().getFriendList().get(i);
                    if (friend != null) {
                        msg.appendSibling(new TextComponentString(
                                "\u00A7a" + friend.getAlias() + "\u00A7c" + ((i == size - 1) ? "" : ", "))
                                        .setStyle(new Style()
                                                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                        new TextComponentString("Name: " + friend.getName() + "\nUUID: "
                                                                + friend.getUuid())))));
                    }
                }
                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(msg);
            } else {
                crack.INSTANCE.logChat("\u00A7cYou don't have any friends :(");
            }
        } else if (equals(clearAlias, split[1])) {
            if (!this.clamp(input, 2, 2)) {
                this.printUsage();
                return;
            }

            final int friends = crack.INSTANCE.getFriendManager().getFriendList().size();
            if (friends > 0) {
                crack.INSTANCE.logChat("Removed \u00A7c" + friends + "\u00A7f friend" + (friends > 1 ? "s" : ""));
                crack.INSTANCE.getFriendManager().getFriendList().clear();
                crack.INSTANCE.getConfigManager().save(FriendConfig.class);
            } else {
                crack.INSTANCE.logChat("\u00A7cYou don't have any friends :(");
            }
        } else {
            crack.INSTANCE.errorChat("\u00A7cUnknown input \u00A7f\"" + input + "\"");
            this.printUsage();
        }
    }

    @Override
    public ObjectArrayList<String> getCommandArgs(String[] args) {
        ObjectArrayList<String> suggestions = new ObjectArrayList<>();

        if (args.length == 1) {
            suggestions.add("\u00A7aAdd");
            suggestions.add("\u00A7aRemove");
            suggestions.add("\u00A7aList");
            suggestions.add("\u00A7aClear");
        } else if (args.length == 2) {
            if (equals(addAlias, args[1])) {
                suggestions.add("\u00A7a<Sername>");
            } else if (equals(removeAlias, args[1])) {
                for (Friend friend : crack.INSTANCE.getFriendManager().getFriendList()) {
                    suggestions.add("\u00A7c" + friend.getAlias());
                }
            }
        } else if (args.length == 3 && equals(addAlias, args[1])) {
            suggestions.add("\u00A7a<Alias>");
        }

        return suggestions;
    }
}
