package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.wheel.cutils.api.event.world.EventCollideSoulSand;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public final class BlockSoulSandPatch extends ClassPatch {

    public BlockSoulSandPatch() {
        super("net.minecraft.block.BlockSoulSand", "atx");
    }

    public static boolean onEntityCollidedWithBlockHook() {
        final EventCollideSoulSand event = new EventCollideSoulSand();
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    @MethodPatch(mcpName = "onEntityCollidedWithBlock", notchName = "a", mcpDesc = "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/entity/Entity;)V", notchDesc = "(Lamu;Let;Lawt;Lvg;)V")
    public void onEntityCollidedWithBlock(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()),
                "onEntityCollidedWithBlockHook", "()Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

}
