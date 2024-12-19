package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.util.StringUtil;
import net.wheel.cutils.crack;

public final class VClipCommand extends Command {

    public VClipCommand() {
        super("VClip", new String[] { "VC", "VerticalClip", "Up", "Down" }, "Allows you to teleport vertically",
                "VClip <Amount>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 2)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        if (StringUtil.isDouble(split[1])) {
            final double num = Double.parseDouble(split[1]);

            if (Minecraft.getMinecraft().player.getRidingEntity() != null) {
                Minecraft.getMinecraft().player.getRidingEntity().setPosition(
                        Minecraft.getMinecraft().player.getRidingEntity().posX,
                        Minecraft.getMinecraft().player.getRidingEntity().posY + num,
                        Minecraft.getMinecraft().player.getRidingEntity().posZ);
            } else {
                Minecraft.getMinecraft().player.setPosition(Minecraft.getMinecraft().player.posX,
                        Minecraft.getMinecraft().player.posY + num, Minecraft.getMinecraft().player.posZ);
            }
            crack.INSTANCE.logChat("Teleported you " + ((num > 0) ? "up" : "down") + " " + num);
        } else {
            crack.INSTANCE.errorChat("Unknown number " + "\247f\"" + split[1] + "\"");
        }
    }
}
