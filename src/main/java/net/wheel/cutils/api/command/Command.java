package net.wheel.cutils.api.command;

import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.util.text.TextComponentString;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.cutils.crack;

public abstract class Command {

    private String displayName;
    private String[] alias;
    private String desc;
    private String usage;
    private ObjectArrayList<String> arguments;

    private TextComponentString textComponentUsage;

    public Command() {
    }

    public Command(String displayName, String[] alias, String desc, String usage) {
        this.displayName = displayName;
        this.alias = alias;
        this.desc = desc;
        this.usage = usage;
    }

    public Command(String displayName, String[] alias, String desc, String usage, ObjectArrayList<String> arguments) {
        this.displayName = displayName;
        this.alias = alias;
        this.desc = desc;
        this.usage = usage;
        this.arguments = arguments;
    }

    public Command(String displayName, String[] alias, String desc, TextComponentString textComponentUsage) {
        this(displayName, alias, desc, textComponentUsage.getText());
        this.textComponentUsage = textComponentUsage;
    }

    public abstract void exec(String input);

    public boolean clamp(String input, int min, int max) {
        String[] split = input.split(" ");
        if (split.length > max) {
            crack.INSTANCE.errorChat("Too much input");
            return false;
        }
        if (split.length < min) {
            crack.INSTANCE.errorChat("empty input");
            return false;
        }
        return true;
    }

    public boolean clamp(String input, int min) {
        String[] split = input.split(" ");
        if (split.length < min) {
            crack.INSTANCE.errorChat("empty input");
            return false;
        }
        return true;
    }

    public boolean equals(String[] list, String input) {
        for (String s : list) {
            if (s.equalsIgnoreCase(input)) {
                return true;
            }
        }
        return false;
    }

    public void printUsage() {
        final String[] usage = this.getUsage().split("\n");
        crack.INSTANCE.logChat(ChatFormatting.GRAY + this.getDisplayName() + " usage: ");

        if (this.textComponentUsage != null) {
            this.getTextComponentUsage().getSiblings().forEach(crack.INSTANCE::logcChat);
        } else {
            for (String u : usage) {
                crack.INSTANCE.logChat(u);
            }
        }
    }

    public String tabComplete(String input) {
        if (arguments == null || arguments.isEmpty())
            return null;

        String[] splitInput = input.split(" ");
        if (splitInput.length == 1) {
            return displayName;
        } else {
            String currentArg = splitInput[splitInput.length - 1].toLowerCase();
            for (String arg : arguments) {
                if (arg.toLowerCase().startsWith(currentArg)) {
                    return arg;
                }
            }
        }
        return null;
    }

    public List<String> getCommandArgs(String[] args) {
        return arguments != null ? arguments : new ObjectArrayList<>();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String[] getAlias() {
        return alias;
    }

    public void setAlias(String[] alias) {
        this.alias = alias;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public ObjectArrayList<String> getArguments() {
        return arguments;
    }

    public void setArguments(ObjectArrayList<String> arguments) {
        this.arguments = arguments;
    }

    public TextComponentString getTextComponentUsage() {
        return textComponentUsage;
    }

    public void setTextComponentUsage(TextComponentString textComponentUsage) {
        this.textComponentUsage = textComponentUsage;
    }
}
