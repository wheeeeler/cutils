package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.wheel.cutils.api.event.render.EventDrawToast;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public class GuiToastPatch extends ClassPatch {

    public GuiToastPatch() {
        super("net.minecraft.client.gui.toasts.GuiToast", "bkc");
    }

    public static boolean drawToastHook() {
        final EventDrawToast event = new EventDrawToast();
        crack.INSTANCE.getEventManager().dispatchEvent(event);
        return event.isCanceled();
    }

    @MethodPatch(mcpName = "drawToast", notchName = "a", mcpDesc = "(Lnet/minecraft/client/gui/ScaledResolution;)V", notchDesc = "(Lbit;)V")
    public void drawToast(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(
                new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "drawToastHook", "()Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }
}
