package net.wheel.cutils.impl.module.CRACK;

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import net.wheel.cutils.api.event.network.EventReceivePacket;
import net.wheel.cutils.api.event.render.EventRenderBlock;
import net.wheel.cutils.api.event.render.EventRenderFog;
import net.wheel.cutils.api.event.render.EventRenderSky;
import net.wheel.cutils.api.event.render.EventRenderTileEntity;
import net.wheel.cutils.api.event.world.EventLightUpdate;
import net.wheel.cutils.api.event.world.EventTileEntityUpdate;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class EventPacketManipulationModule extends Module {

    public final Value<Boolean> blockRenderCancel = new Value<>("BlockRC", new String[] { "BRC", "BlockRenderCancel" },
            "Block render cancel", true);
    public final Value<Boolean> tileRenderCancel = new Value<>("TileeRC",
            new String[] { "TRC", "TileEntityRenderCancel" }, "TE render cancel", false);
    public final Value<Boolean> tileUpdateCancel = new Value<>("TileeUC",
            new String[] { "TUC", "TileEntityUpdateCancel" }, "TE update cancel", false);
    public final Value<Boolean> blockPacket = new Value<>("PacketCancel", new String[] { "pblock" },
            "fist your game by telling it that THINGS do not exist", false);
    public final Value<Boolean> fog = new Value<>("Fog", new String[] { "Fog" }, "Fog", true);
    public final Value<Boolean> sky = new Value<>("Sky", new String[] { "Sky" }, "Sky", true);
    public final Value<Boolean> light = new Value<>("Light", new String[] { "Light" }, "Light", true);

    public EventPacketManipulationModule() {
        super("EPM", new String[] { "EPM", "EventPacketManipulation" }, "Event & packet manipulation", "NONE", -1,
                ModuleType.CRACK);
    }

    public boolean isRCManaged(String modId) {
        return crack.INSTANCE.getRenderCancelManager().getRenderManagedModIds().contains(modId.toLowerCase());
    }

    @Override
    public void onToggle() {
        super.onToggle();
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world != null) {
            mc.renderGlobal.loadRenderers();
        }
    }

    @Listener
    public void onRenderTileEntity(EventRenderTileEntity event) {
        if (this.tileRenderCancel.getValue()) {
            getTileEntityModId(event.getTileEntity()).ifPresent(modId -> {
                if (isRCManaged(modId)) {
                    event.setCanceled(true);
                }
            });
        }
    }

    private Optional<String> getTileEntityModId(TileEntity tileEntity) {
        if (tileEntity == null)
            return Optional.empty();

        Block block = tileEntity.getBlockType();
        if (block == null)
            return Optional.empty();

        ResourceLocation registryName = block.getRegistryName();
        return Optional.ofNullable(registryName != null ? registryName.getNamespace() : null);
    }

    @Listener
    public void onRenderBlock(EventRenderBlock event) {
        BlockPos pos = event.getPos();
        IBlockState blockState = Minecraft.getMinecraft().world.getBlockState(pos);
        Block block = blockState.getBlock();

        if (block != Blocks.AIR && renderBlock(block)) {
            event.setCanceled(true);
        }
    }

    private boolean renderBlock(Block block) {
        ResourceLocation registryName = block.getRegistryName();
        if (registryName == null)
            return false;

        return this.blockRenderCancel.getValue() && isRCManaged(registryName.getNamespace());
    }

    @Listener
    public void onTileEntityUpdate(EventTileEntityUpdate event) {
        if (this.tileUpdateCancel.getValue()) {
            getTileEntityModId(event.getTileEntity()).ifPresent(modId -> {
                if (isRCManaged(modId)) {
                    event.setCanceled(true);
                }
            });
        }
    }

    @Listener
    public void onUpdateLighting(EventLightUpdate event) {
        if (this.light.getValue()) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onRenderFog(EventRenderFog event) {
        if (this.fog.getValue()) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onRenderSky(EventRenderSky event) {
        if (this.sky.getValue()) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onReceivePacket(EventReceivePacket event) {
        if (!this.blockPacket.getValue()) {
            return;
        }

        if (!(event.getPacket() instanceof FMLProxyPacket)) {
            return;
        }

        FMLProxyPacket fmlPacket = (FMLProxyPacket) event.getPacket();
        String channelName = fmlPacket.channel().toLowerCase();

        if (crack.INSTANCE.getPacketCancelManager().getCancelledChannels().contains(channelName)) {
            event.setCanceled(true);
        }
    }
}
