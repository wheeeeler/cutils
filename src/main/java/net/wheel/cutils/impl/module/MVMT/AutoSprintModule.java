package net.wheel.cutils.impl.module.MVMT;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.player.EventUpdateWalkingPlayer;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AutoSprintModule extends Module {

    public final Value<Mode> mode = new Value<Mode>("Mode", new String[] { "Mode", "M" }, "The sprint mode to use",
            Mode.RAGE);
    public final Value<Boolean> keyState = new Value<Boolean>("KeyState",
            new String[] { "Key", "KS", "K", "State", "Keybind" }, "Sets key state for sprinting", false);
    public final Value<Boolean> checkScreen = new Value<Boolean>("CheckScreen",
            new String[] { "Check", "Screen", "CS", "C" }, "Stop sprinting if a GUI is open (breaks GUIMove)", false);

    public AutoSprintModule() {
        super("AutoSprint", new String[] { "AutoSprint", "Spr" }, "Automatically sprints for you", "NONE", -1,
                ModuleType.MVMT);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (Minecraft.getMinecraft().world != null) {
            Minecraft.getMinecraft().player.setSprinting(false);
            if (this.keyState.getValue()) {
                Minecraft.getMinecraft().gameSettings.keyBindSprint.pressed = Keyboard
                        .isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode());
            }
        }
    }

    @Override
    public String getMetaData() {
        return this.mode.getValue().name();
    }

    @Listener
    public void onWalkingUpdate(EventUpdateWalkingPlayer event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            final Minecraft mc = Minecraft.getMinecraft();

            if (mc.player.getFoodStats().getFoodLevel() <= 6f)
                return;

            if (this.checkScreen.getValue())
                if (mc.currentScreen != null)
                    return;

            switch (this.mode.getValue()) {
                case RAGE:
                    if ((mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()
                            || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown())
                            && !(mc.player.isSneaking()) && !(mc.player.collidedHorizontally)) {
                        mc.player.setSprinting(true);
                    }
                    break;
                case LEGIT:
                    if ((mc.gameSettings.keyBindForward.isKeyDown()) && !(mc.player.isSneaking())
                            && !(mc.player.isHandActive()) && !(mc.player.collidedHorizontally)) {
                        mc.player.setSprinting(true);
                    }
                    break;
                case ALWAYS:
                    mc.player.setSprinting(true);
                    break;
            }

            if (this.keyState.getValue()) {
                mc.gameSettings.keyBindSprint.pressed = true;
            }
        }
    }

    private enum Mode {
        RAGE, LEGIT, ALWAYS
    }

}
