package net.wheel.cutils.impl.module.LOCAL;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketEntityEffect;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventReceivePacket;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AntiEffectModules extends Module {

    public final Value<Boolean> nausea = new Value<Boolean>("Nausea", new String[] { "naus", "nau", "n" },
            "Disables the nausea potion effect", true);
    public final Value<Boolean> blindness = new Value<Boolean>("Blindness", new String[] { "blind", "b" },
            "Disables the blindness potion effect", true);
    public final Value<Boolean> invisibility = new Value<Boolean>("Invisibility", new String[] { "invis", "inv", "i" },
            "Disables the invisibility potion effect", false);

    public final Value<Boolean> wither = new Value<Boolean>("Wither", new String[] { "wit", "w" },
            "Disables the withering effect", false);
    public final Value<Boolean> levitation = new Value<Boolean>("Levitation", new String[] { "lev", "l" },
            "Disables the levitation effect", false);

    public AntiEffectModules() {
        super("AntiPotionEffect", new String[] { "AntiEffects", "NoEff", "AntiEff" },
                "Removes potion effects from the player", "NONE", -1, ModuleType.LOCAL);
    }

    @Listener
    public void receivePacket(EventReceivePacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof SPacketEntityEffect) {
                final SPacketEntityEffect packet = (SPacketEntityEffect) event.getPacket();
                if (Minecraft.getMinecraft().player != null
                        && packet.getEntityId() == Minecraft.getMinecraft().player.getEntityId()) {
                    int effectId = packet.getEffectId();
                    if (this.nausea.getValue() && effectId == 9)
                        event.setCanceled(true);

                    if (this.invisibility.getValue() && effectId == 14)
                        event.setCanceled(true);

                    if (this.blindness.getValue() && effectId == 15)
                        event.setCanceled(true);

                    if (this.wither.getValue() && effectId == 20)
                        event.setCanceled(true);

                    if (this.levitation.getValue() && effectId == 25)
                        event.setCanceled(true);
                }
            }
        }
    }
}
