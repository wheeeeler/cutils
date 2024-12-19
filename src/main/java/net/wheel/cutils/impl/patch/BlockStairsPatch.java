package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import net.wheel.cutils.api.event.world.EventAddCollisionBox;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public final class BlockStairsPatch extends ClassPatch {

    public BlockStairsPatch() {
        super("net.minecraft.block.BlockStairs", "aud");
    }

    public static boolean addCollisionBoxToListHook(BlockPos pos, Entity entity) {

        final EventAddCollisionBox event = new EventAddCollisionBox(pos, entity);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    @MethodPatch(mcpName = "" +
            "addCollisionBoxToList", notchName = "a", mcpDesc = "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;Z)V", notchDesc = "(Lawt;Lamu;Let;Lbhb;Ljava/util/List;Lvg;Z)V")
    public void addCollisionBoxToList(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new VarInsnNode(ALOAD, 3));

        insnList.add(new VarInsnNode(ALOAD, 6));

        insnList.add(
                new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "addCollisionBoxToListHook",
                        env == PatchManager.Environment.IDE
                                ? "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)Z"
                                : "(Let;Lvg;)Z",
                        false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

}
