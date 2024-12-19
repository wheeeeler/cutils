package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.wheel.cutils.api.event.render.EventRenderBlockModel;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

import hand.interactor.voodoo.EventManager;

public final class BlockModelRendererPatch extends ClassPatch {

    public BlockModelRendererPatch() {
        super("net.minecraft.client.renderer.BlockModelRenderer", "bvo");
    }

    @MethodPatch(mcpName = "renderModel", notchName = "a", mcpDesc = "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z", notchDesc = "(Lamy;Lcfy;Lawt;Let;Lbuk;ZJ)Z")
    public void renderModel(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new TypeInsnNode(NEW, Type.getInternalName(EventRenderBlockModel.class)));
        insnList.add(new InsnNode(DUP));

        insnList.add(new VarInsnNode(ALOAD, 1));

        insnList.add(new VarInsnNode(ALOAD, 2));

        insnList.add(new VarInsnNode(ALOAD, 3));

        insnList.add(new VarInsnNode(ALOAD, 4));

        insnList.add(new VarInsnNode(ALOAD, 5));

        insnList.add(new VarInsnNode(ILOAD, 6));

        insnList.add(new VarInsnNode(LLOAD, 7));

        insnList.add(new MethodInsnNode(INVOKESPECIAL, Type.getInternalName(EventRenderBlockModel.class), "<init>",
                env == PatchManager.Environment.IDE
                        ? "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZJ)V"
                        : "(Lamy;Lcfy;Lawt;Let;Lbuk;ZJ)V",
                false));

        insnList.add(new VarInsnNode(ASTORE, 13));

        insnList.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(crack.class), "INSTANCE",
                "Lnet/wheel/cutils/crack;"));

        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(crack.class), "getEventManager",
                "()Lhand/interactor/voodoo/EventManager;", false));

        insnList.add(new VarInsnNode(ALOAD, 13));

        insnList.add(new MethodInsnNode(INVOKEINTERFACE, Type.getInternalName(EventManager.class), "dispatchEvent",
                "(Ljava/lang/Object;)Ljava/lang/Object;", true));

        insnList.add(new InsnNode(POP));

        insnList.add(new VarInsnNode(ALOAD, 13));

        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(EventRenderBlockModel.class), "isCanceled",
                "()Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new VarInsnNode(ALOAD, 13));

        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(EventRenderBlockModel.class),
                "isRenderable", "()Z", false));

        insnList.add(new InsnNode(IRETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

}