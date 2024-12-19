package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.wheel.cutils.api.event.render.EventRenderOverlay;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public final class ItemRendererPatch extends ClassPatch {

    public ItemRendererPatch() {
        super("net.minecraft.client.renderer.ItemRenderer", "buu");
    }

    public static boolean renderSuffocationOverlayHook() {
        final EventRenderOverlay event = new EventRenderOverlay(EventRenderOverlay.OverlayType.BLOCK);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static boolean renderWaterOverlayTextureHook() {
        final EventRenderOverlay event = new EventRenderOverlay(EventRenderOverlay.OverlayType.LIQUID);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static boolean renderFireInFirstPersonHook() {
        final EventRenderOverlay event = new EventRenderOverlay(EventRenderOverlay.OverlayType.FIRE);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    @MethodPatch(mcpName = "renderSuffocationOverlay", notchName = "a", mcpDesc = "(Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V", notchDesc = "(Lcdq;)V")
    public void renderSuffocationOverlay(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()),
                "renderSuffocationOverlayHook", "()Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "renderWaterOverlayTexture", notchName = "e", mcpDesc = "(F)V")
    public void renderWaterOverlayTexture(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()),
                "renderWaterOverlayTextureHook", "()Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "renderFireInFirstPerson", notchName = "d", mcpDesc = "()V")
    public void renderFireInFirstPerson(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()),
                "renderFireInFirstPersonHook", "()Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }
}
