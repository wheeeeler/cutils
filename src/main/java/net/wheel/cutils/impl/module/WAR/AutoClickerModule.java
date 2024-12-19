package net.wheel.cutils.impl.module.WAR;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import net.wheel.cutils.api.event.minecraft.EventRunTick;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.util.Timer;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AutoClickerModule extends Module {

    private final Value<Float> clicksPerSecond = new Value<Float>("Speed", new String[] { "Speed", "S" },
            "Clicks per second to automatically click", 8.0f, 1.0f, 15.0f, 0.1f);
    private final Value<Boolean> randomize = new Value<Boolean>("Randomize", new String[] { "Random", "R" },
            "Randomizes the clicks per second", true);
    private final Value<Boolean> onlyWeapons = new Value<Boolean>("OnlyWeapons", new String[] { "OnlyWep", "OW" },
            "When enabled, will only auto click with chosen held weapons", true);
    private final Value<List<Item>> weapons = new Value<List<Item>>("Weapons",
            new String[] { "Wep", "Items", "WI", "I", "W" }, "Choose which items to automatically click with");

    private final Timer timer = new Timer();

    public AutoClickerModule() {
        super("AutoClicker", new String[] { "AutoClick", "Clicker", "AutoAttack" },
                "Automatically clicks the mouse while mouse is held down", "NONE", -1, ModuleType.WAR);
        this.weapons.setValue(new ArrayList<>());
        this.weapons.getValue().add(Items.DIAMOND_SWORD);
        this.weapons.getValue().add(Items.IRON_SWORD);
        this.weapons.getValue().add(Items.GOLDEN_SWORD);
        this.weapons.getValue().add(Items.STONE_SWORD);
        this.weapons.getValue().add(Items.WOODEN_SWORD);
    }

    @Listener
    public void onRunTick(EventRunTick event) {
        if (Minecraft.getMinecraft().inGameHasFocus && Minecraft.getMinecraft().currentScreen == null
                && Minecraft.getMinecraft().player != null)
            if (Mouse.isButtonDown(0)) {
                if (this.onlyWeapons.getValue()) {
                    if (!this.weapons.getValue()
                            .contains(Minecraft.getMinecraft().player.getHeldItemMainhand().getItem())) {
                        return;
                    }
                }
                double cps = generateCPS();
                if (this.timer.passed(1000.0f / cps)) {
                    KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindAttack.getKeyCode(), true);
                    KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindAttack.getKeyCode());
                    this.timer.reset();
                } else {
                    KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindAttack.getKeyCode(), false);
                }
            }
    }

    private double generateCPS() {
        double cps;
        if (this.randomize.getValue()) {
            double randomNumber = ThreadLocalRandom.current().nextDouble(-2.0D, 2.0D);
            cps = this.clicksPerSecond.getValue() + (float) randomNumber;
        } else {
            cps = this.clicksPerSecond.getValue();
        }
        return Math.abs(cps);
    }
}
