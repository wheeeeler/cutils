package net.wheel.cutils.impl.module.hidden;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;

import net.wheel.cutils.api.event.minecraft.EventKeyPress;
import net.wheel.cutils.api.macro.Macro;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class MacroModule extends Module {

    public MacroModule() {
        super("Macros", new String[] { "mac" }, "macro", "NONE", -1, ModuleType.CRACK);
        this.setHidden(true);
        this.toggle();
    }

    @Listener
    public void keyPress(EventKeyPress event) {
        Keyboard.enableRepeatEvents(true);
        for (Macro macro : crack.INSTANCE.getMacroManager().getMacroList()) {
            if (event.getKey() == Keyboard.getKeyIndex(macro.getKey())
                    && Keyboard.getKeyIndex(macro.getKey()) != Keyboard.KEY_NONE) {
                final String[] split = macro.getMacro().split(";");

                for (String s : split) {
                    Minecraft.getMinecraft().player.sendChatMessage(s);
                }
            }
        }
    }

}
