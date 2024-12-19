package net.wheel.cutils.impl.module.GLOBAL;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;

import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

public final class FakePlayerModule extends Module {

    public final Value<String> username = new Value<String>("Username", new String[] { "name", "uname", "u" },
            "The username of the fake player", "asda");
    private final Minecraft mc = Minecraft.getMinecraft();
    private EntityOtherPlayerMP entity;

    public FakePlayerModule() {
        super("FakePlayer", new String[] { "FakeP", "FPlayer" }, "Adds a fake player to your game", "NONE", -1,
                ModuleType.GLOBAL);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.player != null && mc.world != null) {
            entity = new EntityOtherPlayerMP(mc.world, new GameProfile(mc.player.getUniqueID(), username.getValue()));
            entity.copyLocationAndAnglesFrom(mc.player);
            entity.inventory.copyInventory(mc.player.inventory);
            mc.world.addEntityToWorld(6942069, entity);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (mc.world != null) {
            if (entity != null) {
                mc.world.removeEntity(entity);
            }
        }
    }
}
