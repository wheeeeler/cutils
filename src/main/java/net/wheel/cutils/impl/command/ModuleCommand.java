package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.module.hidden.CommandsModule;

public final class ModuleCommand extends Command {

    public ModuleCommand() {
        super("Modules", new String[] { "Mods" }, "Displays all modules", "Modules");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 1, 1)) {
            this.printUsage();
            return;
        }

        final int size = crack.INSTANCE.getModuleManager().getModuleList().size();
        final TextComponentString msg = new TextComponentString("\u00A77Modules [" + size + "]\u00A7f ");

        final CommandsModule commandsModule = (CommandsModule) crack.INSTANCE.getModuleManager()
                .find(CommandsModule.class);

        for (int i = 0; i < size; i++) {
            final Module mod = crack.INSTANCE.getModuleManager().getModuleList().get(i);
            if (mod != null && commandsModule != null) {
                msg.appendSibling(new TextComponentString((mod.isEnabled() ? "\u00A7a" : "\u00A7c")
                        + mod.getDisplayName() + "\u00A7c" + ((i == size - 1) ? "" : ", "))
                                .setStyle(new Style()
                                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                new TextComponentString("\u00A76" + (mod.getDesc() == null
                                                        ? "There is no description for this module"
                                                        : mod.getDesc()) + "\u00A7f")
                                                                .appendSibling(new TextComponentString(
                                                                        mod.toUsageTextComponent() == null ? ""
                                                                                : "\n" + mod.toUsageTextComponent()
                                                                                        .getText()))))
                                        .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                commandsModule.getPrefix().getValue() + "toggle "
                                                        + mod.getDisplayName()))));
            }
        }

        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(msg);
    }
}
