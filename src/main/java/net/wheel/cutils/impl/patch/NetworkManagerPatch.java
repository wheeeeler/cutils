package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketCustomPayload;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventReceivePacket;
import net.wheel.cutils.api.event.network.EventSendPacket;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.api.util.ASMUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public final class NetworkManagerPatch extends ClassPatch {

    public NetworkManagerPatch() {
        super("net.minecraft.network.NetworkManager", "gw");
    }

    public static boolean sendPacketHook(Packet packet, EventStageable.EventStage stage) {
        final EventSendPacket event = new EventSendPacket(stage, packet);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        if (event.isCanceled()) {
            return true;
        }

        if (packet instanceof CPacketCustomPayload) {
            CPacketCustomPayload customPacket = (CPacketCustomPayload) packet;

            if ("FML|HS".equals(customPacket.getChannelName())) {
                crack.INSTANCE.getEventManager().dispatchEvent(event);
            }
        }

        return false;
    }

    public static boolean channelRead0Hook(Packet packet, EventStageable.EventStage stage) {
        if (packet != null) {
            final EventReceivePacket event = new EventReceivePacket(stage, packet);
            crack.INSTANCE.getEventManager().dispatchEvent(event);

            return event.isCanceled();
        }

        return false;
    }

    @MethodPatch(mcpName = "sendPacket", notchName = "a", mcpDesc = "(Lnet/minecraft/network/Packet;)V", notchDesc = "(Lht;)V")
    public void sendPacket(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList preInsn = new InsnList();
        preInsn.add(new VarInsnNode(ALOAD, 1));
        preInsn.add(new FieldInsnNode(GETSTATIC, "net/wheel/cutils/api/event/EventStageable$EventStage", "PRE",
                "Lnet/wheel/cutils/api/event/EventStageable$EventStage;"));
        preInsn.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "sendPacketHook",
                env == PatchManager.Environment.IDE
                        ? "(Lnet/minecraft/network/Packet;Lnet/wheel/cutils/api/event/EventStageable$EventStage;)Z"
                        : "(Lht;Lnet/wheel/cutils/api/event/EventStageable$EventStage;)Z",
                false));
        final LabelNode jmp = new LabelNode();
        preInsn.add(new JumpInsnNode(IFEQ, jmp));
        preInsn.add(new InsnNode(RETURN));
        preInsn.add(jmp);
        methodNode.instructions.insert(preInsn);

        final InsnList postInsn = new InsnList();
        postInsn.add(new VarInsnNode(ALOAD, 1));
        postInsn.add(new FieldInsnNode(GETSTATIC, "net/wheel/cutils/api/event/EventStageable$EventStage", "POST",
                "Lnet/wheel/cutils/api/event/EventStageable$EventStage;"));
        postInsn.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "sendPacketHook",
                env == PatchManager.Environment.IDE
                        ? "(Lnet/minecraft/network/Packet;Lnet/wheel/cutils/api/event/EventStageable$EventStage;)Z"
                        : "(Lht;Lnet/wheel/cutils/api/event/EventStageable$EventStage;)Z",
                false));
        methodNode.instructions.insertBefore(ASMUtil.bottom(methodNode), postInsn);
    }

    @MethodPatch(mcpName = "channelRead0", notchName = "a", mcpDesc = "(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", notchDesc = "(Lio/netty/channel/ChannelHandlerContext;Lht;)V")
    public void channelRead0(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList preInsn = new InsnList();
        preInsn.add(new VarInsnNode(ALOAD, 2));
        preInsn.add(new FieldInsnNode(GETSTATIC, "net/wheel/cutils/api/event/EventStageable$EventStage", "PRE",
                "Lnet/wheel/cutils/api/event/EventStageable$EventStage;"));
        preInsn.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "channelRead0Hook",
                env == PatchManager.Environment.IDE
                        ? "(Lnet/minecraft/network/Packet;Lnet/wheel/cutils/api/event/EventStageable$EventStage;)Z"
                        : "(Lht;Lnet/wheel/cutils/api/event/EventStageable$EventStage;)Z",
                false));
        final LabelNode jmp = new LabelNode();
        preInsn.add(new JumpInsnNode(IFEQ, jmp));
        preInsn.add(new InsnNode(RETURN));
        preInsn.add(jmp);
        methodNode.instructions.insert(preInsn);
    }
}
