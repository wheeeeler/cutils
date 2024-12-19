package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.wheel.cutils.api.event.world.EventLandOnSlime;
import net.wheel.cutils.api.event.world.EventWalkOnSlime;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public final class BlockSlimePatch extends ClassPatch {

    public BlockSlimePatch() {
        super("net.minecraft.block.BlockSlime", "atu");
    }

    public static boolean onEntityWalkHook() {
        final EventWalkOnSlime event = new EventWalkOnSlime();
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static boolean onLanded() {
        final EventLandOnSlime event = new EventLandOnSlime();
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    @MethodPatch(mcpName = "onEntityWalk", notchName = "a", mcpDesc = "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)V", notchDesc = "(Lamu;Let;Lvg;)V")
    public void onEntityWalk(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "onEntityWalkHook", "()Z",
                false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "onLanded", notchName = "a", mcpDesc = "(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;)V", notchDesc = "(Lamu;Lvg;)V")
    public void onLanded(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "onLanded", "()Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

}
