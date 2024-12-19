package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.wheel.cutils.api.event.entity.EventHorseSaddled;
import net.wheel.cutils.api.event.entity.EventSteerEntity;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public final class AbstractHorsePatch extends ClassPatch {

    public AbstractHorsePatch() {
        super("net.minecraft.entity.passive.AbstractHorse", "aao");
    }

    public static boolean canBeSteeredHook() {

        final EventSteerEntity event = new EventSteerEntity();
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static boolean isHorseSaddledHook() {

        final EventHorseSaddled event = new EventHorseSaddled();
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    @MethodPatch(mcpName = "canBeSteered", notchName = "cV", mcpDesc = "()Z")
    public void canBeSteered(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "canBeSteeredHook", "()Z",
                false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(ICONST_1));

        insnList.add(new InsnNode(IRETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "isHorseSaddled", notchName = "dG", mcpDesc = "()Z")
    public void isHorseSaddled(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "isHorseSaddledHook",
                "()Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(ICONST_1));

        insnList.add(new InsnNode(IRETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

}
