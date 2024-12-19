package net.wheel.cutils.impl.module.MVMT;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemShield;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.math.BlockPos;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.player.EventPlayerUpdate;
import net.wheel.cutils.api.event.player.EventUpdateInput;
import net.wheel.cutils.api.event.world.EventCollideSoulSand;
import net.wheel.cutils.api.event.world.EventLandOnSlime;
import net.wheel.cutils.api.event.world.EventWalkOnSlime;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AntiSlowModule extends Module {

    public final Value<Boolean> soulsand = new Value<>("soplsand", new String[] { "Soul", "SS" },
            "Disables the slowness from walking on soul sand", true);
    public final Value<Boolean> slime = new Value<>("slime",
            new String[] { "Slime", "SlimeBlock", "SlimeBlocks", "slim" },
            "Disables the slowness from walking on slime blocks", true);
    public final Value<Boolean> items = new Value<>("items", new String[] { "it" },
            "Disables the slowness from using items (shields, eating, etc)", true);
    public final Value<Boolean> cobweb = new Value<>("cobweb", new String[] { "Webs", "Cob" },
            "Disables slowness from moving in a cobweb", true);
    public final Value<Boolean> ice = new Value<>("ice", new String[] { "ic" }, "Disables slowness from walking on ice",
            true);
    public final Value<Boolean> liquid = new Value<>("liquids", new String[] { "LiquidMovement" },
            "Disables slowness from water, lava, or other fluids", true);

    public AntiSlowModule() {
        super("AntiSlow", new String[] { "AntiSlow", "NoSlowdown", "AntiSlowdown" },
                "Allows you to move faster with things that slow you down", "NONE", -1, ModuleType.MVMT);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Blocks.ICE.setDefaultSlipperiness(0.98f);
        Blocks.FROSTED_ICE.setDefaultSlipperiness(0.98f);
        Blocks.PACKED_ICE.setDefaultSlipperiness(0.98f);
    }

    @Listener
    public void collideSoulSand(EventCollideSoulSand event) {
        if (this.soulsand.getValue()) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onWalkOnSlime(EventWalkOnSlime event) {
        if (this.slime.getValue()) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onLandOnSlime(EventLandOnSlime event) {
        if (this.slime.getValue()) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            final Minecraft mc = Minecraft.getMinecraft();

            if (this.items.getValue() && mc.player.isHandActive()) {
                if (mc.player.getHeldItem(mc.player.getActiveHand()).getItem() instanceof ItemShield) {
                    if (mc.player.movementInput.moveStrafe != 0
                            || mc.player.movementInput.moveForward != 0 && mc.player.getItemInUseMaxCount() >= 8) {
                        mc.player.connection
                                .sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM,
                                        BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
                    }
                }
            }

            if (this.cobweb.getValue()) {
                mc.player.isInWeb = false;
                if (mc.player.getRidingEntity() != null) {
                    mc.player.getRidingEntity().isInWeb = false;
                }
            }

            if (this.ice.getValue()) {
                Blocks.ICE.setDefaultSlipperiness(0.45f);
                Blocks.FROSTED_ICE.setDefaultSlipperiness(0.45f);
                Blocks.PACKED_ICE.setDefaultSlipperiness(0.45f);
            }

            if (this.liquid.getValue() && (mc.player.isInLava() || mc.player.isInWater())) {
                if (mc.player.isInWater()) {
                    mc.player.motionY = 0.08;
                } else if (mc.player.isInLava()) {
                    mc.player.motionY = 0.07;
                }
                if (mc.player.movementInput.moveForward > 0) {
                    mc.player.motionX += Math.sin(Math.toRadians(mc.player.rotationYaw)) * 0.03;
                    mc.player.motionZ -= Math.cos(Math.toRadians(mc.player.rotationYaw)) * 0.03;
                }
            }
        }
    }

    @Listener
    public void updateInput(EventUpdateInput event) {
        if (this.items.getValue()) {
            final Minecraft mc = Minecraft.getMinecraft();
            if (mc.player.isHandActive() && !mc.player.isRiding()) {
                mc.player.movementInput.moveStrafe /= 0.2f;
                mc.player.movementInput.moveForward /= 0.2f;
            }
        }
    }
}
