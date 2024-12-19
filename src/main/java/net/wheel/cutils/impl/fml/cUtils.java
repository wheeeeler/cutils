package net.wheel.cutils.impl.fml;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import net.wheel.cutils.crack;

@Mod(modid = "cutils", name = "crackutils", version = cUtils.VERSION)
public final class cUtils {

    public static final String VERSION = "3.4p";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        crack.INSTANCE.init();
    }
}
