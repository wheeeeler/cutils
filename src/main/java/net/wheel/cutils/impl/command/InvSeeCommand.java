package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.event.render.EventRender2D;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class InvSeeCommand extends Command {

    private String entity;

    public InvSeeCommand() {
        super("InvSee", new String[] { "InventorySee" }, "Allows you to see another players inventory",
                "InvSee <Player>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 2)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        try {
            this.entity = split[1];
            crack.INSTANCE.getEventManager().addEventListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void render(EventRender2D event) {
        try {
            final Minecraft mc = Minecraft.getMinecraft();

            EntityPlayer player = null;

            for (Entity e : mc.world.loadedEntityList) {
                if (e != null && e instanceof EntityPlayer) {
                    if (e.getName().equalsIgnoreCase(this.entity)) {
                        player = (EntityPlayer) e;
                        break;
                    }
                }
            }

            if (player != null) {
                mc.displayGuiScreen(new GuiInventory(player));
            } else {
                crack.INSTANCE.errorChat("\"" + this.entity + "\" is not within range");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        crack.INSTANCE.getEventManager().removeEventListener(this);
    }

}
