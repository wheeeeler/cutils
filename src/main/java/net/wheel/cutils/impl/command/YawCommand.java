package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.util.StringUtil;
import net.wheel.cutils.crack;

public final class YawCommand extends Command {

    public YawCommand() {
        super("Yaw", new String[] { "Yw" }, "Allows you to set your yaw", "Yaw <Number>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 2)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        if (StringUtil.isDouble(split[1])) {
            final float num = Float.parseFloat(split[1]);

            Minecraft.getMinecraft().player.rotationYaw = num;

            if (Minecraft.getMinecraft().player.getRidingEntity() != null) {
                Minecraft.getMinecraft().player.getRidingEntity().rotationYaw = num;
            }

            crack.INSTANCE.logChat("Set yaw to " + num);
        } else {
            crack.INSTANCE.errorChat("Unknown number " + "\247f\"" + split[1] + "\"");
        }
    }
}
