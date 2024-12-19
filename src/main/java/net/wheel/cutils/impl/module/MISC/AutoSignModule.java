package net.wheel.cutils.impl.module.MISC;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.util.text.TextComponentString;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.minecraft.EventDisplayGui;
import net.wheel.cutils.api.event.network.EventSendPacket;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AutoSignModule extends Module {

    public final Value<Boolean> overflow = new Value<>("Overflow", new String[] { "Ov" },
            "Fill the sign with the maximum number of randomly generated characters", false);
    private String[] lines;

    public AutoSignModule() {
        super("AutoSign", new String[] { "AutomaticSign", "ASign" }, "Automatically writes text on signs for you",
                "NONE", -1, ModuleType.MISC);
    }

    @Listener
    public void onToggle() {
        super.onToggle();
        this.lines = null;
    }

    @Listener
    public void displayGui(EventDisplayGui event) {
        if (event.getScreen() instanceof GuiEditSign) {
            final GuiEditSign gui = (GuiEditSign) event.getScreen();

            final boolean shouldCancel = this.overflow.getValue() || this.lines != null;

            if (shouldCancel && gui != null && gui.tileSign != null) {
                Minecraft.getMinecraft().player.connection.sendPacket(new CPacketUpdateSign(
                        gui.tileSign.getPos(),
                        new TextComponentString[] {
                                new TextComponentString(""),
                                new TextComponentString(""),
                                new TextComponentString(""),
                                new TextComponentString("")
                        }));
                Minecraft.getMinecraft().displayGuiScreen(null);
                event.setCanceled(true);
            }
        }
    }

    @Listener
    public void sendPacket(EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof CPacketUpdateSign) {
                final CPacketUpdateSign packet = (CPacketUpdateSign) event.getPacket();

                if (this.overflow.getValue()) {
                    Random random = new Random();
                    StringBuilder sb = new StringBuilder(4 * 384);

                    int totalLength = 4 * 384;
                    int i = 0;
                    while (i < totalLength) {
                        int codePoint = random.nextInt(0x10FFFF - 0x80 - 0x800) + 0x80;
                        if (codePoint >= 0xD800) {
                            codePoint += 0x800;
                        }
                        if (Character.isDefined(codePoint) && !Character.isISOControl(codePoint)) {
                            sb.appendCodePoint(codePoint);
                            i++;
                        }
                    }
                    String line = sb.toString();
                    for (int j = 0; j < 4; j++) {
                        packet.lines[j] = line.substring(j * 384, (j + 1) * 384);
                    }
                } else {
                    if (this.lines == null) {
                        this.lines = packet.getLines();
                        crack.INSTANCE.logChat("Sign text set");
                    } else {
                        packet.lines = this.lines;
                    }
                }
            }
        }
    }
}
