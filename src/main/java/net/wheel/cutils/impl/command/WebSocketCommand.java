package net.wheel.cutils.impl.command;

import net.wheel.cutils.api.chatrelayhandler.ChatRelayManager;
import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;

public final class WebSocketCommand extends Command {

    public WebSocketCommand() {
        super("ws", new String[] { "ws" }, "turn on/off ws", "ws connect [c] / disconnect [dc]");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2)) {
            this.printUsage();
            return;
        }

        String[] args = input.split(" ", 2);
        if (args.length < 2) {
            this.printUsage();
            return;
        }

        String action = args[1].toLowerCase();
        ChatRelayManager manager = ChatRelayManager.getInstance();

        switch (action) {
            case "connect":
            case "c":
                if (manager.isConnected()) {
                    crack.INSTANCE.logChat("Connection already active vro");
                } else {
                    manager.start();
                }
                break;

            case "disconnect":
            case "dc":
                if (!manager.isConnected()) {
                    crack.INSTANCE.logChat("Not connected vro");
                } else {
                    manager.stop();
                }
                break;

            default:
                this.printUsage();
                break;
        }
    }
}
