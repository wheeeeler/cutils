package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.impl.management.PatchManager;

public final class ActiveRenderInfoPatch extends ClassPatch {

    public ActiveRenderInfoPatch() {
        super("net.minecraft.client.renderer.ActiveRenderInfo", "bhv");
    }

    public static void updateRenderInfoHook() {

        RenderUtil.updateModelViewProjectionMatrix();
    }

    @MethodPatch(mcpName = "updateRenderInfo", notchName = "updateRenderInfo", mcpDesc = "(Lnet/minecraft/entity/Entity;Z)V", notchDesc = "(Lvg;Z)V")
    public void updateRenderInfo(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "updateRenderInfoHook",
                "()V", false));

        methodNode.instructions.insert(insnList);
    }

}
