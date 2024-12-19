package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.IRETURN;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.impl.management.PatchManager;

public final class KeyBindingPatch extends ClassPatch {

    public KeyBindingPatch() {
        super("net.minecraft.client.settings.KeyBinding", "bhy");
    }

    @MethodPatch(mcpName = "isKeyDown", notchName = "e", mcpDesc = "()Z")
    public void isKeyDown(MethodNode methodNode, PatchManager.Environment env) {
        AbstractInsnNode target = null;

        for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
            if (insn.getOpcode() == ALOAD) {
                target = insn;
                break;
            }
        }

        if (target != null) {
            AbstractInsnNode next = target.getNext().getNext();
            while (next.getOpcode() != IRETURN) {
                next = next.getNext();
                methodNode.instructions.remove(next.getPrevious());
            }
        }
    }

}
