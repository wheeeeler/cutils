package net.wheel.cutils.impl.command;

import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.math.RayTraceResult;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.event.render.EventRender2D;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class PeekCommand extends Command {

    private String entity;

    public PeekCommand() {
        super("Peek", new String[] { "Pk" }, "Allows you to see inside shulker boxes without having to place them",
                "Peek <Username>\nPeek");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 1, 2)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        if (split.length > 1) {
            if (!this.clamp(input, 2, 2)) {
                this.printUsage();
                return;
            }
            this.entity = split[1];
        }

        try {
            crack.INSTANCE.getEventManager().addEventListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void render(EventRender2D event) {
        try {
            final Minecraft mc = Minecraft.getMinecraft();

            ItemStack stack = null;

            if (this.entity != null) {
                EntityPlayer target = null;

                for (Entity e : mc.world.loadedEntityList) {
                    if (e != null) {
                        if (e instanceof EntityPlayer && e.getName().equalsIgnoreCase(this.entity)) {
                            target = (EntityPlayer) e;
                        }
                    }
                }

                if (target != null) {
                    stack = getHeldShulker(target);

                    if (stack == null) {
                        crack.INSTANCE.errorChat("\"" + target.getName() + "\" is not holding a shulker box");
                        this.entity = null;
                        crack.INSTANCE.getEventManager().removeEventListener(this);
                        return;
                    }
                } else {
                    crack.INSTANCE.errorChat("\"" + this.entity + "\" is not within range");
                }
                this.entity = null;
            } else {
                final RayTraceResult ray = mc.objectMouseOver;

                if (ray != null) {
                    if (ray.entityHit != null) {
                        if (ray.entityHit instanceof EntityItemFrame) {
                            final EntityItemFrame itemFrame = (EntityItemFrame) ray.entityHit;
                            if (!itemFrame.getDisplayedItem().isEmpty()
                                    && itemFrame.getDisplayedItem().getItem() instanceof ItemShulkerBox) {
                                stack = itemFrame.getDisplayedItem();
                            } else {
                                stack = getHeldShulker(mc.player);
                            }
                        }
                    } else {
                        stack = getHeldShulker(mc.player);
                    }
                } else {
                    stack = getHeldShulker(mc.player);
                }
            }

            if (stack != null) {
                final Item item = stack.getItem();

                if (item instanceof ItemShulkerBox) {
                    if (Block.getBlockFromItem(item) instanceof BlockShulkerBox) {
                        final NBTTagCompound tag = stack.getTagCompound();
                        if (tag != null && tag.hasKey("BlockEntityTag", 10)) {
                            final NBTTagCompound entityTag = tag.getCompoundTag("BlockEntityTag");

                            final TileEntityShulkerBox te = new TileEntityShulkerBox();
                            te.setWorld(mc.world);
                            te.readFromNBT(entityTag);
                            mc.displayGuiScreen(new GuiShulkerBox(mc.player.inventory, te));
                        } else {
                            crack.INSTANCE.errorChat("This shulker box is empty");
                        }
                    }

                } else {
                    crack.INSTANCE.errorChat("Please hold a shulker box");
                }
            } else {
                crack.INSTANCE.errorChat("Please hold a shulker box");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        crack.INSTANCE.getEventManager().removeEventListener(this);
    }

    private ItemStack getHeldShulker(EntityPlayer entity) {
        if (!entity.getHeldItemMainhand().isEmpty()
                && entity.getHeldItemMainhand().getItem() instanceof ItemShulkerBox) {
            return entity.getHeldItemMainhand();
        }
        if (!entity.getHeldItemOffhand().isEmpty() && entity.getHeldItemOffhand().getItem() instanceof ItemShulkerBox) {
            return entity.getHeldItemOffhand();
        }
        return null;
    }

}
