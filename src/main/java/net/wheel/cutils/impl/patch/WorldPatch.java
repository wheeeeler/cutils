package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.minecraft.entity.Entity;

import net.wheel.cutils.api.event.world.*;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public final class WorldPatch extends ClassPatch {

    public WorldPatch() {
        super("net.minecraft.world.World", "amu");
    }

    public static boolean checkLightForHook() {
        final EventLightUpdate event = new EventLightUpdate();
        crack.INSTANCE.getEventManager().dispatchEvent(event);
        return event.isCanceled();
    }

    public static boolean getRainStrengthHook() {
        final EventRainStrength event = new EventRainStrength();
        crack.INSTANCE.getEventManager().dispatchEvent(event);
        return event.isCanceled();
    }

    /*
     * @MethodPatch( mcpName = "checkLight", notchName = "w", mcpDesc = "(Lnet/minecraft/util/math/BlockPos;)Z",
     * notchDesc = "(Let;)Z") public void checkLight(MethodNode methodNode, PatchManager.Environment env) { final
     * InsnList list = new InsnList(); list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()),
     * "checkLightHook", "()Z", false)); final LabelNode jmp = new LabelNode(); list.add(new JumpInsnNode(IFEQ, jmp));
     * 
     * list.add(new InsnNode(ICONST_0)); list.add(new InsnNode(IRETURN)); list.add(jmp);
     * methodNode.instructions.insert(list); }
     * 
     * public static boolean checkLightHook() { final EventLightUpdate event = new EventLightUpdate();
     * crack.INSTANCE.getEventManager().dispatchEvent(event); return event.isCanceled(); }
     */

    public static boolean onEntityAddedHook(Entity entity) {
        final EventAddEntity eventAddEntity = new EventAddEntity(entity);
        crack.INSTANCE.getEventManager().dispatchEvent(eventAddEntity);
        return eventAddEntity.isCanceled();
    }

    public static void onEntityRemovedHook(Entity entity) {
        crack.INSTANCE.getEventManager().dispatchEvent(new EventRemoveEntity(entity));
    }

    public static boolean spawnParticleHook() {
        final EventSpawnParticle event = new EventSpawnParticle();
        crack.INSTANCE.getEventManager().dispatchEvent(event);
        return event.isCanceled();
    }

    @MethodPatch(mcpName = "checkLightFor", notchName = "c", mcpDesc = "(Lnet/minecraft/world/EnumSkyBlock;Lnet/minecraft/util/math/BlockPos;)Z", notchDesc = "(Lana;Let;)Z")
    public void checkLightFor(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList list = new InsnList();

        list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "checkLightForHook", "()Z",
                false));

        final LabelNode jmp = new LabelNode();

        list.add(new JumpInsnNode(IFEQ, jmp));

        list.add(new InsnNode(ICONST_0));

        list.add(new InsnNode(IRETURN));

        list.add(jmp);

        methodNode.instructions.insert(list);
    }

    @MethodPatch(mcpName = "getRainStrength", notchName = "j", mcpDesc = "(F)F", notchDesc = "(F)F")
    public void getRainStrength(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList list = new InsnList();
        list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "getRainStrengthHook", "()Z",
                false));
        final LabelNode jmp = new LabelNode();
        list.add(new JumpInsnNode(IFEQ, jmp));
        list.add(new InsnNode(FCONST_0));
        list.add(new InsnNode(FRETURN));
        list.add(jmp);
        methodNode.instructions.insert(list);
    }

    @MethodPatch(mcpName = "onEntityAdded", notchName = "b", mcpDesc = "(Lnet/minecraft/entity/Entity;)V", notchDesc = "(Lvg;)V")
    public void onEntityAdded(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList list = new InsnList();
        list.add(new VarInsnNode(ALOAD, 1));
        list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "onEntityAddedHook",
                env == PatchManager.Environment.IDE ? "(Lnet/minecraft/entity/Entity;)Z" : "(Lvg;)Z", false));
        final LabelNode jmp = new LabelNode();
        list.add(new JumpInsnNode(IFEQ, jmp));
        list.add(new InsnNode(RETURN));
        list.add(jmp);
        methodNode.instructions.insert(list);
    }

    @MethodPatch(mcpName = "onEntityRemoved", notchName = "c", mcpDesc = "(Lnet/minecraft/entity/Entity;)V", notchDesc = "(Lvg;)V")
    public void onEntityRemoved(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList list = new InsnList();
        list.add(new VarInsnNode(ALOAD, 1));
        list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "onEntityRemovedHook",
                env == PatchManager.Environment.IDE ? "(Lnet/minecraft/entity/Entity;)V" : "(Lvg;)V", false));
        methodNode.instructions.insert(list);
    }

    @MethodPatch(mcpName = "spawnParticle", notchName = "a", mcpDesc = "(IZDDDDDD[I)V", notchDesc = "(IZDDDDDD[I)V")
    public void spawnParticle(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList list = new InsnList();
        list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "spawnParticleHook", "()Z",
                false));
        final LabelNode jmp = new LabelNode();
        list.add(new JumpInsnNode(IFEQ, jmp));
        list.add(new InsnNode(RETURN));
        list.add(jmp);
        methodNode.instructions.insert(list);
    }
}
