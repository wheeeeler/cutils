package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;

public final class SpectateCommand extends Command {

    public SpectateCommand() {
        super("Spectate", new String[] { "Spec" }, "Allows you to spectate nearby players", "Spectate <Username>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 2)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        EntityPlayer target = null;

        for (Entity e : Minecraft.getMinecraft().world.loadedEntityList) {
            if (e != null) {
                if (e instanceof EntityPlayer && e.getName().equalsIgnoreCase(split[1])) {
                    target = (EntityPlayer) e;
                    break;
                }
            }
        }

        if (target != null) {
            crack.INSTANCE.logChat("nigger spectating " + target.getName());
            Minecraft.getMinecraft().setRenderViewEntity(target);
        } else {
            crack.INSTANCE.errorChat("\"" + split[1] + "\" is not within range");
        }
    }

}
