package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.minecraft.util.EnumHand;
import net.minecraftforge.client.ForgeHooksClient;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.player.*;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.api.util.ASMUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

import hand.interactor.voodoo.EventManager;

public final class EntityPlayerSPPatch extends ClassPatch {

    public EntityPlayerSPPatch() {
        super("net.minecraft.client.entity.EntityPlayerSP", "bud");
    }

    public static boolean onUpdateHook(EventStageable.EventStage stage) {

        final EventPlayerUpdate event = new EventPlayerUpdate(stage);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        if (stage == EventStageable.EventStage.PRE) {

            crack.INSTANCE.getCameraManager().update();
        }

        return event.isCanceled();
    }

    public static boolean onUpdateWalkingPlayerHook(EventStageable.EventStage stage) {
        if (stage == EventStageable.EventStage.PRE) {
            crack.INSTANCE.getRotationManager().updateRotations();
            crack.INSTANCE.getPositionManager().updatePosition();
        }

        final EventUpdateWalkingPlayer event = new EventUpdateWalkingPlayer(stage);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        if (stage == EventStageable.EventStage.POST) {
            crack.INSTANCE.getRotationManager().restoreRotations();
            crack.INSTANCE.getPositionManager().restorePosition();
        }

        return event.isCanceled();
    }

    public static boolean sendChatMessageHook(String message) {

        final EventSendChatMessage event = new EventSendChatMessage(message);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static boolean swingArmHook(EnumHand hand) {

        final EventSwingArm event = new EventSwingArm(hand);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static boolean closeScreenHook() {

        final EventCloseScreen event = new EventCloseScreen();
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static boolean pushOutOfBlocksHook() {

        final EventPushOutOfBlocks event = new EventPushOutOfBlocks();
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static void onLivingUpdateHook() {

        final EventUpdateInput event = new EventUpdateInput();
        crack.INSTANCE.getEventManager().dispatchEvent(event);
    }

    public static boolean isHandActiveHook() {

        final EventHandActive event = new EventHandActive();
        crack.INSTANCE.getEventManager().dispatchEvent(event);
        return event.isCanceled();
    }

    @MethodPatch(mcpName = "onUpdate", notchName = "B_", mcpDesc = "()V")
    public void onUpdate(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList preInsn = new InsnList();

        preInsn.add(new FieldInsnNode(GETSTATIC, "net/wheel/cutils/api/event/EventStageable$EventStage", "PRE",
                "Lnet/wheel/cutils/api/event/EventStageable$EventStage;"));

        preInsn.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "onUpdateHook",
                "(Lnet/wheel/cutils/api/event/EventStageable$EventStage;)Z", false));

        final LabelNode jmp = new LabelNode();

        preInsn.add(new JumpInsnNode(IFEQ, jmp));

        preInsn.add(new InsnNode(RETURN));

        preInsn.add(jmp);

        methodNode.instructions.insert(preInsn);

        final InsnList postInsn = new InsnList();

        postInsn.add(new FieldInsnNode(GETSTATIC, "net/wheel/cutils/api/event/EventStageable$EventStage", "POST",
                "Lnet/wheel/cutils/api/event/EventStageable$EventStage;"));

        postInsn.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "onUpdateHook",
                "(Lnet/wheel/cutils/api/event/EventStageable$EventStage;)Z", false));

        methodNode.instructions.insertBefore(ASMUtil.bottom(methodNode), postInsn);
    }

    @MethodPatch(mcpName = "onUpdateWalkingPlayer", notchName = "N", mcpDesc = "()V")
    public void onUpdateWalkingPlayer(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList preInsn = new InsnList();

        preInsn.add(new FieldInsnNode(GETSTATIC, "net/wheel/cutils/api/event/EventStageable$EventStage", "PRE",
                "Lnet/wheel/cutils/api/event/EventStageable$EventStage;"));

        preInsn.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "onUpdateWalkingPlayerHook",
                "(Lnet/wheel/cutils/api/event/EventStageable$EventStage;)Z", false));

        final LabelNode jmp = new LabelNode();

        preInsn.add(new JumpInsnNode(IFEQ, jmp));

        preInsn.add(new InsnNode(RETURN));

        preInsn.add(jmp);

        methodNode.instructions.insert(preInsn);

        final InsnList postInsn = new InsnList();

        postInsn.add(new FieldInsnNode(GETSTATIC, "net/wheel/cutils/api/event/EventStageable$EventStage", "POST",
                "Lnet/wheel/cutils/api/event/EventStageable$EventStage;"));

        postInsn.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()),
                "onUpdateWalkingPlayerHook", "(Lnet/wheel/cutils/api/event/EventStageable$EventStage;)Z", false));

        methodNode.instructions.insertBefore(ASMUtil.bottom(methodNode), postInsn);
    }

    @MethodPatch(mcpName = "sendChatMessage", notchName = "g", mcpDesc = "(Ljava/lang/String;)V")
    public void sendChatMessage(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new VarInsnNode(ALOAD, 1));

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "sendChatMessageHook",
                "(Ljava/lang/String;)Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "swingArm", notchName = "a", mcpDesc = "(Lnet/minecraft/util/EnumHand;)V", notchDesc = "(Lub;)V")
    public void swingArm(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new VarInsnNode(ALOAD, 1));

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "swingArmHook",
                env == PatchManager.Environment.IDE ? "(Lnet/minecraft/util/EnumHand;)Z" : "(Lub;)Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "closeScreen", notchName = "p", mcpDesc = "()V")
    public void closeScreen(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "closeScreenHook", "()Z",
                false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "pushOutOfBlocks", notchName = "i", mcpDesc = "(DDD)Z")
    public void pushOutOfBlocks(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "pushOutOfBlocksHook",
                "()Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(ICONST_0));

        insnList.add(new InsnNode(IRETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "onLivingUpdate", notchName = "n", mcpDesc = "()V")
    public void onLivingUpdate(MethodNode methodNode, PatchManager.Environment env) {
        final AbstractInsnNode target = ASMUtil.findMethodInsn(methodNode, INVOKESTATIC,
                Type.getInternalName(ForgeHooksClient.class), "onInputUpdate",
                env == PatchManager.Environment.IDE
                        ? "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/MovementInput;)V"
                        : "(Laed;Lbub;)V");

        if (target != null) {
            methodNode.instructions.insert(target, new MethodInsnNode(INVOKESTATIC,
                    Type.getInternalName(this.getClass()), "onLivingUpdateHook", "()V", false));
        }
    }

    @MethodPatch(mcpName = "move", notchName = "a", mcpDesc = "(Lnet/minecraft/entity/MoverType;DDD)V", notchDesc = "(Lvv;DDD)V")
    public void move(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList insnList = new InsnList();
        insnList.add(new TypeInsnNode(NEW, Type.getInternalName(EventMove.class)));
        insnList.add(new InsnNode(DUP));
        insnList.add(new VarInsnNode(ALOAD, 1));
        insnList.add(new VarInsnNode(DLOAD, 2));
        insnList.add(new VarInsnNode(DLOAD, 4));
        insnList.add(new VarInsnNode(DLOAD, 6));
        insnList.add(new MethodInsnNode(INVOKESPECIAL, Type.getInternalName(EventMove.class), "<init>",
                env == PatchManager.Environment.IDE ? "(Lnet/minecraft/entity/MoverType;DDD)V" : "(Lvv;DDD)V", false));
        insnList.add(new VarInsnNode(ASTORE, 11));

        insnList.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(crack.class), "INSTANCE",
                "Lnet/wheel/cutils/crack;"));
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(crack.class), "getEventManager",
                "()Lhand/interactor/voodoo/EventManager;", false));
        insnList.add(new VarInsnNode(ALOAD, 11));
        insnList.add(new MethodInsnNode(INVOKEINTERFACE, Type.getInternalName(EventManager.class), "dispatchEvent",
                "(Ljava/lang/Object;)Ljava/lang/Object;", true));
        insnList.add(new InsnNode(POP));

        insnList.add(new VarInsnNode(ALOAD, 11));
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(EventMove.class), "getX", "()D", false));
        insnList.add(new VarInsnNode(DSTORE, 2));

        insnList.add(new VarInsnNode(ALOAD, 11));
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(EventMove.class), "getY", "()D", false));
        insnList.add(new VarInsnNode(DSTORE, 4));

        insnList.add(new VarInsnNode(ALOAD, 11));
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(EventMove.class), "getZ", "()D", false));
        insnList.add(new VarInsnNode(DSTORE, 6));

        insnList.add(new VarInsnNode(ALOAD, 11));
        insnList.add(
                new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(EventMove.class), "isCanceled", "()Z", false));
        final LabelNode jmp = new LabelNode();
        insnList.add(new JumpInsnNode(IFEQ, jmp));
        insnList.add(new InsnNode(RETURN));
        insnList.add(jmp);
        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "isHandActive", notchName = "cG", mcpDesc = "()Z")
    public void isHandActive(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "isHandActiveHook", "()Z",
                false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(ICONST_0));

        insnList.add(new InsnNode(IRETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }
}
