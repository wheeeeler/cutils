package net.wheel.cutils.impl.command;

import net.minecraft.client.Minecraft;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.util.StringUtil;
import net.wheel.cutils.crack;

public final class PitchCommand extends Command {

    public PitchCommand() {
        super("Pitch", new String[] { "Pch" }, "Allows you to set your pitch", "Pitch <Number>");
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

            Minecraft.getMinecraft().player.rotationPitch = num;
            if (Minecraft.getMinecraft().player.getRidingEntity() != null) {
                Minecraft.getMinecraft().player.getRidingEntity().rotationPitch = num;
            }

            crack.INSTANCE.logChat("Set pitch to " + num);
        } else {
            crack.INSTANCE.errorChat("Unknown number " + "\247f\"" + split[1] + "\"");
        }
    }
}
