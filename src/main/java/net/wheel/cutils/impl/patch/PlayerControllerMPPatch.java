package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import net.wheel.cutils.api.event.player.*;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

import hand.interactor.voodoo.EventManager;

public final class PlayerControllerMPPatch extends ClassPatch {

    public PlayerControllerMPPatch() {
        super("net.minecraft.client.multiplayer.PlayerControllerMP", "bsa");
    }

    public static boolean onPlayerDestroyBlockHook(BlockPos pos) {

        final EventDestroyBlock event = new EventDestroyBlock(pos);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static boolean clickBlockHook(BlockPos pos, EnumFacing face) {

        final EventClickBlock event = new EventClickBlock(pos, face);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static boolean resetBlockRemovingHook() {

        final EventResetBlockRemoving event = new EventResetBlockRemoving();
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static boolean onPlayerDamageBlockHook(BlockPos pos, EnumFacing face) {

        final EventPlayerDamageBlock event = new EventPlayerDamageBlock(pos, face);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static boolean isHittingPositionHook(BlockPos pos) {
        final EventHittingPosition event = new EventHittingPosition(pos);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static boolean getIsHittingBlockHook() {

        final EventHittingBlock event = new EventHittingBlock();
        crack.INSTANCE.getEventManager().dispatchEvent(event);
        return event.isCanceled();
    }

    @MethodPatch(mcpName = "onPlayerDestroyBlock", notchName = "a", mcpDesc = "(Lnet/minecraft/util/math/BlockPos;)Z", notchDesc = "(Let;)Z")
    public void onPlayerDestroyBlock(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new VarInsnNode(ALOAD, 1));

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "onPlayerDestroyBlockHook",
                env == PatchManager.Environment.IDE ? "(Lnet/minecraft/util/math/BlockPos;)Z" : "(Let;)Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(ICONST_0));

        insnList.add(new InsnNode(IRETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "clickBlock", notchName = "a", mcpDesc = "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z", notchDesc = "(Let;Lfa;)Z")
    public void clickBlock(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new VarInsnNode(ALOAD, 1));

        insnList.add(new VarInsnNode(ALOAD, 2));

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "clickBlockHook",
                env == PatchManager.Environment.IDE
                        ? "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z"
                        : "(Let;Lfa;)Z",
                false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(ICONST_0));

        insnList.add(new InsnNode(IRETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "resetBlockRemoving", notchName = "c", mcpDesc = "()V")
    public void resetBlockRemoving(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();
        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "resetBlockRemovingHook",
                "()Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "onPlayerDamageBlock", notchName = "b", mcpDesc = "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z", notchDesc = "(Let;Lfa;)Z")
    public void onPlayerDamageBlock(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new VarInsnNode(ALOAD, 1));

        insnList.add(new VarInsnNode(ALOAD, 2));

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "onPlayerDamageBlockHook",
                env == PatchManager.Environment.IDE
                        ? "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z"
                        : "(Let;Lfa;)Z",
                false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(ICONST_0));

        insnList.add(new InsnNode(IRETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "processRightClickBlock", notchName = "a", mcpDesc = "(Lnet/minecraft/client/entity/EntityPlayerSP;Lnet/minecraft/client/multiplayer/WorldClient;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/EnumHand;)Lnet/minecraft/util/EnumActionResult;", notchDesc = "(Lbud;Lbsb;Let;Lfa;Lbhe;Lub;)Lud;")
    public void processRightClickBlock(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new TypeInsnNode(NEW, Type.getInternalName(EventRightClickBlock.class)));
        insnList.add(new InsnNode(DUP));

        insnList.add(new VarInsnNode(ALOAD, 3));

        insnList.add(new VarInsnNode(ALOAD, 4));

        insnList.add(new VarInsnNode(ALOAD, 5));

        insnList.add(new VarInsnNode(ALOAD, 6));

        insnList.add(new MethodInsnNode(INVOKESPECIAL, Type.getInternalName(EventRightClickBlock.class), "<init>",
                env == PatchManager.Environment.IDE
                        ? "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/EnumHand;)V"
                        : "(Let;Lfa;Lbhe;Lub;)V",
                false));

        insnList.add(new VarInsnNode(ASTORE, 19));

        insnList.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(crack.class), "INSTANCE",
                "Lnet/wheel/cutils/crack;"));

        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(crack.class), "getEventManager",
                "()Lhand/interactor/voodoo/EventManager;", false));

        insnList.add(new VarInsnNode(ALOAD, 19));

        insnList.add(new MethodInsnNode(INVOKEINTERFACE, Type.getInternalName(EventManager.class), "dispatchEvent",
                "(Ljava/lang/Object;)Ljava/lang/Object;", true));

        insnList.add(new InsnNode(POP));

        insnList.add(new VarInsnNode(ALOAD, 19));

        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(EventRightClickBlock.class), "isCanceled",
                "()Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new FieldInsnNode(GETSTATIC,
                env == PatchManager.Environment.IDE ? "net/minecraft/util/EnumActionResult" : "ud",
                env == PatchManager.Environment.IDE ? "FAIL" : "c",
                env == PatchManager.Environment.IDE ? "Lnet/minecraft/util/EnumActionResult;" : "Lud;"));

        insnList.add(new InsnNode(ARETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "processRightClick", notchName = "a", mcpDesc = "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/EnumHand;)Lnet/minecraft/util/EnumActionResult;", notchDesc = "(Laed;Lamu;Lub;)Lud;")
    public void processRightClick(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new TypeInsnNode(NEW, Type.getInternalName(EventRightClick.class)));
        insnList.add(new InsnNode(DUP));

        insnList.add(new VarInsnNode(ALOAD, 3));

        insnList.add(new MethodInsnNode(INVOKESPECIAL, Type.getInternalName(EventRightClick.class), "<init>",
                env == PatchManager.Environment.IDE ? "(Lnet/minecraft/util/EnumHand;)V" : "(Lub;)V", false));

        insnList.add(new VarInsnNode(ASTORE, 10));

        insnList.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(crack.class), "INSTANCE",
                "Lnet/wheel/cutils/crack;"));

        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(crack.class), "getEventManager",
                "()Lhand/interactor/voodoo/EventManager;", false));

        insnList.add(new VarInsnNode(ALOAD, 10));

        insnList.add(new MethodInsnNode(INVOKEINTERFACE, Type.getInternalName(EventManager.class), "dispatchEvent",
                "(Ljava/lang/Object;)Ljava/lang/Object;", true));

        insnList.add(new InsnNode(POP));

        insnList.add(new VarInsnNode(ALOAD, 10));

        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(EventRightClick.class), "isCanceled", "()Z",
                false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new FieldInsnNode(GETSTATIC,
                env == PatchManager.Environment.IDE ? "net/minecraft/util/EnumActionResult" : "ud",
                env == PatchManager.Environment.IDE ? "FAIL" : "c",
                env == PatchManager.Environment.IDE ? "Lnet/minecraft/util/EnumActionResult;" : "Lud;"));

        insnList.add(new InsnNode(ARETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "isHittingPosition", notchName = "b", mcpDesc = "(Lnet/minecraft/util/math/BlockPos;)Z", notchDesc = "(Let;)Z")
    public void isHittingPosition(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new VarInsnNode(ALOAD, 1));

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "isHittingPositionHook",
                env == PatchManager.Environment.IDE ? "(Lnet/minecraft/util/math/BlockPos;)Z" : "(Let;)Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(ICONST_0));

        insnList.add(new InsnNode(IRETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "getIsHittingBlock", notchName = "m", mcpDesc = "()Z")
    public void getIsHittingBlock(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "getIsHittingBlockHook",
                "()Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(ICONST_0));

        insnList.add(new InsnNode(IRETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "getBlockReachDistance", notchName = "d", mcpDesc = "()F")
    public void getBlockReachDistance(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList insnList = new InsnList();
        insnList.add(new TypeInsnNode(NEW, Type.getInternalName(EventPlayerReach.class)));
        insnList.add(new InsnNode(DUP));
        insnList.add(new MethodInsnNode(INVOKESPECIAL, Type.getInternalName(EventPlayerReach.class), "<init>", "()V",
                false));
        insnList.add(new VarInsnNode(ASTORE, 2));
        insnList.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(crack.class), "INSTANCE",
                "Lnet/wheel/cutils/crack;"));
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(crack.class), "getEventManager",
                "()Lhand/interactor/voodoo/EventManager;", false));
        insnList.add(new VarInsnNode(ALOAD, 2));
        insnList.add(new MethodInsnNode(INVOKEINTERFACE, Type.getInternalName(EventManager.class), "dispatchEvent",
                "(Ljava/lang/Object;)Ljava/lang/Object;", true));
        insnList.add(new InsnNode(POP));
        insnList.add(new VarInsnNode(ALOAD, 2));
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(EventPlayerReach.class), "isCanceled",
                "()Z", false));
        final LabelNode jmp = new LabelNode();
        insnList.add(new JumpInsnNode(IFEQ, jmp));
        insnList.add(new VarInsnNode(ALOAD, 2));
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(EventPlayerReach.class), "getReach", "()F",
                false));
        insnList.add(new InsnNode(FRETURN));
        insnList.add(jmp);
        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "extendedReach", notchName = "i", mcpDesc = "()Z")
    public void extendedReach(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList insnList = new InsnList();
        insnList.add(new TypeInsnNode(NEW, Type.getInternalName(EventExtendPlayerReach.class)));
        insnList.add(new InsnNode(DUP));
        insnList.add(new MethodInsnNode(INVOKESPECIAL, Type.getInternalName(EventExtendPlayerReach.class), "<init>",
                "()V", false));
        insnList.add(new VarInsnNode(ASTORE, 2));
        insnList.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(crack.class), "INSTANCE",
                "Lnet/wheel/cutils/crack;"));
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(crack.class), "getEventManager",
                "()Lhand/interactor/voodoo/EventManager;", false));
        insnList.add(new VarInsnNode(ALOAD, 2));
        insnList.add(new MethodInsnNode(INVOKEINTERFACE, Type.getInternalName(EventManager.class), "dispatchEvent",
                "(Ljava/lang/Object;)Ljava/lang/Object;", true));
        insnList.add(new InsnNode(POP));
        insnList.add(new VarInsnNode(ALOAD, 2));
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(EventExtendPlayerReach.class), "isCanceled",
                "()Z", false));
        final LabelNode jmp = new LabelNode();
        insnList.add(new JumpInsnNode(IFEQ, jmp));
        insnList.add(new InsnNode(ICONST_1));
        insnList.add(new InsnNode(IRETURN));
        insnList.add(jmp);
        methodNode.instructions.insert(insnList);
    }
}
