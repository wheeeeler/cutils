package net.wheel.cutils.impl.module.CRACK;

import net.wheel.cutils.api.event.minecraft.EventPlaySound;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class MuteModule extends Module {

    public MuteModule() {
        super("MuteModule", new String[] { "Mute" }, "Mute modid", "NONE", -1, ModuleType.CRACK);
    }

    public boolean isModIdMuted(String modId) {
        return crack.INSTANCE.getMuteManager().getMutedModIds().contains(modId.toLowerCase());
    }

    @Listener
    public void onPlaySound(EventPlaySound event) {
        if (event.getSound() != null) {
            String modId = event.getSound().getSoundLocation().getNamespace();
            if (isModIdMuted(modId)) {
                event.setResultSound(null);
            }
        }
    }
}
