package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.wheel.cutils.api.event.entity.EventPigTravel;
import net.wheel.cutils.api.event.entity.EventSteerEntity;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.api.util.ASMUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public final class EntityPigPatch extends ClassPatch {

    public EntityPigPatch() {
        super("net.minecraft.entity.passive.EntityPig", "aad");
    }

    public static boolean travelHook() {
        final EventPigTravel event = new EventPigTravel();
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static boolean canBeSteeredHook() {

        final EventSteerEntity event = new EventSteerEntity();
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

    @MethodPatch(mcpName = "travel", notchName = "a", mcpDesc = "(FFF)V")
    public void travel(MethodNode methodNode, PatchManager.Environment env) {
        final AbstractInsnNode target = ASMUtil.findMethodInsn(methodNode, INVOKEVIRTUAL,
                env == PatchManager.Environment.IDE ? "net/minecraft/entity/passive/EntityPig" : "aad",
                env == PatchManager.Environment.IDE ? "setAIMoveSpeed" : "k", "(F)V");
        if (target != null) {

            final InsnList insnList = new InsnList();

            insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "travelHook", "()Z",
                    false));

            final LabelNode jmp = new LabelNode();

            insnList.add(new JumpInsnNode(IFEQ, jmp));

            insnList.add(new InsnNode(RETURN));

            insnList.add(jmp);

            methodNode.instructions.insert(target, insnList);
        }
    }

}
