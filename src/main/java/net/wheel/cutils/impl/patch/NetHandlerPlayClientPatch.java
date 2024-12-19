package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketChunkData;

import net.wheel.cutils.api.event.world.EventChunk;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.api.util.ASMUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public final class NetHandlerPlayClientPatch extends ClassPatch {

    public NetHandlerPlayClientPatch() {
        super("net.minecraft.client.network.NetHandlerPlayClient", "brz");
    }

    public static void handleChunkDataHook(SPacketChunkData chunkData) {
        if (chunkData != null) {
            final EventChunk event = new EventChunk(EventChunk.ChunkType.LOAD,
                    Minecraft.getMinecraft().world.getChunk(chunkData.getChunkX(), chunkData.getChunkZ()));
            crack.INSTANCE.getEventManager().dispatchEvent(event);
        }
    }

    @MethodPatch(mcpName = "handleChunkData", notchName = "a", mcpDesc = "(Lnet/minecraft/network/play/server/SPacketChunkData;)V", notchDesc = "(Lje;)V")
    public void handleChunkData(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(ALOAD, 1));
        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "handleChunkDataHook",
                env == PatchManager.Environment.IDE ? "(Lnet/minecraft/network/play/server/SPacketChunkData;)V"
                        : "(Lje;)V",
                false));
        methodNode.instructions.insertBefore(ASMUtil.bottom(methodNode), insnList);
    }
}
