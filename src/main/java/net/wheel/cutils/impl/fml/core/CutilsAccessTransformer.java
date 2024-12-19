package net.wheel.cutils.impl.fml.core;

import java.io.IOException;

import net.minecraftforge.fml.common.asm.transformers.AccessTransformer;

public final class CutilsAccessTransformer extends AccessTransformer {

    public CutilsAccessTransformer() throws IOException {
        super("cutils_at.cfg");
    }

}
