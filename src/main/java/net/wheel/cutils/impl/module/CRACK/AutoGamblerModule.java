package net.wheel.cutils.impl.module.CRACK;

import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.util.Timer;
import net.wheel.cutils.api.value.Value;

public final class AutoGamblerModule extends Module {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public final Value<Integer> gambleAmountSetting = new Value<>("Amount", new String[] { "Amt" }, "Amount to gamble",
            10000, 1, 10000, 1);
    public final Value<Integer> gambleCoolDownSetting = new Value<>("Cooldown", new String[] { "CD" },
            "Cooldown in seconds", 300, 1, 600, 1);
    public final Value<Boolean> gambleCancelEvent = new Value<>("Cancel GuiOpen", new String[] { "Cancel" },
            "Will destroy inventory", false);

    private boolean isEnabled = false;
    private boolean cancelGUI = false;
    private boolean guiOpen = false;
    private final Timer timer = new Timer();

    public AutoGamblerModule() {
        super("AutoGambler", new String[] { "AutoGamble" }, "gamba", "NONE", -1, ModuleType.CRACK);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.world == null || mc.player == null) {
            isEnabled = false;
            this.setEnabled(false);
        } else {
            isEnabled = true;
            timer.reset();
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        isEnabled = false;
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!isEnabled || mc.world == null || mc.player == null) {
            return;
        }

        if (timer.passed(gambleCoolDownSetting.getValue() * 1000)) {
            cancelGUI = true;
            String message = "/gamble " + gambleAmountSetting.getValue();
            CPacketChatMessage packet = new CPacketChatMessage(message);
            Objects.requireNonNull(mc.getConnection()).sendPacket(packet);
            timer.reset();
        } else if (guiOpen) {
            guiOpen = false;
            cancelGUI = false;
            mc.player.closeScreen();
        }
    }

    @SubscribeEvent
    public void onGambleGui(GuiOpenEvent event) {
        if (isEnabled() && cancelGUI) {
            if (event.getGui() instanceof GuiChest) {
                if (gambleCancelEvent.getValue()) {
                    event.setCanceled(true);
                } else {
                    guiOpen = true;
                }
            }
        }
    }
}
