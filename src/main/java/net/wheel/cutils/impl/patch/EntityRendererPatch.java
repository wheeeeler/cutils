package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import net.wheel.cutils.api.event.player.EventFovModifier;
import net.wheel.cutils.api.event.player.EventGetMouseOver;
import net.wheel.cutils.api.event.player.EventPlayerReach;
import net.wheel.cutils.api.event.render.*;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.api.util.ASMUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

import hand.interactor.voodoo.EventManager;

public final class EntityRendererPatch extends ClassPatch {

    public EntityRendererPatch() {
        super("net.minecraft.client.renderer.EntityRenderer", "buq");
    }

    public static void updateCameraAndRenderHook(float partialTicks) {

        crack.INSTANCE.getEventManager()
                .dispatchEvent(new EventRender2D(partialTicks, new ScaledResolution(Minecraft.getMinecraft())));
    }

    public static void renderWorldPassHook(float partialTicks) {
        if (crack.INSTANCE.getCameraManager().isCameraRecording()) {
            return;
        }

        crack.INSTANCE.getEventManager().dispatchEvent(new EventRender3D(partialTicks));
    }

    public static boolean hurtCameraEffectHook() {

        final EventHurtCamEffect event = new EventHurtCamEffect();
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    public static boolean orientCameraHook() {
        final EventOrientCamera event = new EventOrientCamera();
        crack.INSTANCE.getEventManager().dispatchEvent(event);
        return event.isCanceled();
    }

    public static void getMouseOverHook(float partialTicks) {
        final Minecraft mc = Minecraft.getMinecraft();
        final Entity entity = mc.getRenderViewEntity();
        if (entity != null && mc.world != null) {
            mc.profiler.startSection("pick");
            mc.pointedEntity = null;
            double d0 = mc.playerController.getBlockReachDistance();
            mc.objectMouseOver = entity.rayTrace(d0, partialTicks);
            Vec3d vec3d = entity.getPositionEyes(partialTicks);
            boolean flag = false;

            double d1 = d0;
            if (mc.playerController.extendedReach()) {
                final EventPlayerReach event = new EventPlayerReach();
                crack.INSTANCE.getEventManager().dispatchEvent(event);
                d1 = event.isCanceled() ? event.getReach() : 6.0d;
                d0 = d1;
            } else if (d0 > 3.0D) {
                flag = true;
            }

            if (mc.objectMouseOver != null) {
                d1 = mc.objectMouseOver.hitVec.distanceTo(vec3d);
            }

            Vec3d vec3d1 = entity.getLook(1.0F);
            Vec3d vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
            mc.entityRenderer.pointedEntity = null;
            Vec3d vec3d3 = null;
            float f = 1.0F;
            List<Entity> list = mc.world
                    .getEntitiesInAABBexcluding(entity,
                            entity.getEntityBoundingBox().expand(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0).grow(1.0D,
                                    1.0D, 1.0D),
                            Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
                                public boolean apply(@Nullable Entity p_apply_1_) {
                                    return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
                                }
                            }));

            final EventGetMouseOver event = new EventGetMouseOver();
            crack.INSTANCE.getEventManager().dispatchEvent(event);

            if (event.isCanceled()) {
                list = new ArrayList<>();
            }

            double d2 = d1;

            for (int j = 0; j < list.size(); ++j) {
                Entity entity1 = list.get(j);
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(entity1.getCollisionBorderSize());
                RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);
                if (axisalignedbb.contains(vec3d)) {
                    if (d2 >= 0.0D) {
                        mc.entityRenderer.pointedEntity = entity1;
                        vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
                        d2 = 0.0D;
                    }
                } else if (raytraceresult != null) {
                    double d3 = vec3d.distanceTo(raytraceresult.hitVec);
                    if (d3 < d2 || d2 == 0.0D) {
                        if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity()
                                && !entity1.canRiderInteract()) {
                            if (d2 == 0.0D) {
                                mc.entityRenderer.pointedEntity = entity1;
                                vec3d3 = raytraceresult.hitVec;
                            }
                        } else {
                            mc.entityRenderer.pointedEntity = entity1;
                            vec3d3 = raytraceresult.hitVec;
                            d2 = d3;
                        }
                    }
                }
            }

            if (mc.entityRenderer.pointedEntity != null && flag && vec3d.distanceTo(vec3d3) > 3.0D) {
                mc.entityRenderer.pointedEntity = null;
                mc.objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, vec3d3, null, new BlockPos(vec3d3));
            }

            if (mc.entityRenderer.pointedEntity != null && (d2 < d1 || mc.objectMouseOver == null)) {
                mc.objectMouseOver = new RayTraceResult(mc.entityRenderer.pointedEntity, vec3d3);
                if (mc.entityRenderer.pointedEntity instanceof EntityLivingBase
                        || mc.entityRenderer.pointedEntity instanceof EntityItemFrame) {
                    mc.pointedEntity = mc.entityRenderer.pointedEntity;
                }
            }

            mc.profiler.endSection();
        }

    }

    public static boolean drawNameplateHook(FontRenderer fontRenderer, String str, float x, float y, float z,
            int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking) {

        final EventDrawNameplate event = new EventDrawNameplate(fontRenderer, str, x, y, z, verticalShift, viewerYaw,
                viewerPitch, isThirdPersonFrontal, isSneaking);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.isCanceled();
    }

    @MethodPatch(mcpName = "updateCameraAndRender", notchName = "a", mcpDesc = "(FJ)V")
    public void updateCameraAndRender(MethodNode methodNode, PatchManager.Environment env) {

        final AbstractInsnNode target = ASMUtil.findMethodInsn(methodNode, INVOKEVIRTUAL,
                env == PatchManager.Environment.IDE ? "net/minecraft/client/gui/GuiIngame" : "biq",
                env == PatchManager.Environment.IDE ? "renderGameOverlay" : "a", "(F)V");

        if (target != null) {

            final InsnList insnList = new InsnList();

            insnList.add(new VarInsnNode(FLOAD, 1));

            insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()),
                    "updateCameraAndRenderHook", "(F)V", false));

            methodNode.instructions.insert(target, insnList);
        }
    }

    @MethodPatch(mcpName = "renderWorldPass", notchName = "a", mcpDesc = "(IFJ)V")
    public void renderWorldPass(MethodNode methodNode, PatchManager.Environment env) {

        final AbstractInsnNode target = ASMUtil.findInsnLdc(methodNode, "hand");

        if (target != null) {

            final InsnList list = new InsnList();

            list.add(new VarInsnNode(FLOAD, 2));

            list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "renderWorldPassHook",
                    "(F)V", false));

            methodNode.instructions.insert(target.getNext(), list);
        }
    }

    @MethodPatch(mcpName = "hurtCameraEffect", notchName = "d", mcpDesc = "(F)V")
    public void hurtCameraEffect(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "hurtCameraEffectHook",
                "()Z", false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "orientCamera", notchName = "f", mcpDesc = "(F)V")
    public void orientCamera(MethodNode methodNode, PatchManager.Environment env) {
        final AbstractInsnNode target = ASMUtil.findMethodInsn(methodNode, INVOKEVIRTUAL,
                env == PatchManager.Environment.IDE ? "net/minecraft/client/multiplayer/WorldClient" : "bsb",
                env == PatchManager.Environment.IDE ? "rayTraceBlocks" : "a",
                env == PatchManager.Environment.IDE
                        ? "(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;"
                        : "(Lbhe;Lbhe;)Lbhc;");

        if (target != null) {
            final InsnList insnList = new InsnList();
            insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "orientCameraHook",
                    "()Z", false));
            final LabelNode jmp = new LabelNode();
            insnList.add(new JumpInsnNode(IFEQ, jmp));
            insnList.add(new InsnNode(ACONST_NULL));
            insnList.add(new VarInsnNode(ASTORE, 24));
            insnList.add(jmp);
            methodNode.instructions.insert(target.getNext(), insnList);
        }
    }

    @MethodPatch(mcpName = "getFOVModifier", notchName = "a", mcpDesc = "(FZ)F")
    public void getFovModifier(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList insnList = new InsnList();
        insnList.add(new TypeInsnNode(NEW, Type.getInternalName(EventFovModifier.class)));
        insnList.add(new InsnNode(DUP));
        insnList.add(new MethodInsnNode(INVOKESPECIAL, Type.getInternalName(EventFovModifier.class), "<init>", "()V",
                false));
        insnList.add(new VarInsnNode(ASTORE, 5));

        insnList.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(crack.class), "INSTANCE",
                "Lnet/wheel/cutils/crack;"));
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(crack.class), "getEventManager",
                "()Lhand/interactor/voodoo/EventManager;", false));
        insnList.add(new VarInsnNode(ALOAD, 5));
        insnList.add(new MethodInsnNode(INVOKEINTERFACE, Type.getInternalName(EventManager.class), "dispatchEvent",
                "(Ljava/lang/Object;)Ljava/lang/Object;", true));
        insnList.add(new InsnNode(POP));

        insnList.add(new VarInsnNode(ALOAD, 5));
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(EventFovModifier.class), "isCanceled",
                "()Z", false));
        final LabelNode label = new LabelNode();
        insnList.add(new JumpInsnNode(IFEQ, label));
        insnList.add(new VarInsnNode(ALOAD, 5));
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(EventFovModifier.class), "getFov", "()F",
                false));
        insnList.add(new InsnNode(FRETURN));
        insnList.add(label);
        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "getMouseOver", notchName = "a", mcpDesc = "(F)V")
    public void getMouseOver(MethodNode methodNode, PatchManager.Environment env) {
        final InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(FLOAD, 1));
        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "getMouseOverHook", "(F)V",
                false));
        insnList.add(new InsnNode(RETURN));
        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "drawNameplate", notchName = "a", mcpDesc = "(Lnet/minecraft/client/gui/FontRenderer;Ljava/lang/String;FFFIFFZZ)V", notchDesc = "(Lbip;Ljava/lang/String;FFFIFFZZ)V")
    public void drawNameplate(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new VarInsnNode(ALOAD, 0));
        insnList.add(new VarInsnNode(ALOAD, 1));
        insnList.add(new VarInsnNode(FLOAD, 2));
        insnList.add(new VarInsnNode(FLOAD, 3));
        insnList.add(new VarInsnNode(FLOAD, 4));
        insnList.add(new VarInsnNode(ILOAD, 5));
        insnList.add(new VarInsnNode(FLOAD, 6));
        insnList.add(new VarInsnNode(FLOAD, 7));
        insnList.add(new VarInsnNode(ILOAD, 8));
        insnList.add(new VarInsnNode(ILOAD, 9));

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "drawNameplateHook",
                env == PatchManager.Environment.IDE
                        ? "(Lnet/minecraft/client/gui/FontRenderer;Ljava/lang/String;FFFIFFZZ)Z"
                        : "(Lbip;Ljava/lang/String;FFFIFFZZ)Z",
                false));

        final LabelNode jmp = new LabelNode();

        insnList.add(new JumpInsnNode(IFEQ, jmp));

        insnList.add(new InsnNode(RETURN));

        insnList.add(jmp);

        methodNode.instructions.insert(insnList);
    }
}
