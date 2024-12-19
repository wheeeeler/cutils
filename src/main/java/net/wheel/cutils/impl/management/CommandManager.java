package net.wheel.cutils.impl.management;

import java.util.Comparator;

import net.minecraft.util.text.TextComponentString;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.util.StringUtil;
import net.wheel.cutils.api.value.Regex;
import net.wheel.cutils.api.value.Shader;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.command.*;
import net.wheel.cutils.impl.config.ModuleConfig;

@Getter
@Setter
public final class CommandManager {

    private ObjectArrayList<Command> commandList = new ObjectArrayList<>();

    public CommandManager() {
        this.commandList.add(new HelpCommand());
        this.commandList.add(new ToggleCommand());
        this.commandList.add(new VClipCommand());
        this.commandList.add(new DirectionClipCommand());
        this.commandList.add(new HideCommand());
        this.commandList.add(new ColorCommand());
        this.commandList.add(new BindCommand());
        this.commandList.add(new XrayCommand());
        this.commandList.add(new FriendCommand());
        this.commandList.add(new PeekCommand());
        this.commandList.add(new SpectateCommand());
        this.commandList.add(new ModuleCommand());
        this.commandList.add(new YawCommand());
        this.commandList.add(new PitchCommand());
        this.commandList.add(new NameCommand());
        this.commandList.add(new MacroCommand());
        this.commandList.add(new ReloadCommand());
        this.commandList.add(new UnloadCommand());
        this.commandList.add(new DupeCommand());
        this.commandList.add(new InvSeeCommand());
        this.commandList.add(new SayCommand());
        this.commandList.add(new IPCommand());
        this.commandList.add(new CoordsCommand());
        this.commandList.add(new ConnectCommand());
        this.commandList.add(new DisconnectCommand());
        this.commandList.add(new SeedCommand());
        this.commandList.add(new TpChunkCommand());
        this.commandList.add(new IgnoreCommand());
        this.commandList.add(new PythonCommand());
        this.commandList.add(new FakeChatCommand());
        this.commandList.add(new EnchantCommand());
        this.commandList.add(new RenameCommand());
        this.commandList.add(new RenameModuleCommand());
        this.commandList.add(new SpawnEggCommand());
        this.commandList.add(new StackSizeCommand());
        this.commandList.add(new CrashSlimeCommand());
        this.commandList.add(new SignBookCommand());
        this.commandList.add(new SkullCommand());
        this.commandList.add(new GiveCommand());
        this.commandList.add(new CalcStrongholdCommand());
        this.commandList.add(new LastInvCommand());
        this.commandList.add(new ItemFindCommand());
        this.commandList.add(new LocateFeatureCommand());
        this.commandList.add(new MainMenuCommand());
        this.commandList.add(new ConfigCommand());
        this.commandList.add(new NukerFilterCommand());
        this.commandList.add(new ExportCommand());
        this.commandList.add(new LoadCommand());
        this.commandList.add(new SaveCommand());
        this.commandList.add(new GiveExtendedCommand());
        this.commandList.add(new FilterCommand());
        this.commandList.add(new AttributeCommand());
        this.commandList.add(new NbtCommand());
        this.commandList.add(new MuteCommand());
        this.commandList.add(new ConsoleCommand());
        this.commandList.add(new RenderCancelCommand());
        this.commandList.add(new InfoCommand());
        this.commandList.add(new PacketCancelCommand());
        this.commandList.add(new WebSocketCommand());

        loadValueCommands();

        commandList.sort(Comparator.comparing(Command::getDisplayName));
    }

    public void cutils(String input) {
        String[] split = input.split(" ", 2);
        Command command = find(split[0]);
        if (command != null) {
            command.exec(input);
        } else {
            crack.INSTANCE.errorChat("not a command: " + split[0]);
        }
    }

    public void loadValueCommands() {
        for (final Module module : crack.INSTANCE.getModuleManager().getModuleList()) {
            if (!module.getValueList().isEmpty()) {
                this.commandList.add(new Command(module.getDisplayName(), module.getAlias(),
                        module.getDesc() != null ? module.getDesc() : "There is no description for this command",
                        module.toUsageTextComponent()) {

                    @Override
                    public TextComponentString getTextComponentUsage() {
                        return module.toUsageTextComponent();
                    }

                    @Override
                    public void exec(String input) {
                        if (!this.clamp(input, 2, 3)) {
                            this.printUsage();
                            return;
                        }

                        final String[] split = input.split(" ");

                        final Value v = module.findValue(split[1]);

                        if (v != null) {
                            if (v.getValue() instanceof Boolean) {
                                if (split.length == 3) {
                                    if (split[2].equalsIgnoreCase("true") || split[2].equalsIgnoreCase("false")
                                            || split[2].equalsIgnoreCase("1") || split[2].equalsIgnoreCase("0")) {
                                        if (split[2].equalsIgnoreCase("1")) {
                                            v.setValue(true);
                                            crack.INSTANCE.logChat(module.getDisplayName() + " \u00a7c" + v.getName()
                                                    + "\247f set to \247atrue");
                                            crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
                                        } else if (split[2].equalsIgnoreCase("0")) {
                                            v.setValue(false);
                                            crack.INSTANCE.logChat(module.getDisplayName() + " \u00a7c" + v.getName()
                                                    + "\247f set to \247cfalse");
                                            crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
                                        } else {
                                            v.setValue(Boolean.parseBoolean(split[2]));
                                            crack.INSTANCE.logChat(module.getDisplayName() + " \u00a7c" + v.getName()
                                                    + "\247f set to " + ((Boolean) v.getValue() ? "\247a" : "\247c")
                                                    + v.getValue());
                                            crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
                                        }
                                    } else {
                                        crack.INSTANCE.errorChat(
                                                "Invalid input " + "\"" + split[2] + "\" expected true/false");
                                    }
                                } else {
                                    v.setValue(!((Boolean) v.getValue()));
                                    crack.INSTANCE.logChat(
                                            module.getDisplayName() + " \u00a7c" + v.getName() + "\247f set to "
                                                    + ((Boolean) v.getValue() ? "\247a" : "\247c") + v.getValue());
                                    crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
                                }
                            }

                            if (v.getValue() instanceof String) {
                                if (!this.clamp(input, 3, 3)) {
                                    this.printUsage();
                                    return;
                                }
                                v.setValue(split[2]);
                                crack.INSTANCE.logChat(module.getDisplayName() + " \u00a7c" + v.getName()
                                        + "\247f set to " + split[2]);
                                crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
                            }

                            if (v.getValue() instanceof Number && !(v.getValue() instanceof Enum)) {
                                if (!this.clamp(input, 3, 3)) {
                                    this.printUsage();
                                    return;
                                }
                                if (v.getValue().getClass() == Float.class) {
                                    if (StringUtil.isFloat(split[2])) {
                                        v.setValue(Float.parseFloat(split[2]));
                                        crack.INSTANCE.logChat(module.getDisplayName() + " \u00a7c" + v.getName()
                                                + "\247f set to \247b" + Float.parseFloat(split[2]));
                                        crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
                                    } else {
                                        crack.INSTANCE
                                                .errorChat("Invalid input " + "\"" + split[2] + "\" expected a number");
                                    }
                                }
                                if (v.getValue().getClass() == Double.class) {
                                    if (StringUtil.isDouble(split[2])) {
                                        v.setValue(Double.parseDouble(split[2]));
                                        crack.INSTANCE.logChat(module.getDisplayName() + " \u00a7c" + v.getName()
                                                + "\247f set to \247b" + Double.parseDouble(split[2]));
                                        crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
                                    } else {
                                        crack.INSTANCE
                                                .errorChat("Invalid input " + "\"" + split[2] + "\" expected a number");
                                    }
                                }
                                if (v.getValue().getClass() == Integer.class) {
                                    if (StringUtil.isInt(split[2])) {
                                        v.setValue(Integer.parseInt(split[2]));
                                        crack.INSTANCE.logChat(module.getDisplayName() + " \u00a7c" + v.getName()
                                                + "\247f set to \247b" + Integer.parseInt(split[2]));
                                        crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
                                    } else {
                                        crack.INSTANCE
                                                .errorChat("Invalid input " + "\"" + split[2] + "\" expected a number");
                                    }
                                }
                            }

                            if (v.getValue() instanceof Enum) {
                                if (!this.clamp(input, 3, 3) || split[2].matches("-?\\d+(\\.\\d+)?")) {
                                    this.printUsage();
                                    return;
                                }

                                final int op = v.getEnum(split[2]);

                                if (op != -1) {
                                    v.setEnumValue(split[2]);
                                    crack.INSTANCE.logChat(module.getDisplayName() + " \u00a7c" + v.getName()
                                            + "\247f set to \247e" + ((Enum) v.getValue()).name().toLowerCase());
                                    crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
                                } else {
                                    crack.INSTANCE
                                            .errorChat("Invalid input " + "\"" + split[2] + "\" expected a string");
                                }
                            }

                            if (v.getValue() instanceof Regex) {
                                if (!this.clamp(input, 2, 3)) {
                                    this.printUsage();
                                    return;
                                }

                                final Regex regex = (Regex) v.getValue();
                                if (split.length == 2 || split[2].equals("")) {
                                    regex.setPatternString("");
                                    crack.INSTANCE.logChat(
                                            module.getDisplayName() + " \u00a7c" + v.getName() + "\247f cleared");
                                } else {
                                    final String oldPatternString = regex.getPatternString();
                                    regex.setPatternString(split[2]);
                                    if (regex.getPattern() == null) {
                                        regex.setPatternString(oldPatternString);
                                        crack.INSTANCE.errorChat("Invalid input " + "\"" + split[2]
                                                + "\" expected a valid regular expression or an empty string");
                                    } else {
                                        crack.INSTANCE.logChat(module.getDisplayName() + " \u00a7c" + v.getName()
                                                + "\247f set to " + split[2]);
                                        crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
                                    }
                                }
                            }

                            if (v.getValue() instanceof Shader) {
                                if (!this.clamp(input, 2, 3)) {
                                    this.printUsage();
                                    return;
                                }

                                final Shader shader = (Shader) v.getValue();
                                if (split.length == 2 || split[2].equals("")) {
                                    shader.setShaderID("");
                                    crack.INSTANCE.logChat(
                                            module.getDisplayName() + " \u00a7c" + v.getName() + "\247f unset");
                                } else {
                                    final String oldShaderID = shader.getShaderID();
                                    shader.setShaderID(split[2]);
                                    if (shader.getShaderProgram() == null) {
                                        shader.setShaderID(oldShaderID);
                                        crack.INSTANCE.errorChat("Invalid input " + "\"" + split[2]
                                                + "\" expected an existing shader ID or an empty string");
                                    } else {
                                        crack.INSTANCE.logChat(module.getDisplayName() + " \u00a7c" + v.getName()
                                                + "\247f set to " + split[2]);
                                        crack.INSTANCE.getConfigManager().save(ModuleConfig.class);
                                    }
                                }
                            }
                        } else {
                            crack.INSTANCE.errorChat("Invalid input " + "\"" + split[1] + "\"");
                            this.printUsage();
                        }
                    }
                });
            }
        }
    }

    public Command find(String alias) {
        for (Command cmd : this.getCommandList()) {
            for (String s : cmd.getAlias()) {
                if (alias.equalsIgnoreCase(s) || alias.equalsIgnoreCase(cmd.getDisplayName())) {
                    return cmd;
                }
            }
        }
        return null;
    }

    public Command findSimilar(String input) {
        Command cmd = null;
        double similarity = 0.0f;

        for (Command command : this.getCommandList()) {
            final double currentSimilarity = StringUtil.levenshteinDistance(input, command.getDisplayName());

            if (currentSimilarity >= similarity) {
                similarity = currentSimilarity;
                cmd = command;
            }
        }

        return cmd;
    }

    public void unload() {
        for (Command cmd : this.commandList) {
            crack.INSTANCE.getEventManager().removeEventListener(cmd);
        }
        this.commandList.clear();
    }

}
