package net.wheel.cutils.impl.module.RENDER;

import org.lwjgl.input.Mouse;

import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.NonNullList;

import net.wheel.cutils.api.event.gui.EventRenderTooltip;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.util.RenderUtil;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class ShulkerViewModule extends Module {

    public final Value<Boolean> middleClick = new Value("MiddleClick", new String[] { "MC", "Mid" },
            "Allows you to middle click shulkers and view their contents", true);

    private boolean clicked;

    public ShulkerViewModule() {
        super("ShulkerView", new String[] { "SPreview", "ShulkerView" },
                "Hover over a shulker box to view the items inside", "NONE", -1, ModuleType.RENDER);
    }

    @Listener
    public void onRenderTooltip(EventRenderTooltip event) {
        if (event.getItemStack() == null)
            return;

        final Minecraft mc = Minecraft.getMinecraft();

        if (event.getItemStack().getItem() instanceof ItemShulkerBox) {
            ItemStack shulker = event.getItemStack();
            NBTTagCompound tagCompound = shulker.getTagCompound();
            if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10)) {
                NBTTagCompound blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag");
                if (blockEntityTag.hasKey("Items", 9)) {
                    event.setCanceled(true);

                    NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
                    ItemStackHelper.loadAllItems(blockEntityTag, nonnulllist);

                    int x = event.getX();
                    int y = event.getY();

                    GlStateManager.translate(x + 10, y - 5, 0);

                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();

                    RenderUtil.drawRect(-3, -mc.fontRenderer.FONT_HEIGHT - 4, 9 * 16 + 3, 3 * 16 + 3, 0x99101010);
                    RenderUtil.drawRect(-2, -mc.fontRenderer.FONT_HEIGHT - 3, 9 * 16 + 2, 3 * 16 + 2, 0xFF202020);
                    RenderUtil.drawRect(0, 0, 9 * 16, 3 * 16, 0xFF101010);

                    mc.fontRenderer.drawStringWithShadow(shulker.getDisplayName(), 0, -mc.fontRenderer.FONT_HEIGHT - 1,
                            0xFFFFFFFF);

                    GlStateManager.enableDepth();
                    mc.getRenderItem().zLevel = 150.0F;
                    RenderHelper.enableGUIStandardItemLighting();

                    for (int i = 0; i < nonnulllist.size(); i++) {
                        ItemStack itemStack = nonnulllist.get(i);
                        int offsetX = (i % 9) * 16;
                        int offsetY = (i / 9) * 16;
                        mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, offsetX, offsetY);
                        mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, offsetX, offsetY, null);
                    }

                    RenderHelper.disableStandardItemLighting();
                    mc.getRenderItem().zLevel = 0.0F;
                    GlStateManager.enableLighting();

                    GlStateManager.translate(-(x + 10), -(y - 5), 0);
                }
            }

            if (this.middleClick.getValue()) {
                if (Mouse.isButtonDown(2)) {
                    if (!this.clicked) {
                        final BlockShulkerBox shulkerBox = (BlockShulkerBox) Block.getBlockFromItem(shulker.getItem());
                        if (shulkerBox != null) {
                            final NBTTagCompound tag = shulker.getTagCompound();
                            if (tag != null && tag.hasKey("BlockEntityTag", 10)) {
                                final NBTTagCompound entityTag = tag.getCompoundTag("BlockEntityTag");

                                final TileEntityShulkerBox te = new TileEntityShulkerBox();
                                te.setWorld(mc.world);
                                te.readFromNBT(entityTag);
                                mc.displayGuiScreen(new GuiShulkerBox(mc.player.inventory, te));
                            }
                        }
                    }
                    this.clicked = true;
                } else {
                    this.clicked = false;
                }
            }
        }
    }
}
