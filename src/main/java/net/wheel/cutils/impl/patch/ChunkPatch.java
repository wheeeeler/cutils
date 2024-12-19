package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.minecraft.entity.Entity;

import net.wheel.cutils.api.event.world.EventAddEntity;
import net.wheel.cutils.api.event.world.EventChunk;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.api.util.ASMUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

import hand.interactor.voodoo.EventManager;

public final class ChunkPatch extends ClassPatch {

    public ChunkPatch() {
        super("net.minecraft.world.chunk.Chunk", "axw");
    }

    public static boolean onEntityAddedHook(Entity entity) {
        final EventAddEntity eventAddEntity = new EventAddEntity(entity);
        crack.INSTANCE.getEventManager().dispatchEvent(eventAddEntity);
        return eventAddEntity.isCanceled();
    }

    @MethodPatch(mcpName = "onUnload", notchName = "d", mcpDesc = "()V", notchDesc = "()V")
    public void onUnload(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList insnList = new InsnList();

        insnList.add(new TypeInsnNode(NEW, Type.getInternalName(EventChunk.class)));
        insnList.add(new InsnNode(DUP));
        insnList.add(new FieldInsnNode(GETSTATIC, "net/wheel/cutils/api/event/world/EventChunk$ChunkType", "UNLOAD",
                "Lnet/wheel/cutils/api/event/world/EventChunk$ChunkType;"));
        insnList.add(new VarInsnNode(ALOAD, 0));
        insnList.add(new MethodInsnNode(INVOKESPECIAL, Type.getInternalName(EventChunk.class), "<init>",
                env == PatchManager.Environment.IDE
                        ? "(Lnet/wheel/cutils/api/event/world/EventChunk$ChunkType;Lnet/minecraft/world/chunk/Chunk;)V"
                        : "(Lnet/wheel/cutils/api/event/world/EventChunk$ChunkType;Laxw;)V",
                false));
        insnList.add(new VarInsnNode(ASTORE, 7));
        insnList.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(crack.class), "INSTANCE",
                "Lnet/wheel/cutils/crack;"));
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(crack.class), "getEventManager",
                "()Lhand/interactor/voodoo/EventManager;", false));
        insnList.add(new VarInsnNode(ALOAD, 7));
        insnList.add(new MethodInsnNode(INVOKEINTERFACE, Type.getInternalName(EventManager.class), "dispatchEvent",
                "(Ljava/lang/Object;)Ljava/lang/Object;", true));
        insnList.add(new InsnNode(POP));

        methodNode.instructions.insertBefore(ASMUtil.bottom(methodNode), insnList);
    }

    @MethodPatch(mcpName = "addEntity", notchName = "a", mcpDesc = "(Lnet/minecraft/entity/Entity;)V", notchDesc = "(Lvg;)V")
    public void addEntity(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(ALOAD, 1));
        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "onEntityAddedHook",
                env == PatchManager.Environment.IDE ? "(Lnet/minecraft/entity/Entity;)Z" : "(Lvg;)Z", false));
        final LabelNode jmp = new LabelNode();
        insnList.add(new JumpInsnNode(IFEQ, jmp));
        insnList.add(new InsnNode(RETURN));
        insnList.add(jmp);
        methodNode.instructions.insert(insnList);
    }

}
