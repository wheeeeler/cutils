package net.wheel.cutils.impl.management;

import java.lang.reflect.Field;
import java.util.Comparator;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.util.StringUtil;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.module.CRACK.*;
import net.wheel.cutils.impl.module.GLOBAL.*;
import net.wheel.cutils.impl.module.LOCAL.*;
import net.wheel.cutils.impl.module.MISC.*;
import net.wheel.cutils.impl.module.MVMT.*;
import net.wheel.cutils.impl.module.RENDER.*;
import net.wheel.cutils.impl.module.WAR.*;
import net.wheel.cutils.impl.module.hidden.*;
import net.wheel.cutils.impl.module.ui.CrackHudModule;

@Getter
@Setter
public final class ModuleManager {

    private ObjectArrayList<Module> moduleList = new ObjectArrayList<Module>();

    public ModuleManager() {
        add(new KeybindsModule());
        add(new CommandsModule());
        add(new HudModule());
        add(new ArrayListModule());
        add(new OverlayModule());
        add(new AntiCollisionModule());
        add(new BlinkModule());
        add(new XrayModule());
        add(new AntiSlowModule());
        add(new AntiFlinchModule());
        add(new JesusModule());
        add(new AutoPaintModule());
        add(new RenderTweaks());
        add(new InventoryExtenderModule());
        add(new AutoSprintModule());
        add(new CoordLoggerModule());
        add(new KnockbackModule());
        add(new AntiRotationModule());
        add(new TimerModule());
        add(new CancerChatModule());
        add(new RespawnModule());
        add(new NoFallModule());
        add(new NoSwingModule());
        add(new EntityRenderModule());
        add(new SneakModule());
        add(new FullbrightModule());
        add(new AutoReconnectModule());
        add(new AutoFishModule());
        add(new InteractModule());
        add(new TracerModule());
        add(new ChamsModule());
        add(new FastPlaceModule());
        add(new FastBreakModule());
        add(new AutoToolModule());
        add(new AntiBreakAnimationModule());
        add(new FreecamModule());
        add(new EntityCommanderModule());
        add(new SafeWalkModule());
        add(new PhaseModule());
        add(new FlightModule());
        add(new AntiHungerModule());
        add(new CrystalAuraModule());
        add(new BowModule());
        add(new WeaponModule());
        add(new CrackAuraModule());
        add(new RegenModule());
        add(new AutoArmorModule());
        add(new CriticalsModule());
        add(new HandSpoofModule());
        add(new AutoWalkModule());
        add(new PacketLagModule());
        add(new MacroModule());
        add(new AutoBreedModule());
        add(new MovementSpeedModule());
        add(new AntiVoidModule());
        add(new ChunkGenModule());
        add(new AntiCrystalModule());
        add(new ContainerESPModule());
        add(new AutoDisconnectModule());
        add(new AntiChunkModule());
        add(new TrajectoryModule());
        add(new SolidFluidModule());
        add(new AntiAfkModule());
        add(new DesyncFixModule());
        add(new NukeModule());
        add(new AutoSignModule());
        add(new IgnoreModule());
        add(new StepModule());
        add(new ViewClipModule());
        add(new AntiBiomeColorModule());
        add(new PacketLogModule());
        add(new BuildHeightModule());
        add(new BlockHighlightModule());
        add(new CrackHudModule());
        add(new StrafeModule());
        add(new PortalModule());
        add(new ShulkerViewModule());
        add(new AutoStockerModule());
        add(new ReticleModule());
        add(new EntityDesyncModule());
        add(new PacketModule());
        add(new AutoFarmModule());
        add(new AntiEffectModules());
        add(new AntiEntityTrace());
        add(new MultitaskModule());
        add(new EnderChestModule());
        add(new ItemFinderModule());
        add(new AutoFeederModule());
        add(new ReachModule());
        add(new WitherBuilderModule());
        add(new AutoClickerModule());
        add(new FakePlayerModule());
        add(new AmbiTweakerModule());
        add(new AntiNotification());
        add(new BoundaryModule());
        add(new ChatGameResolverModule());
        add(new AutoPeripheralModule());
        add(new AutoGamblerModule());
        add(new ChatTweaksModule());
        add(new MuteModule());
        add(new FakeAnglesModule());
        add(new ZoomModule());
        add(new AntiInventoryModule());
        add(new EventPacketManipulationModule());

        if (crack.INSTANCE.getCapeManager().hasCape())
            add(new CapeModule());

        moduleList.sort(Comparator.comparing(Module::getDisplayName));
    }

    public void unload() {
        for (Module mod : this.moduleList) {
            mod.onDisable();
            mod.unload();
        }
        this.moduleList.clear();
    }

    public void add(Module mod) {
        try {
            for (Field field : mod.getClass().getDeclaredFields()) {
                if (Value.class.isAssignableFrom(field.getType())) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    final Value val = (Value) field.get(mod);
                    mod.getValueList().add(val);
                }
            }
            this.moduleList.add(mod);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Module getModuleByName(String name) {
        return find(name);
    }

    public Module find(String alias) {
        for (Module mod : this.getModuleList()) {
            if (alias.equalsIgnoreCase(mod.getDisplayName())) {
                return mod;
            }

            if (mod.getAlias() != null && mod.getAlias().length > 0) {
                for (String s : mod.getAlias()) {
                    if (alias.equalsIgnoreCase(s)) {
                        return mod;
                    }
                }
            }
        }
        return null;
    }

    public Module find(Class clazz) {
        for (Module mod : this.getModuleList()) {
            if (mod.getClass() == clazz) {
                return mod;
            }
        }
        return null;
    }

    public Module findSimilar(String input) {
        Module mod = null;
        double similarity = 0.0f;

        for (Module m : this.getModuleList()) {
            final double currentSimilarity = StringUtil.levenshteinDistance(input, m.getDisplayName());
            if (currentSimilarity >= similarity) {
                similarity = currentSimilarity;
                mod = m;
            }
        }
        return mod;
    }

    public ObjectArrayList<Module> getModuleList(Module.ModuleType type) {
        ObjectArrayList<Module> list = new ObjectArrayList<>();
        for (Module module : moduleList) {
            if (module.getType().equals(type)) {
                list.add(module);
            }
        }
        return list;
    }
}
