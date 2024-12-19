package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.minecraft.client.particle.Particle;

import net.wheel.cutils.api.event.render.EventAddEffect;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public class ParticleManagerPatch extends ClassPatch {

    public ParticleManagerPatch() {
        super("net.minecraft.client.particle.ParticleManager", "btg");
    }

    public static boolean addEffectHook(Particle particle) {
        final EventAddEffect event = new EventAddEffect(particle);
        crack.INSTANCE.getEventManager().dispatchEvent(event);
        return event.isCanceled();
    }

    @MethodPatch(mcpName = "addEffect", notchName = "a", mcpDesc = "(Lnet/minecraft/client/particle/Particle;)V", notchDesc = "(Lbtf;)V")
    public void addEffect(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new VarInsnNode(ALOAD, 1));

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "addEffectHook",
                env == PatchManager.Environment.IDE ? "(Lnet/minecraft/client/particle/Particle;)Z" : "(Lbtf;)Z",
                false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }
}
