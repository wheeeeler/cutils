package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.minecraft.entity.Entity;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.render.EventRenderEntity;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.api.util.ASMUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public final class RenderManagerPatch extends ClassPatch {

    public RenderManagerPatch() {
        super("net.minecraft.client.renderer.entity.RenderManager", "bzf");
    }

    public static boolean renderEntityHook(Entity entity, double x, double y, double z, float yaw, float partialTicks,
            EventStageable.EventStage stage) {

        final EventRenderEntity event = new EventRenderEntity(stage, entity, x, y, z, yaw, partialTicks);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    @MethodPatch(mcpName = "renderEntity", notchName = "a", mcpDesc = "(Lnet/minecraft/entity/Entity;DDDFFZ)V", notchDesc = "(Lvg;DDDFFZ)V")
    public void renderEntity(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList preInsn = new InsnList();

        preInsn.add(new VarInsnNode(ALOAD, 1));

        preInsn.add(new VarInsnNode(DLOAD, 2));

        preInsn.add(new VarInsnNode(DLOAD, 4));

        preInsn.add(new VarInsnNode(DLOAD, 6));

        preInsn.add(new VarInsnNode(FLOAD, 8));

        preInsn.add(new VarInsnNode(FLOAD, 9));

        preInsn.add(new FieldInsnNode(GETSTATIC, "net/wheel/cutils/api/event/EventStageable$EventStage", "PRE",
                "Lnet/wheel/cutils/api/event/EventStageable$EventStage;"));

        preInsn.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "renderEntityHook",
                env == PatchManager.Environment.IDE
                        ? "(Lnet/minecraft/entity/Entity;DDDFFLnet/wheel/cutils/api/event/EventStageable$EventStage;)Z"
                        : "(Lvg;DDDFFLnet/wheel/cutils/api/event/EventStageable$EventStage;)Z",
                false));

        final LabelNode jmp = new LabelNode();

        preInsn.add(new JumpInsnNode(IFEQ, jmp));

        preInsn.add(new InsnNode(RETURN));

        preInsn.add(jmp);

        methodNode.instructions.insert(preInsn);

        final InsnList postInsn = new InsnList();

        postInsn.add(new VarInsnNode(ALOAD, 1));

        postInsn.add(new VarInsnNode(DLOAD, 2));

        postInsn.add(new VarInsnNode(DLOAD, 4));

        postInsn.add(new VarInsnNode(DLOAD, 6));

        postInsn.add(new VarInsnNode(FLOAD, 8));

        postInsn.add(new VarInsnNode(FLOAD, 9));

        postInsn.add(new FieldInsnNode(GETSTATIC, "net/wheel/cutils/api/event/EventStageable$EventStage", "POST",
                "Lnet/wheel/cutils/api/event/EventStageable$EventStage;"));

        postInsn.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "renderEntityHook",
                env == PatchManager.Environment.IDE
                        ? "(Lnet/minecraft/entity/Entity;DDDFFLnet/wheel/cutils/api/event/EventStageable$EventStage;)Z"
                        : "(Lvg;DDDFFLnet/wheel/cutils/api/event/EventStageable$EventStage;)Z",
                false));

        methodNode.instructions.insertBefore(ASMUtil.bottom(methodNode), postInsn);
    }

}
