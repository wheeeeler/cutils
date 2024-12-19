package net.wheel.cutils.impl.module.RENDER;

import java.util.List;

import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.server.*;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.gui.hud.EventUIValueChanged;
import net.wheel.cutils.api.event.gui.hud.modulelist.EventUIListValueChanged;
import net.wheel.cutils.api.event.network.EventReceivePacket;
import net.wheel.cutils.api.event.render.*;
import net.wheel.cutils.api.event.world.*;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class RenderTweaks extends Module {

    public final Value<Boolean> names = new Value<Boolean>("Names", new String[] { "Name", "n" },
            "Disables the rendering of vanilla name-tags", false);
    public final Value<Boolean> particles = new Value<Boolean>("Particles", new String[] { "Part", "par" },
            "Disables the spawning of all particles", true);
    public final Value<Boolean> particlesPackets = new Value<Boolean>("ParticlesPackets",
            new String[] { "PartPacket", "parpac" }, "Disables particle packets and effect packets", false);
    public final Value<Boolean> particlesEntityPackets = new Value<Boolean>("ParticlesEntityPackets",
            new String[] { "PartEntPacket", "parentpac" }, "Disables entity effect packets (usually particles)", false);
    public final Value<Boolean> slimes = new Value<Boolean>("Slimes", new String[] { "Slime", "sl" },
            "Choose to enable the slime lag fix. Disables slimes from spawning", false);
    public final Value<Boolean> blocks = new Value<Boolean>("Blocks", new String[] { "NoLagBlocks", "Block", "b" },
            "Manual override for block renders", false);
    public final Value<Boolean> blocksAll = new Value<Boolean>("BlocksAll",
            new String[] { "NoLagBlocksAll", "AllBlocks", "ba" }, "Disables the rendering of all blocks", false);
    public final Value<Boolean> items = new Value<Boolean>("Items", new String[] { "Item", "i" },
            "Manual override for dropped item renders", false);
    public final Value<Boolean> itemsAll = new Value<Boolean>("ItemsAll", new String[] { "AllItems", "ia" },
            "Disables the rendering of all items", false);
    public final Value<Boolean> itemsItemBlocks = new Value<Boolean>("ItemBlocks",
            new String[] { "AllItemBlocks", "itemblocks" }, "Disables the rendering of dropped item-block stacks",
            false);
    public final Value<List<Item>> itemsList = new Value<List<Item>>("ItemsList", new String[] { "ItemIds", "itemid" },
            "Items to disable rendering");
    public final Value<Boolean> crystals = new Value<Boolean>("Crystals", new String[] { "Crystal", "cr", "c" },
            "Disables the rendering of crystals", false);
    public final Value<Boolean> fireworks = new Value<Boolean>("Fireworks", new String[] { "FireW", "Fworks", "fw" },
            "Disables the rendering of fireworks", false);
    public final Value<Boolean> fireworksEffects = new Value<Boolean>("FireworksEffects",
            new String[] { "FireWE", "Fworkfx", "fwe" }, "Disables the rendering of firework effects", false);
    public final Value<Boolean> fluids = new Value<Boolean>("Fluids",
            new String[] { "Fluid", "f", "Liquids", "liq", "Water", "Lava" }, "Disables the rendering of all fluids",
            false);
    public final Value<Boolean> pistons = new Value<Boolean>("Pistons", new String[] { "Piston", "p" },
            "Choose to enable the piston lag fix. Disables pistons from rendering", false);
    public final Value<Boolean> redstone = new Value<Boolean>("Redstone", new String[] { "Red", "r" },
            "Disables the rendering of redstone dust", false);
    public final Value<Boolean> redstoneLogic = new Value<Boolean>("RedstoneLogic", new String[] { "RedLogic", "rl" },
            "Disables the rendering of redstone logic blocks", false);
    public final Value<Boolean> redstoneTorch = new Value<Boolean>("RedstoneTorch", new String[] { "RedTorch", "rt" },
            "Disables the rendering of redstone torches", false);
    public final Value<Boolean> skulls = new Value<Boolean>("Skulls",
            new String[] { "WitherSkull", "skulls", "skull", "ws" }, "Disables the rendering of flying wither skulls",
            false);
    public final Value<Boolean> sounds = new Value<Boolean>("Sounds", new String[] { "Sound", "s" },
            "Choose to enable the sound lag fix. Disable entity swap-item/equip sound", true);
    public final Value<Boolean> storms = new Value<Boolean>("Storms", new String[] { "Lightning" },
            "Disables the rendering of lightning strikes", false);
    public final Value<Boolean> tnt = new Value<Boolean>("TNT", new String[] { "Dynamite", "explosives", "tn" },
            "Disables the rendering of (primed) TNT", false);
    public final Value<Boolean> torches = new Value<Boolean>("Torches", new String[] { "Torch", "t" },
            "Disables the rendering of torches", false);
    public final Value<Boolean> withers = new Value<Boolean>("Withers", new String[] { "Wither", "w" },
            "Disables the rendering of withers", false);
    public final Value<Boolean> withersForce = new Value<Boolean>("WithersForce", new String[] { "WithersF", "wf" },
            "Force disables the rendering of withers", false);
    public final Value<Float> itemDrops = new Value<>("itmdrops", new String[] { "idrop" },
            "Max amount of on-ground item to rrender", 1.0f, 0.0f, 10000.0f, 0.01f);

    public RenderTweaks() {
        super("RenderTweaks", new String[] { "renderoptimizer" }, "fix rendering shit", "NONE", -1, ModuleType.RENDER);

        this.itemsList.setValue(new ObjectArrayList<>());
    }

    @Override
    public void onToggle() {
        super.onToggle();

        if (Minecraft.getMinecraft().world != null)
            Minecraft.getMinecraft().renderGlobal.loadRenderers();
    }

    @Listener
    public void onReceivePacket(EventReceivePacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof SPacketSpawnMob) {
                final SPacketSpawnMob packet = (SPacketSpawnMob) event.getPacket();
                if (this.slimes.getValue() && packet.getEntityType() == 55) {
                    event.setCanceled(true);
                }
                if (this.withersForce.getValue() && packet.getEntityType() == 64) {
                    event.setCanceled(true);
                }
                if (this.skulls.getValue() && packet.getEntityType() == 19) {
                    event.setCanceled(true);
                }
            }

            if (this.sounds.getValue() && event.getPacket() instanceof SPacketSoundEffect) {
                final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
                if ((packet.getCategory() == SoundCategory.PLAYERS
                        && packet.getSound() == SoundEvents.ITEM_ARMOR_EQUIP_GENERIC) ||
                        (packet.getSound() == SoundEvents.ENTITY_FIREWORK_LAUNCH)) {
                    event.setCanceled(true);
                }
            }

            if (this.particlesPackets.getValue()
                    && (event.getPacket() instanceof SPacketParticles || event.getPacket() instanceof SPacketEffect)) {
                event.setCanceled(true);
            }

            if (this.particlesEntityPackets.getValue() && event.getPacket() instanceof SPacketEntityEffect) {
                event.setCanceled(true);
            }
        }
    }

    @Listener
    public void onRenderBlock(EventRenderBlock event) {
        final BlockPos pos = event.getPos();
        final Block block = Minecraft.getMinecraft().world.getBlockState(pos).getBlock();

        if (renderBlock(block)) {
            event.setCanceled(true);
        }
    }

    private boolean renderBlock(Block block) {
        if (block == Blocks.AIR)
            return false;

        if (this.blocks.getValue() && this.blocksAll.getValue()) {
            return true;
        }

        return this.fluids.getValue() && block instanceof BlockLiquid
                || this.pistons.getValue()
                        && (block instanceof BlockPistonMoving || block instanceof BlockPistonExtension)
                || this.redstone.getValue()
                        && (block instanceof BlockRedstoneDiode || block instanceof BlockRedstoneWire)
                || this.redstoneTorch.getValue() && block instanceof BlockRedstoneTorch
                || this.redstoneLogic.getValue()
                        && (block instanceof BlockRedstoneComparator || block instanceof BlockRedstoneRepeater)
                || this.torches.getValue() && block instanceof BlockTorch;
    }

    @Listener
    public void onRenderEntity(EventRenderEntity event) {
        if (event.getEntity() instanceof EntityItem) {
            int maxItemsToRender = Math.round(this.itemDrops.getValue());
            int currentRenderedItems = 0;

            for (Entity entity : event.getEntity().world.loadedEntityList) {
                if (entity instanceof EntityItem) {
                    currentRenderedItems++;
                    if (currentRenderedItems > maxItemsToRender) {
                        event.setCanceled(true);
                        return;
                    }
                }
            }

            if (this.items.getValue()) {
                EntityItem entityItem = (EntityItem) event.getEntity();

                if (this.itemsAll.getValue()) {
                    event.setCanceled(true);
                    return;
                }

                if (this.itemsItemBlocks.getValue() && entityItem.getItem().getItem() instanceof ItemBlock) {
                    event.setCanceled(true);
                    return;
                }

                for (Item item : this.itemsList.getValue()) {
                    if (Item.getIdFromItem(item) == Item.getIdFromItem(entityItem.getItem().getItem())) {
                        event.setCanceled(true);
                        return;
                    }
                }
            }

            if (this.withers.getValue()) {
                if (event.getEntity() instanceof EntityWither)
                    event.setCanceled(true);
            }

            if (this.skulls.getValue()) {
                if (event.getEntity() instanceof EntityWitherSkull)
                    event.setCanceled(true);
            }

            if (this.crystals.getValue()) {
                if (event.getEntity() instanceof EntityEnderCrystal)
                    event.setCanceled(true);
            }

            if (this.tnt.getValue()) {
                if (event.getEntity() instanceof EntityTNTPrimed)
                    event.setCanceled(true);
            }

            if (this.fireworks.getValue()) {
                if (event.getEntity() instanceof EntityFireworkRocket)
                    event.setCanceled(true);
            }
        }
    }

    @Listener
    public void onSpawnEffectParticle(EventSpawnEffect event) {
        if (this.fireworksEffects.getValue()) {
            if (event.getParticleID() == EnumParticleTypes.FIREWORKS_SPARK.getParticleID() ||
                    event.getParticleID() == EnumParticleTypes.EXPLOSION_HUGE.getParticleID() ||
                    event.getParticleID() == EnumParticleTypes.EXPLOSION_LARGE.getParticleID() ||
                    event.getParticleID() == EnumParticleTypes.EXPLOSION_NORMAL.getParticleID()) {
                event.setCanceled(true);
            }
        }
    }

    @Listener
    public void onAddEffect(EventAddEffect event) {
        if (this.fireworksEffects.getValue()) {
            if (event.getParticle() instanceof ParticleFirework.Starter ||
                    event.getParticle() instanceof ParticleFirework.Spark ||
                    event.getParticle() instanceof ParticleFirework.Overlay) {
                event.setCanceled(true);
            }
        }
    }

    @Listener
    public void onRenderName(EventRenderName event) {
        if (this.names.getValue()) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onEntityAdd(EventAddEntity event) {
        if (this.fireworks.getValue()) {
            if (event.getEntity() instanceof EntityFireworkRocket) {
                event.setCanceled(true);
            }
        }

        if (this.withersForce.getValue()) {
            if (event.getEntity() instanceof EntityWither) {
                event.setCanceled(true);
            }
        }

        if (this.skulls.getValue()) {
            if (event.getEntity() instanceof EntityWitherSkull) {
                event.setCanceled(true);
            }
        }

        if (this.tnt.getValue()) {
            if (event.getEntity() instanceof EntityTNTPrimed) {
                event.setCanceled(true);
            }
        }

        if (this.storms.getValue()) {
            if (event.getEntity() instanceof EntityLightningBolt) {
                event.setCanceled(true);
            }
        }
    }

    @Listener
    public void onSpawnParticle(EventSpawnParticle event) {
        if (this.particles.getValue()) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onValueChanged(EventUIValueChanged event) {
        if (event.getValue().getAlias()[0].toLowerCase().startsWith("nolagblocks")) {
            Minecraft.getMinecraft().renderGlobal.loadRenderers();
        }
    }

    @Listener
    public void onListValueChanged(EventUIListValueChanged event) {
        if (event.getValue().getAlias()[0].equalsIgnoreCase("nolagblockslist")) {
            Minecraft.getMinecraft().renderGlobal.loadRenderers();
        }
    }
}
