package net.wheel.cutils.impl.module.hidden;

import org.lwjgl.input.Keyboard;

import net.wheel.cutils.api.event.minecraft.EventKeyPress;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class KeybindsModule extends Module {

    public final Value<Boolean> f3Toggle = new Value<Boolean>("F3Toggle", new String[] { "f3" },
            "If enabled, you will not toggle modules if you also press F3", true);

    public KeybindsModule() {
        super("Keybinds", new String[] { "Binds" }, "Allows you to bind modules to keys", "NONE", -1,
                ModuleType.HIDDEN);
        this.setHidden(true);
        this.toggle();
    }

    @Listener
    public void keyPress(EventKeyPress event) {
        if (f3Toggle.getValue() && Keyboard.isKeyDown(Keyboard.KEY_F3))
            return;
        for (Module mod : crack.INSTANCE.getModuleManager().getModuleList()) {
            if (mod != null) {
                if (mod.getType() != ModuleType.HIDDEN && event.getKey() == Keyboard.getKeyIndex(mod.getKey())
                        && Keyboard.getKeyIndex(mod.getKey()) != Keyboard.KEY_NONE) {
                    mod.toggle();
                }
            }
        }
    }

}
