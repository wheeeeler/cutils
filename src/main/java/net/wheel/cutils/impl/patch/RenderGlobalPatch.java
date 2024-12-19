package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.wheel.cutils.api.event.render.EventRenderBlockDamage;
import net.wheel.cutils.api.event.render.EventRenderEntityOutlines;
import net.wheel.cutils.api.event.render.EventRenderSky;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public final class RenderGlobalPatch extends ClassPatch {

    public RenderGlobalPatch() {
        super("net.minecraft.client.renderer.RenderGlobal", "buy");
    }

    public static boolean isRenderEntityOutlinesHook() {
        final EventRenderEntityOutlines event = new EventRenderEntityOutlines();
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static boolean renderSkyHook() {
        final EventRenderSky event = new EventRenderSky();
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static boolean drawBlockDamageTextureHook() {
        final EventRenderBlockDamage event = new EventRenderBlockDamage();
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    @MethodPatch(mcpName = "isRenderEntityOutlines", notchName = "d", mcpDesc = "()Z")
    public void isRenderEntityOutlines(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList insnList = new InsnList();
        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()),
                "isRenderEntityOutlinesHook", "()Z", false));
        final LabelNode jmp = new LabelNode();
        insnList.add(new JumpInsnNode(IFEQ, jmp));
        insnList.add(new InsnNode(ICONST_0));
        insnList.add(new InsnNode(IRETURN));
        insnList.add(jmp);
        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "renderSky", notchName = "a", mcpDesc = "(FI)V", notchDesc = "(FI)V")
    public void renderSky(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList insnList = new InsnList();
        insnList.add(
                new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "renderSkyHook", "()Z", false));
        final LabelNode jmp = new LabelNode();
        insnList.add(new JumpInsnNode(IFEQ, jmp));
        insnList.add(new InsnNode(RETURN));
        insnList.add(jmp);
        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "drawBlockDamageTexture", notchName = "a", mcpDesc = "(Lnet/minecraft/client/renderer/Tessellator;Lnet/minecraft/client/renderer/BufferBuilder;Lnet/minecraft/entity/Entity;F)V", notchDesc = "(Lbve;Lbuk;Lvg;F)V")
    public void drawBlockDamageTexture(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList insnList = new InsnList();
        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()),
                "drawBlockDamageTextureHook", "()Z", false));
        final LabelNode jmp = new LabelNode();
        insnList.add(new JumpInsnNode(IFEQ, jmp));
        insnList.add(new InsnNode(RETURN));
        insnList.add(jmp);
        methodNode.instructions.insert(insnList);
    }

}
