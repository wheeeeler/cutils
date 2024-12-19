package net.wheel.cutils.api.event.command;

import net.wheel.cutils.api.command.Command;

public class EventCommandLoad {

    private Command command;

    public EventCommandLoad(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}
