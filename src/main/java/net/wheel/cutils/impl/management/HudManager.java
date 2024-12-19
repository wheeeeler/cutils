package net.wheel.cutils.impl.management;

import java.lang.reflect.Field;
import java.util.Comparator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.event.render.EventRender2D;
import net.wheel.cutils.api.gui.hud.component.HudComponent;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.gui.hud.anchor.AnchorPoint;
import net.wheel.cutils.impl.gui.hud.component.*;
import net.wheel.cutils.impl.gui.hud.component.graph.FpsGraphComponent;
import net.wheel.cutils.impl.gui.hud.component.graph.MovementGraphComponent;
import net.wheel.cutils.impl.gui.hud.component.graph.TpsGraphComponent;
import net.wheel.cutils.impl.gui.hud.component.module.ModuleListComponent;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

@Setter
public final class HudManager {

    private ObjectList<HudComponent> componentList = new ObjectArrayList<>();
    @Getter
    private ObjectList<AnchorPoint> anchorPoints = new ObjectArrayList<>();

    public HudManager() {
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        final AnchorPoint TOP_LEFT = new AnchorPoint(AnchorPoint.Point.TOP_LEFT);
        final AnchorPoint TOP_RIGHT = new AnchorPoint(AnchorPoint.Point.TOP_RIGHT);
        final AnchorPoint BOTTOM_LEFT = new AnchorPoint(AnchorPoint.Point.BOTTOM_LEFT);
        final AnchorPoint BOTTOM_RIGHT = new AnchorPoint(AnchorPoint.Point.BOTTOM_RIGHT);
        final AnchorPoint TOP_CENTER = new AnchorPoint(AnchorPoint.Point.TOP_CENTER);
        final AnchorPoint BOTTOM_CENTER = new AnchorPoint(AnchorPoint.Point.BOTTOM_CENTER);
        this.anchorPoints.add(TOP_LEFT);
        this.anchorPoints.add(TOP_RIGHT);
        this.anchorPoints.add(BOTTOM_LEFT);
        this.anchorPoints.add(BOTTOM_RIGHT);
        this.anchorPoints.add(TOP_CENTER);
        this.anchorPoints.add(BOTTOM_CENTER);

        for (AnchorPoint anchorPoint : this.anchorPoints)
            anchorPoint.updatePosition(sr);

        int moduleListXOffset = 0;
        int moduleListYOffset = 0;
        for (Module.ModuleType type : Module.ModuleType.values()) {
            if (type.equals(Module.ModuleType.HIDDEN) || type.equals(Module.ModuleType.UI))
                continue;

            final ModuleListComponent moduleList = new ModuleListComponent(type);
            if ((moduleList.getX() + moduleListXOffset) > sr.getScaledWidth()) {
                moduleListXOffset = 0;
                moduleListYOffset += (int) (moduleList.getH() + 4);
            }

            moduleList.setX(moduleList.getX() + moduleListXOffset);
            if (moduleListYOffset != 0) {
                moduleList.setY(moduleList.getY() + moduleListYOffset);
            }

            add(moduleList);

            moduleListXOffset += (int) (moduleList.getW() + 4);
        }

        add(new ParticlesComponent());
        add(new CUTagComponent());
        add(new CrackListComponent(TOP_RIGHT));
        add(new TpsComponent());
        add(new FpsComponent());
        add(new CoordsComponent());
        add(new ChunkComponent());
        add(new SpeedComponent());
        add(new ArmorComponent());
        add(new PingComponent());
        add(new BiomeComponent());
        add(new DirectionComponent());
        add(new PacketTimeComponent());
        add(new TimeComponent());
        add(new HomeComponent());
        add(new InventoryComponent());
        add(new PlayerCountComponent());
        add(new OverViewComponent());
        add(new RearViewComponent());
        add(new EntityListComponent());
        add(new TpsGraphComponent());
        add(new MovementGraphComponent());
        add(new PaletteComponent());
        add(new FpsGraphComponent());
        add(new KeyboardComponent());
        add(new RegionComponent());
        add(new ItemInfoComponent());
        add(new RelayConnectionComponent());

        NotificationsComponent notificationsComponent = new NotificationsComponent();
        notificationsComponent.setAnchorPoint(anchorPoints.get(4));
        add(notificationsComponent);

        componentList.sort(Comparator.comparing(HudComponent::getName));

        crack.INSTANCE.getEventManager().addEventListener(this);
    }

    public void add(HudComponent component) {
        try {
            for (Field field : component.getClass().getDeclaredFields()) {
                if (Value.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    final Value val = (Value) field.get(component);
                    component.getValueList().add(val);
                }
            }
            componentList.add(component);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void onRender(EventRender2D event) {
        final Minecraft mc = Minecraft.getMinecraft();
        final int chatHeight = (mc.currentScreen instanceof GuiChat) ? 14 : 0;

        for (AnchorPoint point : anchorPoints) {
            switch (point.getPoint()) {
                case TOP_LEFT:
                    point.setX(2);
                    point.setY(2);
                    break;
                case TOP_RIGHT:
                    point.setX(event.getScaledResolution().getScaledWidth() - 2);
                    point.setY(2);
                    break;
                case BOTTOM_LEFT:
                    point.setX(2);
                    point.setY(event.getScaledResolution().getScaledHeight() - chatHeight - 2);
                    break;
                case BOTTOM_RIGHT:
                    point.setX(event.getScaledResolution().getScaledWidth() - 2);
                    point.setY(event.getScaledResolution().getScaledHeight() - chatHeight - 2);
                    break;
                case TOP_CENTER:
                    point.setX(event.getScaledResolution().getScaledWidth() / 2.0f);
                    point.setY(2);
                    break;
                case BOTTOM_CENTER:
                    point.setX(event.getScaledResolution().getScaledWidth() / 2.0f);
                    point.setY(event.getScaledResolution().getScaledHeight() - 2);
                    break;
            }
        }
    }

    public void moveToTop(HudComponent component) {
        componentList.remove(component);
        componentList.add(component);
    }

    public void unload() {
        anchorPoints.clear();
        componentList.clear();
        crack.INSTANCE.getEventManager().removeEventListener(this);
    }

    public AnchorPoint findPoint(AnchorPoint.Point point) {
        for (AnchorPoint anchorPoint : anchorPoints) {
            if (anchorPoint.getPoint() == point) {
                return anchorPoint;
            }
        }
        return null;
    }

    public HudComponent findComponent(String componentName) {
        for (HudComponent component : componentList) {
            if (componentName.equalsIgnoreCase(component.getName())) {
                return component;
            }
        }
        return null;
    }

    public HudComponent findComponent(Class<?> componentClass) {
        for (HudComponent component : componentList) {
            if (component.getClass() == componentClass) {
                return component;
            }
        }
        return null;
    }

    public ObjectList<HudComponent> getComponentList() {
        componentList.sort(Comparator.comparing(HudComponent::getName));
        return componentList;
    }

}
