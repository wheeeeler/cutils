package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.Iterator;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.minecraft.entity.EntityLivingBase;

import net.wheel.cutils.api.event.render.EventRenderName;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.api.util.ASMUtil;
import net.wheel.cutils.api.util.shader.ShaderProgram;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public final class RenderLivingBasePatch extends ClassPatch {

    public RenderLivingBasePatch() {
        super("net.minecraft.client.renderer.entity.RenderLivingBase", "caa");
    }

    public static boolean renderNameHook(EntityLivingBase entity) {

        final EventRenderName event = new EventRenderName(entity);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static void setBrightnessHook(Buffer buf) {
        FloatBuffer brightness = (FloatBuffer) buf;
        for (Iterator<ShaderProgram> it = ShaderProgram.getProgramsInUse(); it.hasNext();) {
            it.next().setEntityBrightnessUniform(brightness.get(), brightness.get(), brightness.get(),
                    brightness.get());
            brightness.position(0);
        }
    }

    public static void unsetBrightnessHook() {
        for (Iterator<ShaderProgram> it = ShaderProgram.getProgramsInUse(); it.hasNext();) {
            it.next().setEntityBrightnessUniform(0.0f, 0.0f, 0.0f, 0.0f);
        }
    }

    @MethodPatch(mcpName = "renderName", notchName = "a", mcpDesc = "(Lnet/minecraft/entity/EntityLivingBase;DDD)V", notchDesc = "(Lvp;DDD)V")
    public void renderName(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new VarInsnNode(ALOAD, 1));

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "renderNameHook",
                env == PatchManager.Environment.IDE ? "(Lnet/minecraft/entity/EntityLivingBase;)Z" : "(Lvp;)Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "setBrightness", notchName = "a", mcpDesc = "(Lnet/minecraft/entity/EntityLivingBase;FZ)Z", notchDesc = "(Lvp;FZ)Z")
    public void setBrightness(MethodNode methodNode, PatchManager.Environment env) {

        final AbstractInsnNode target = ASMUtil.findMethodInsn(methodNode, INVOKEVIRTUAL, "java/nio/FloatBuffer",
                "flip", "()Ljava/nio/Buffer;");
        if (target != null) {

            final InsnList insnList = new InsnList();

            insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "setBrightnessHook",
                    "(Ljava/nio/Buffer;)V", false));

            insnList.add(new InsnNode(ICONST_0));

            methodNode.instructions.insert(target, insnList);
        }
    }

    @MethodPatch(mcpName = "unsetBrightness", notchName = "g", mcpDesc = "()V", notchDesc = "()V")
    public void unsetBrightness(MethodNode methodNode, PatchManager.Environment env) {

        methodNode.instructions.insert(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()),
                "unsetBrightnessHook", "()V", false));
    }

}
