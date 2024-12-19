package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.wheel.cutils.api.event.player.EventCapeLocation;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

import hand.interactor.voodoo.EventManager;

public final class AbstractClientPlayerPatch extends ClassPatch {

    public AbstractClientPlayerPatch() {
        super("net.minecraft.client.entity.AbstractClientPlayer", "bua");
    }

    @MethodPatch(mcpName = "getLocationCape", notchName = "q", mcpDesc = "()Lnet/minecraft/util/ResourceLocation;", notchDesc = "()Lnf;")
    public void getLocationCape(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList insnList = new InsnList();
        insnList.add(new TypeInsnNode(NEW, Type.getInternalName(EventCapeLocation.class)));
        insnList.add(new InsnNode(DUP));
        insnList.add(new VarInsnNode(ALOAD, 0));
        insnList.add(new MethodInsnNode(INVOKESPECIAL, Type.getInternalName(EventCapeLocation.class), "<init>",
                env == PatchManager.Environment.IDE ? "(Lnet/minecraft/client/entity/AbstractClientPlayer;)V"
                        : "(Lbua;)V",
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
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(EventCapeLocation.class), "isCanceled",
                "()Z", false));

        final LabelNode labelNode = new LabelNode();
        insnList.add(new JumpInsnNode(IFEQ, labelNode));
        insnList.add(new VarInsnNode(ALOAD, 2));
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(EventCapeLocation.class), "getLocation",
                env == PatchManager.Environment.IDE ? "()Lnet/minecraft/util/ResourceLocation;" : "()Lnf;", false));
        insnList.add(new InsnNode(ARETURN));
        insnList.add(labelNode);

        methodNode.instructions.insert(insnList);
    }

}
