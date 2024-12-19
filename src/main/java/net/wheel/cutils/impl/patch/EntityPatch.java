package net.wheel.cutils.impl.patch;

import net.wheel.cutils.api.patch.ClassPatch;

public final class EntityPatch extends ClassPatch {

    public EntityPatch() {
        super("net.minecraft.entity.Entity", "vg");
    }

}
