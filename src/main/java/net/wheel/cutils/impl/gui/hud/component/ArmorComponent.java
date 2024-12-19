package net.wheel.cutils.impl.gui.hud.component;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import net.wheel.cutils.api.gui.hud.component.DraggableHudComponent;
import net.wheel.cutils.impl.gui.hud.GuiHudEditor;

public final class ArmorComponent extends DraggableHudComponent {

    private static final int ITEM_SIZE = 18;

    public ArmorComponent() {
        super("EquippedArmor");
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        boolean isInHudEditor = mc.currentScreen instanceof GuiHudEditor;
        int itemSpacingWidth = 0;
        boolean playerHasArmor = false;

        if (mc.player != null) {
            for (int i = 0; i <= 3; i++) {
                final ItemStack stack = mc.player.inventoryContainer.getSlot(8 - i).getStack();
                if (!stack.isEmpty()) {
                    GlStateManager.pushMatrix();
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(stack, (int) this.getX() + itemSpacingWidth,
                            (int) this.getY());
                    mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, (int) this.getX() + itemSpacingWidth,
                            (int) this.getY());
                    RenderHelper.disableStandardItemLighting();
                    GlStateManager.popMatrix();
                    itemSpacingWidth += ITEM_SIZE;
                    playerHasArmor = true;
                }
            }
        }

        if (!playerHasArmor) {
            if (isInHudEditor) {
                mc.fontRenderer.drawString("(armor)", (int) this.getX(), (int) this.getY(), 0xFFAAAAAA);
                itemSpacingWidth = ITEM_SIZE * 4;
            } else {
                this.setW(0);
                this.setH(0);
                this.setEmptyH(16);
                return;
            }
        }

        this.setW(itemSpacingWidth);
        this.setH(16);
    }

}
