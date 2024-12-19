package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.wheel.cutils.api.event.player.EventApplyCollision;
import net.wheel.cutils.api.event.player.EventPushedByWater;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public final class EntityPlayerPatch extends ClassPatch {

    public EntityPlayerPatch() {
        super("net.minecraft.entity.player.EntityPlayer", "aed");
    }

    public static boolean isPushedByWaterHook() {

        final EventPushedByWater event = new EventPushedByWater();
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static boolean applyEntityCollisionHook() {

        final EventApplyCollision event = new EventApplyCollision();
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    @MethodPatch(mcpName = "isPushedByWater", notchName = "bo", mcpDesc = "()Z")
    public void isPushedByWater(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "isPushedByWaterHook",
                "()Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(ICONST_0));

        insnList.add(new InsnNode(IRETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "applyEntityCollision", notchName = "i", mcpDesc = "(Lnet/minecraft/entity/Entity;)V", notchDesc = "(Lvg;)V")
    public void applyEntityCollision(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "applyEntityCollisionHook",
                "()Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

}
