package net.wheel.cutils.impl.command;

import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.ModuleConfig;

public final class BindCommand extends Command {

    private final String[] clearAlias = new String[] { "Clear" };

    public BindCommand() {
        super("Bind", new String[] { "B" }, "Allows you to change keybinds for modules",
                "Bind <Module> <Key>\nBind Clear");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 3)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        if (equals(clearAlias, split[1])) {
            if (!this.clamp(input, 2, 2)) {
                this.printUsage();
                return;
            }

            int count = 0;

            for (Module mod : crack.INSTANCE.getModuleManager().getModuleList()) {
                if (mod.getType() != Module.ModuleType.HIDDEN && mod.getKey() != null && !mod.getKey().equals("NONE")) {
                    count++;
                    mod.setKey("NONE");
                }
            }

            if (count > 0) {
                crack.INSTANCE.logChat("Removed " + count + " Bind" + (count > 1 ? "s" : ""));
            } else {
                crack.INSTANCE.logChat("You have no binds");
            }
        } else {
            if (!this.clamp(input, 3, 3)) {
                this.printUsage();
                return;
            }

            final Module mod = crack.INSTANCE.getModuleManager().find(split[1]);

            if (mod != null) {
                if (mod.getType() == Module.ModuleType.HIDDEN) {
                    crack.INSTANCE.errorChat("Cannot change bind of " + "\247f\"" + mod.getDisplayName() + "\"");
                } else {
                    if (split[2].equalsIgnoreCase(mod.getKey())) {
                        crack.INSTANCE.logChat(
                                "\247c" + mod.getDisplayName() + "'s\247f key is already " + split[2].toUpperCase());
                    } else {
                        if (split[2].equalsIgnoreCase("NONE")) {
                            crack.INSTANCE.logChat(
                                    "Bound \247c" + mod.getDisplayName() + "\247f to " + split[2].toUpperCase());
                            mod.setKey(split[2].toUpperCase());
                            crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
                        } else if (Keyboard.getKeyIndex(split[2].toUpperCase()) != Keyboard.KEY_NONE) {
                            crack.INSTANCE.logChat(
                                    "Bound \247c" + mod.getDisplayName() + "\247f to " + split[2].toUpperCase());
                            mod.setKey(split[2].toUpperCase());
                            crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
                        } else {
                            crack.INSTANCE.logChat("\247c" + split[2] + "\247f is not a valid key");
                        }
                    }
                }
            } else {
                crack.INSTANCE.errorChat("Unknown module " + "\247f\"" + split[1] + "\"");
                final Module similar = crack.INSTANCE.getModuleManager().findSimilar(split[1]);
                if (similar != null) {
                    crack.INSTANCE.logChat("Did you mean " + "\247c" + similar.getDisplayName() + "\247f?");
                }
            }
        }
    }

    @Override
    public ObjectArrayList<String> getCommandArgs(String[] args) {
        ObjectArrayList<String> suggestions = new ObjectArrayList<>();

        switch (args.length) {
            case 1:
                for (Module mod : crack.INSTANCE.getModuleManager().getModuleList()) {
                    suggestions.add(mod.getDisplayName());
                }
                suggestions.addAll(Arrays.asList(clearAlias));
                break;
            case 2:
                for (int i = 0; i < Keyboard.KEYBOARD_SIZE; i++) {
                    String keyName = Keyboard.getKeyName(i);
                    if (keyName != null) {
                        suggestions.add(keyName);
                    }
                }
                suggestions.add("NONE");
                break;
            default:
                break;
        }

        return suggestions;
    }
}
