package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.wheel.cutils.api.event.world.EventCanCollide;
import net.wheel.cutils.api.event.world.EventLiquidCollisionBB;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

import hand.interactor.voodoo.EventManager;

public final class BlockLiquidPatch extends ClassPatch {

    public BlockLiquidPatch() {
        super("net.minecraft.block.BlockLiquid", "aru");
    }

    public static boolean canCollideCheckHook() {
        final EventCanCollide event = new EventCanCollide();
        crack.INSTANCE.getEventManager().dispatchEvent(event);
        return event.isCanceled();
    }

    @MethodPatch(mcpName = "getCollisionBoundingBox", notchName = "a", mcpDesc = "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/AxisAlignedBB;", notchDesc = "(Lawt;Lamy;Let;)Lbhb;")
    public void getCollisionBoundingBox(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new TypeInsnNode(NEW, Type.getInternalName(EventLiquidCollisionBB.class)));
        insnList.add(new InsnNode(DUP));

        insnList.add(new VarInsnNode(ALOAD, 3));

        insnList.add(new MethodInsnNode(INVOKESPECIAL, Type.getInternalName(EventLiquidCollisionBB.class), "<init>",
                env == PatchManager.Environment.IDE ? "(Lnet/minecraft/util/math/BlockPos;)V" : "(Let;)V", false));

        insnList.add(new VarInsnNode(ASTORE, 4));

        insnList.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(crack.class), "INSTANCE",
                "Lnet/wheel/cutils/crack;"));
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(crack.class), "getEventManager",
                "()Lhand/interactor/voodoo/EventManager;", false));

        insnList.add(new VarInsnNode(ALOAD, 4));

        insnList.add(new MethodInsnNode(INVOKEINTERFACE, Type.getInternalName(EventManager.class), "dispatchEvent",
                "(Ljava/lang/Object;)Ljava/lang/Object;", true));

        insnList.add(new InsnNode(POP));

        insnList.add(new VarInsnNode(ALOAD, 4));

        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(EventLiquidCollisionBB.class), "isCanceled",
                "()Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new VarInsnNode(ALOAD, 4));

        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(EventLiquidCollisionBB.class),
                "getBoundingBox",
                env == PatchManager.Environment.IDE ? "()Lnet/minecraft/util/math/AxisAlignedBB;" : "()Lbhb;", false));

        insnList.add(new InsnNode(ARETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "canCollideCheck", notchName = "a", mcpDesc = "(Lnet/minecraft/block/state/IBlockState;Z)Z", notchDesc = "(Lawt;Z)Z")
    public void canCollideCheck(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList preInsn = new InsnList();
        preInsn.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "canCollideCheckHook",
                "()Z", false));
        final LabelNode jmp = new LabelNode();
        preInsn.add(new JumpInsnNode(IFEQ, jmp));
        preInsn.add(new InsnNode(ICONST_1));
        preInsn.add(new InsnNode(IRETURN));
        preInsn.add(jmp);
        methodNode.instructions.insert(preInsn);
    }

}
