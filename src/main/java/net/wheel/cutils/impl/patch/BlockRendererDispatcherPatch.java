package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import net.wheel.cutils.api.event.render.EventRenderBlock;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public final class BlockRendererDispatcherPatch extends ClassPatch {

    public BlockRendererDispatcherPatch() {
        super("net.minecraft.client.renderer.BlockRendererDispatcher", "bvm");
    }

    public static boolean renderBlockHook(IBlockState state, BlockPos pos, IBlockAccess access,
            BufferBuilder bufferBuilder) {
        final EventRenderBlock event = new EventRenderBlock(state, pos, access, bufferBuilder);
        crack.INSTANCE.getEventManager().dispatchEvent(event);
        return event.isCanceled();
    }

    @MethodPatch(mcpName = "renderBlock", notchName = "a", mcpDesc = "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/BufferBuilder;)Z", notchDesc = "(Lawt;Let;Lamy;Lbuk;)Z")
    public void renderBlock(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList insnList = new InsnList();

        insnList.add(new VarInsnNode(ALOAD, 1));
        insnList.add(new VarInsnNode(ALOAD, 2));
        insnList.add(new VarInsnNode(ALOAD, 3));
        insnList.add(new VarInsnNode(ALOAD, 4));

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "renderBlockHook",
                env == PatchManager.Environment.IDE
                        ? "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/BufferBuilder;)Z"
                        : "(Lawt;Let;Lamy;Lbuk;)Z",
                false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(ICONST_1));
        insnList.add(new InsnNode(IRETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }
}
