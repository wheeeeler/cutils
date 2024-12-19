package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.minecraft.client.network.NetworkPlayerInfo;

import net.wheel.cutils.api.event.gui.EventGetGuiTabName;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public final class GuiPlayerTabOverlayPatch extends ClassPatch {

    public GuiPlayerTabOverlayPatch() {
        super("net.minecraft.client.gui.GuiPlayerTabOverlay", "bjq");
    }

    public static String getPlayerNameHook(NetworkPlayerInfo networkPlayerInfo) {
        final EventGetGuiTabName event = new EventGetGuiTabName(networkPlayerInfo.getDisplayName() != null
                ? networkPlayerInfo.getDisplayName().getUnformattedComponentText()
                : networkPlayerInfo.getGameProfile().getName());
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.getName();
    }

    @MethodPatch(mcpName = "getPlayerName", notchName = "a", mcpDesc = "(Lnet/minecraft/client/network/NetworkPlayerInfo;)Ljava/lang/String;", notchDesc = "(Lbsc;)Ljava/lang/String;")
    public void getPlayerName(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(ALOAD, 1));
        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "getPlayerNameHook",
                env == PatchManager.Environment.IDE
                        ? "(Lnet/minecraft/client/network/NetworkPlayerInfo;)Ljava/lang/String;"
                        : "(Lbsc;)Ljava/lang/String;",
                false));
        insnList.add(new InsnNode(ARETURN));
        methodNode.instructions.insert(insnList);
    }

}
