package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.wheel.cutils.api.event.gui.EventRenderHelmet;
import net.wheel.cutils.api.event.gui.EventRenderPortal;
import net.wheel.cutils.api.event.gui.EventRenderPotions;
import net.wheel.cutils.api.event.render.EventRenderCrosshairs;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public final class GuiIngameForgePatch extends ClassPatch {

    public GuiIngameForgePatch() {
        super("net.minecraftforge.client.GuiIngameForge");
    }

    public static boolean renderPortalHook() {

        final EventRenderPortal event = new EventRenderPortal();
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static boolean renderPotionIconsHook() {

        final EventRenderPotions event = new EventRenderPotions();
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static boolean renderHelmetHook() {

        final EventRenderHelmet event = new EventRenderHelmet();
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static boolean renderCrosshairsHook() {

        final EventRenderCrosshairs event = new EventRenderCrosshairs();
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    @MethodPatch(mcpName = "renderPortal", mcpDesc = "(Lnet/minecraft/client/gui/ScaledResolution;F)V", notchDesc = "(Lbit;F)V")
    public void renderPortal(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "renderPortalHook", "()Z",
                false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "renderPotionIcons", mcpDesc = "(Lnet/minecraft/client/gui/ScaledResolution;)V", notchDesc = "(Lbit;)V")
    public void renderPotionIcons(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "renderPotionIconsHook",
                "()Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "renderHelmet", mcpDesc = "(Lnet/minecraft/client/gui/ScaledResolution;F)V", notchDesc = "(Lbit;F)V")
    public void renderHelmet(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "renderHelmetHook", "()Z",
                false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "renderCrosshairs", mcpDesc = "(F)V", notchDesc = "(F)V")
    public void renderCrosshairs(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "renderCrosshairsHook",
                "()Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }
}
