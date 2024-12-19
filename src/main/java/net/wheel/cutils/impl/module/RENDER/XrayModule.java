package net.wheel.cutils.impl.module.RENDER;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.math.BlockPos;

import lombok.Getter;

import net.wheel.cutils.api.event.gui.hud.modulelist.EventUIListValueChanged;
import net.wheel.cutils.api.event.render.EventRenderBlock;
import net.wheel.cutils.api.event.world.EventSetOpaqueCube;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class XrayModule extends Module {

    @Getter
    public final Value<List<Block>> blocks = new Value<List<Block>>("Ids", new String[] { "id", "i" },
            "Blocks to xray for");

    private float lastGamma;
    private int lastAO;

    public XrayModule() {
        super("CrackXray", new String[] { "JadeVision", "Jade" }, "Allows you to filter what the world renders",
                "NONE", -1, ModuleType.RENDER);

        this.blocks.setValue(new ArrayList<>());

        if (crack.INSTANCE.getConfigManager().isFirstLaunch())
            this.add("diamond_ore");
    }

    @Override
    public void onEnable() {
        super.onEnable();

        final Minecraft mc = Minecraft.getMinecraft();
        lastGamma = mc.gameSettings.gammaSetting;
        lastAO = mc.gameSettings.ambientOcclusion;

        mc.gameSettings.gammaSetting = 100;
        mc.gameSettings.ambientOcclusion = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        Minecraft.getMinecraft().gameSettings.gammaSetting = lastGamma;
        Minecraft.getMinecraft().gameSettings.ambientOcclusion = lastAO;
    }

    @Override
    public void onToggle() {
        super.onToggle();

        Minecraft.getMinecraft().renderGlobal.loadRenderers();
    }

    @Listener
    public void onRenderBlock(EventRenderBlock event) {
        final BlockPos pos = event.getPos();
        IBlockState state = event.getState();

        if (!this.contains(state.getBlock())) {
            event.setCanceled(true);
            return;
        }

        state = state.getBlock().getExtendedState(state, event.getAccess(), pos);

        if (state.getBlock() instanceof BlockLiquid) {
            Minecraft.getMinecraft().getBlockRendererDispatcher().fluidRenderer.renderFluid(event.getAccess(), state,
                    pos, event.getBufferBuilder());
        } else {
            final IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
            Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(event.getAccess(),
                    model, state, pos, event.getBufferBuilder(), false);
        }
    }

    @Listener
    public void setOpaqueCube(EventSetOpaqueCube event) {
        event.setCanceled(true);
    }

    @Listener
    public void onUIListValueChanged(EventUIListValueChanged event) {
        this.updateRenders();
    }

    public void updateRenders() {

        final Minecraft mc = Minecraft.getMinecraft();
        mc.renderGlobal.markBlockRangeForRenderUpdate(
                (int) mc.player.posX - 256,
                (int) mc.player.posY - 256,
                (int) mc.player.posZ - 256,
                (int) mc.player.posX + 256,
                (int) mc.player.posY + 256,
                (int) mc.player.posZ + 256);
    }

    public boolean contains(Block block) {
        return this.blocks.getValue().contains(block);
    }

    public void add(int id) {
        final Block blockFromID = Block.getBlockById(id);
        if (!contains(blockFromID)) {
            this.blocks.getValue().add(blockFromID);
        }
    }

    public void add(String name) {
        final Block blockFromName = Block.getBlockFromName(name);
        if (blockFromName != null) {
            if (!contains(blockFromName)) {
                this.blocks.getValue().add(blockFromName);
            }
        }
    }

    public void remove(int id) {
        for (Block block : this.blocks.getValue()) {
            final int blockID = Block.getIdFromBlock(block);
            if (blockID == id) {
                this.blocks.getValue().remove(block);
                break;
            }
        }
    }

    public void remove(String name) {
        final Block blockFromName = Block.getBlockFromName(name);
        if (blockFromName != null) {
            if (contains(blockFromName)) {
                this.blocks.getValue().remove(blockFromName);
            }
        }
    }

    public int clear() {
        final int count = this.blocks.getValue().size();
        this.blocks.getValue().clear();
        return count;
    }

}
