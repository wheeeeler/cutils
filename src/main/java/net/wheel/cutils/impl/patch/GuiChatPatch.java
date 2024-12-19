package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.wheel.cutils.api.event.player.EventChatKeyTyped;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public final class GuiChatPatch extends ClassPatch {

    public GuiChatPatch() {
        super("net.minecraft.client.gui.GuiChat", "bkn");
    }

    public static boolean keyTypedHook(char typedChar, int keyCode) {
        final EventChatKeyTyped event = new EventChatKeyTyped(typedChar, keyCode);
        crack.INSTANCE.getEventManager().dispatchEvent(event);
        return event.isCanceled();
    }

    @MethodPatch(mcpName = "keyTyped", notchName = "a", mcpDesc = "(CI)V")
    public void keyTyped(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(ILOAD, 1));
        insnList.add(new VarInsnNode(ILOAD, 2));
        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "keyTypedHook", "(CI)Z",
                false));
        final LabelNode jmp = new LabelNode();
        insnList.add(new JumpInsnNode(IFEQ, jmp));
        insnList.add(new InsnNode(RETURN));
        insnList.add(jmp);
        methodNode.instructions.insert(insnList);
    }
}
