package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.wheel.cutils.api.event.world.EventSetOpaqueCube;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public final class VisGraphPatch extends ClassPatch {

    public VisGraphPatch() {
        super("net.minecraft.client.renderer.chunk.VisGraph", "bxu");
    }

    public static boolean setOpaqueCubeHook() {
        final EventSetOpaqueCube event = new EventSetOpaqueCube();
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    @MethodPatch(mcpName = "setOpaqueCube", notchName = "a", mcpDesc = "(Lnet/minecraft/util/math/BlockPos;)V", notchDesc = "(Let;)V")
    public void setOpaqueCube(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "setOpaqueCubeHook", "()Z",
                false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

}
