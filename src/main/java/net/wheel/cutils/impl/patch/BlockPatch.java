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

public final class BlockPatch extends ClassPatch {

    public BlockPatch() {
        super("net.minecraft.block.Block", "aow");
    }

    /*
     * @MethodPatch( mcpName = "shouldSideBeRendered", notchName = "a", mcpDesc =
     * "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z",
     * notchDesc = "(Lawt;Lamy;Let;Lfa;)Z") public void shouldSideBeRendered(MethodNode methodNode,
     * PatchManager.Environment env) {
     * 
     * final InsnList insnList = new InsnList();
     * 
     * insnList.add(new TypeInsnNode(NEW, Type.getInternalName(EventRenderBlockSide.class))); insnList.add(new
     * InsnNode(DUP));
     * 
     * insnList.add(new VarInsnNode(ALOAD, 0));
     * 
     * insnList.add(new VarInsnNode(ALOAD, 3));
     * 
     * insnList.add(new MethodInsnNode(INVOKESPECIAL, Type.getInternalName(EventRenderBlockSide.class), "<init>", env ==
     * PatchManager.Environment.IDE ? "(Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)V" :
     * "(Laow;Let;)V", false));
     * 
     * insnList.add(new VarInsnNode(ASTORE, 6));
     * 
     * insnList.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(crack.class), "INSTANCE",
     * "Lnet/wheel/cutils/crack;"));
     * 
     * insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(crack.class), "getEventManager",
     * "()Lhand/interactor/voodoo/EventManager;", false));
     * 
     * insnList.add(new VarInsnNode(ALOAD, 6));
     * 
     * insnList.add(new MethodInsnNode(INVOKEINTERFACE, Type.getInternalName(EventManager.class), "dispatchEvent",
     * "(Ljava/lang/Object;)Ljava/lang/Object;", true));
     * 
     * insnList.add(new InsnNode(POP));
     * 
     * insnList.add(new VarInsnNode(ALOAD, 6));
     * 
     * insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(EventRenderBlockSide.class), "isCanceled",
     * "()Z", false));
     * 
     * final LabelNode jmp = new LabelNode();
     * 
     * insnList.add(new JumpInsnNode(IFEQ, jmp));
     * 
     * insnList.add(new VarInsnNode(ALOAD, 6));
     * 
     * insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(EventRenderBlockSide.class), "isRenderable",
     * "()Z", false));
     * 
     * insnList.add(new InsnNode(IRETURN));
     * 
     * insnList.add(jmp);
     * 
     * methodNode.instructions.insert(insnList); }
     */

    /*
     * @MethodPatch( mcpName = "getBlockLayer", notchName = "f", mcpDesc = "Lnet/minecraft/util/BlockRenderLayer;",
     * notchDesc = "Lamm;") public void getBlockLayer(MethodNode methodNode, PatchManager.Environment env) {
     * 
     * final InsnList insnList = new InsnList();
     * 
     * insnList.add(new TypeInsnNode(NEW, Type.getInternalName(EventGetBlockLayer.class))); insnList.add(new
     * InsnNode(DUP));
     * 
     * insnList.add(new VarInsnNode(ALOAD, 0));
     * 
     * insnList.add(new MethodInsnNode(INVOKESPECIAL, Type.getInternalName(EventGetBlockLayer.class), "<init>", env ==
     * PatchManager.Environment.IDE ? "(Lnet/minecraft/block/Block;)V" : "(Laow;)V", false));
     * 
     * insnList.add(new VarInsnNode(ASTORE, 1));
     * 
     * insnList.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(crack.class), "INSTANCE",
     * "Lnet/wheel/cutils/crack;"));
     * 
     * insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(crack.class), "getEventManager",
     * "()Lhand/interactor/voodoo/EventManager;", false));
     * 
     * insnList.add(new VarInsnNode(ALOAD, 1));
     * 
     * insnList.add(new MethodInsnNode(INVOKEINTERFACE, Type.getInternalName(EventManager.class), "dispatchEvent",
     * "(Ljava/lang/Object;)Ljava/lang/Object;", true));
     * 
     * insnList.add(new InsnNode(POP));
     * 
     * insnList.add(new VarInsnNode(ALOAD, 1));
     * 
     * insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(EventGetBlockLayer.class), "isCanceled",
     * "()Z", false));
     * 
     * final LabelNode jmp = new LabelNode();
     * 
     * insnList.add(new JumpInsnNode(IFEQ, jmp));
     * 
     * insnList.add(new VarInsnNode(ALOAD, 1));
     * 
     * insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(EventGetBlockLayer.class), "getLayer", env ==
     * PatchManager.Environment.IDE ? "()Lnet/minecraft/util/BlockRenderLayer;" : "()Lamm;", false));
     * 
     * insnList.add(new InsnNode(ARETURN));
     * 
     * insnList.add(jmp);
     * 
     * methodNode.instructions.insert(insnList); }
     */

    public static boolean addCollisionBoxToListHook(BlockPos pos, Entity entity) {

        final EventAddCollisionBox event = new EventAddCollisionBox(pos, entity);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    @MethodPatch(mcpName = "addCollisionBoxToList", notchName = "a", mcpDesc = "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;Z)V", notchDesc = "(Lawt;Lamu;Let;Lbhb;Ljava/util/List;Lvg;Z)V")
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
