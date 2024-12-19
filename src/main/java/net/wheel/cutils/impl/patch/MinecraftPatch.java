package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.lwjgl.input.Keyboard;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.minecraft.EventDisplayGui;
import net.wheel.cutils.api.event.minecraft.EventKeyPress;
import net.wheel.cutils.api.event.minecraft.EventRunTick;
import net.wheel.cutils.api.event.minecraft.EventUpdateFramebufferSize;
import net.wheel.cutils.api.event.mouse.EventMouseLeftClick;
import net.wheel.cutils.api.event.mouse.EventMouseRightClick;
import net.wheel.cutils.api.event.world.EventLoadWorld;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.api.util.ASMUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public final class MinecraftPatch extends ClassPatch {

    public MinecraftPatch() {
        super("net.minecraft.client.Minecraft", "bib");
    }

    public static void updateFramebufferSizeHook() {

        crack.INSTANCE.getEventManager().dispatchEvent(new EventUpdateFramebufferSize());
    }

    public static void runTickHook(EventStageable.EventStage stage) {

        crack.INSTANCE.getEventManager().dispatchEvent(new EventRunTick(stage));
    }

    public static void runTickKeyboardHook(int key) {

        if (Keyboard.getEventKeyState()) {

            crack.INSTANCE.getEventManager().dispatchEvent(new EventKeyPress(key));
        }
    }

    public static boolean displayGuiScreenHook(GuiScreen screen) {

        final EventDisplayGui event = new EventDisplayGui(screen);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static boolean loadWorldHook(WorldClient worldClient) {
        final EventLoadWorld event = new EventLoadWorld(worldClient);
        crack.INSTANCE.getEventManager().dispatchEvent(event);
        return event.isCanceled();
    }

    public static boolean clickMouseHook() {
        final EventMouseLeftClick event = new EventMouseLeftClick();
        crack.INSTANCE.getEventManager().dispatchEvent(event);
        return event.isCanceled();
    }

    public static boolean rightClickMouseHook() {
        final EventMouseRightClick event = new EventMouseRightClick();
        crack.INSTANCE.getEventManager().dispatchEvent(event);
        return event.isCanceled();
    }

    @MethodPatch(mcpName = "updateFramebufferSize", notchName = "aC", mcpDesc = "()V")
    public void updateFramebufferSize(MethodNode methodNode, PatchManager.Environment env) {

        methodNode.instructions.insert(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()),
                "updateFramebufferSizeHook", "()V", false));
    }

    @MethodPatch(mcpName = "runTick", notchName = "t", mcpDesc = "()V")
    public void runTick(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList preInsn = new InsnList();

        preInsn.add(new FieldInsnNode(GETSTATIC, "net/wheel/cutils/api/event/EventStageable$EventStage", "PRE",
                "Lnet/wheel/cutils/api/event/EventStageable$EventStage;"));

        preInsn.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "runTickHook",
                "(Lnet/wheel/cutils/api/event/EventStageable$EventStage;)V", false));

        methodNode.instructions.insert(preInsn);

        final InsnList postInsn = new InsnList();

        postInsn.add(new FieldInsnNode(GETSTATIC, "net/wheel/cutils/api/event/EventStageable$EventStage", "POST",
                "Lnet/wheel/cutils/api/event/EventStageable$EventStage;"));

        postInsn.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "runTickHook",
                "(Lnet/wheel/cutils/api/event/EventStageable$EventStage;)V", false));

        methodNode.instructions.insertBefore(ASMUtil.bottom(methodNode), postInsn);
    }

    @MethodPatch(mcpName = "runTickKeyboard", notchName = "aD", mcpDesc = "()V")
    public void runTickKeyboard(MethodNode methodNode, PatchManager.Environment env) {

        final AbstractInsnNode target = ASMUtil.findMethodInsn(methodNode, INVOKEVIRTUAL,
                env == PatchManager.Environment.IDE ? "net/minecraft/client/Minecraft" : "bib",
                env == PatchManager.Environment.IDE ? "dispatchKeypresses" : "W", "()V");

        if (target != null) {

            final InsnList insnList = new InsnList();

            insnList.add(new VarInsnNode(ILOAD, 1));

            insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "runTickKeyboardHook",
                    "(I)V", false));

            methodNode.instructions.insert(target, insnList);
        }
    }

    @MethodPatch(mcpName = "displayGuiScreen", notchName = "a", mcpDesc = "(Lnet/minecraft/client/gui/GuiScreen;)V", notchDesc = "(Lblk;)V")
    public void displayGuiScreen(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new VarInsnNode(ALOAD, 1));

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "displayGuiScreenHook",
                env == PatchManager.Environment.IDE ? "(Lnet/minecraft/client/gui/GuiScreen;)Z" : "(Lblk;)Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "loadWorld", notchName = "a", mcpDesc = "(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", notchDesc = "(Lbsb;Ljava/lang/String;)V")
    public void loadWorld(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(ALOAD, 1));
        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "loadWorldHook",
                env == PatchManager.Environment.IDE ? "(Lnet/minecraft/client/multiplayer/WorldClient;)Z" : "(Lbsb;)Z",
                false));
        final LabelNode jmp = new LabelNode();
        insnList.add(new JumpInsnNode(IFEQ, jmp));
        insnList.add(new InsnNode(RETURN));
        insnList.add(jmp);
        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "clickMouse", notchName = "aA", mcpDesc = "()V", notchDesc = "()V")
    public void clickMouse(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList insnList = new InsnList();
        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "clickMouseHook", "()Z",
                false));
        final LabelNode jmp = new LabelNode();
        insnList.add(new JumpInsnNode(IFEQ, jmp));
        insnList.add(new InsnNode(RETURN));
        insnList.add(jmp);
        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "rightClickMouse", notchName = "aB", mcpDesc = "()V", notchDesc = "()V")
    public void rightClickMouse(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList insnList = new InsnList();
        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "rightClickMouseHook",
                "()Z", false));
        final LabelNode jmp = new LabelNode();
        insnList.add(new JumpInsnNode(IFEQ, jmp));
        insnList.add(new InsnNode(RETURN));
        insnList.add(jmp);
        methodNode.instructions.insert(insnList);
    }
}
