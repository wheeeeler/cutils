package net.wheel.cutils.impl.fml.core;

import java.util.Map;

import javax.annotation.Nullable;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import net.wheel.cutils.impl.management.PatchManager;

@IFMLLoadingPlugin.TransformerExclusions(value = "net.wheel.cutils.impl.fml.core")
@IFMLLoadingPlugin.MCVersion(value = "1.12.2")
public final class CutilsLoadingPlugin implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {
                CutilsClassTransformer.class.getName()
        };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        final boolean runtimeDeobfuscationEnabled = (boolean) data.getOrDefault("runtimeDeobfuscationEnabled", true);
        CutilsClassTransformer.PATCH_MANAGER = new PatchManager(!runtimeDeobfuscationEnabled);
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.cutils.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
    }

    @Override
    public String getAccessTransformerClass() {
        return CutilsAccessTransformer.class.getName();
    }
}
