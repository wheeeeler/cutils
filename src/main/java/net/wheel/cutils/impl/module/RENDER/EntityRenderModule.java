package net.wheel.cutils.impl.module.RENDER;

import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventReceivePacket;
import net.wheel.cutils.api.event.render.EventRender2D;
import net.wheel.cutils.api.event.render.EventRenderName;
import net.wheel.cutils.api.friend.Friend;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.util.*;
import net.wheel.cutils.api.util.Timer;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class EntityRenderModule extends Module {

    public final Value<Mode> mode = new Value<Mode>("Mode", new String[] { "Mode", "M" },
            "The mode of the drawn esp/wallhack", Mode.OPAQUE);
    public final Value<HealthMode> hpMode = new Value<HealthMode>("Hp", new String[] { "Health", "HpMode" },
            "Rendering mode for the health bar", HealthMode.NONE);
    public final Value<PotionsMode> potionsMode = new Value<PotionsMode>("Potions",
            new String[] { "Pot", "Pots", "PotsMode" }, "Rendering mode for active potion-effects on the entity",
            PotionsMode.NONE);
    public final Value<Boolean> distance = new Value<Boolean>("Distance", new String[] { "Dist", "Distance" },
            "Draw the player's distance", true);
    public final Value<Boolean> players = new Value<Boolean>("Players", new String[] { "Player" },
            "Choose to enable on players", true);
    public final Value<Color> playersColor = new Value<Color>("PlayersColor", new String[] { "playerscolor", "pc" },
            "Change the color of players on esp", new Color(255, 68, 68));
    public final Value<Boolean> mobs = new Value<Boolean>("Mobs", new String[] { "Mob" }, "Choose to enable on mobs",
            true);
    public final Value<Color> mobsColor = new Value<Color>("MobsColor", new String[] { "mobscolor", "mc" },
            "Change the color of mobs on esp", new Color(255, 170, 0));
    public final Value<Boolean> animals = new Value<Boolean>("Animals", new String[] { "Animal" },
            "Choose to enable on animals", true);
    public final Value<Color> animalsColor = new Value<Color>("AnimalsColor", new String[] { "animalscolor", "ac" },
            "Change the color of animals on esp", new Color(0, 255, 68));
    public final Value<Boolean> vehicles = new Value<Boolean>("Vehicles", new String[] { "Vehic", "Vehicle" },
            "Choose to enable on vehicles", true);
    public final Value<Color> vehiclesColor = new Value<Color>("VehiclesColor", new String[] { "vehiclescolor", "vc" },
            "Change the color of vehicles on esp", new Color(213, 255, 0));
    public final Value<Boolean> items = new Value<Boolean>("Items", new String[] { "Item" },
            "Choose to enable on items", true);
    public final Value<Color> itemsColor = new Value<Color>("ItemsColor", new String[] { "itemscolor", "ic" },
            "Change the color of items on esp", new Color(0, 255, 170));
    public final Value<Boolean> local = new Value<Boolean>("Player", new String[] { "Self" },
            "Choose to enable on self/local-player", true);
    public final Value<Boolean> crystals = new Value<Boolean>("Crystals",
            new String[] { "crystal", "crystals", "endcrystal", "endcrystals" }, "Choose to enable on end crystals",
            true);
    public final Value<Color> crystalsColor = new Value<Color>("CrystalsColor",
            new String[] { "endercrystalscolor", "endercrystalcolor", "crystalscolor", "crystalcolor", "ecc" },
            "Change the color of ender crystals on esp", new Color(205, 0, 205));
    public final Value<Boolean> pearls = new Value<Boolean>("Pearls", new String[] { "Pearl" },
            "Choose to enable on ender pearls.", true);
    public final Value<Color> pearlsColor = new Value<Color>("PearlsColor",
            new String[] { "enderpearlscolor", "enderpearlcolor", "pearlscolor", "pearlcolor", "epc" },
            "Change the color of ender pearls on esp", new Color(151, 255, 252));
    public final Value<Boolean> armorStand = new Value<Boolean>("ArmorStands",
            new String[] { "ArmorStand", "ArmourStand", "ArmourStands", "ArmStand" },
            "Choose to enable on armor-stands", true);
    public final Value<Boolean> footsteps = new Value<Boolean>("FootSteps", new String[] { "FootStep", "Steps" },
            "Choose to draw entity footsteps", false);
    public final Value<Boolean> owner = new Value<Boolean>("Owner", new String[] { "Owners", "MobOwner" },
            "Choose to draw entity (tame-able or horse) owner name", false);
    public final Value<Boolean> nametag = new Value<Boolean>("Nametag", new String[] { "tag", "tags", "names", "name" },
            "Draw the entity's name tag", true);
    public final Value<Boolean> ping = new Value<Boolean>("Ping", new String[] { "Ms" },
            "Draw the entity's ping (only works on players)", true);
    public final Value<Boolean> Gamemode = new Value<Boolean>("Gamemode", new String[] { "GM" },
            "Draw the entity's gamemode (only works on players)", true);
    public final Value<Boolean> armor = new Value<Boolean>("Armor", new String[] { "Arm" },
            "Draw the entity's equipped armor", true);
    public final Value<Boolean> hearts = new Value<Boolean>("Hearts", new String[] { "Hrts" },
            "Draw the entity's hearts in decimal format", true);
    public final Value<Boolean> absorption = new Value<Boolean>("Absorption", new String[] { "Abs", "GappleHearts" },
            "Adds absorption value to heart display", true);
    public final Value<Boolean> enchants = new Value<Boolean>("Enchants", new String[] { "Ench" },
            "Draw enchant names above the entity's equipped armor. (requires Armor value to be enabled", true);
    public final Value<Color> friendsColor = new Value<Color>("FriendsColor",
            new String[] { "friendscolor", "friendcolor", "fc" }, "Change the color of friendly players on esp",
            new Color(153, 0, 238));
    public final Value<Color> sneakingColor = new Value<Color>("SneakingColor",
            new String[] { "sneakingcolor", "sneakcolor", "sc" }, "Change the color of sneaking players on esp",
            new Color(238, 153, 0));
    public final Value<Boolean> background = new Value<Boolean>("Background", new String[] { "Bg" },
            "Draw a transparent black background behind any text or icon drawn", true);

    private final Timer uuidTimer = new Timer();
    private final ICamera camera = new Frustum();
    private final ResourceLocation inventory = new ResourceLocation("textures/gui/container/inventory.png");
    private final List<FootstepData> footstepDataList = new CopyOnWriteArrayList<>();

    private final Map<UUID, String> cachedMobOwners = new ConcurrentHashMap<>();
    private static final Map<String, Integer> gameModeColors = new ConcurrentHashMap<>();

    static {
        gameModeColors.put("[S]", 0x0bd407);
        gameModeColors.put("[C]", 0xea010e);
        gameModeColors.put("[A]", 0x0b19cc);
        gameModeColors.put("[SP]", 0x9a0fc6);
        gameModeColors.put("[?]", 0xFFFFFFFF);
    }

    public EntityRenderModule() {
        super("EntityRenders", new String[] { "ESP", "Wall-Hack", "Walls", "NameTags", "NameTag", "Name-Tag",
                "Name-Tags", "ExtraSensoryPerception" }, "Highlights entities", "NONE", -1, ModuleType.RENDER);
    }

    @Listener
    public void render2D(EventRender2D event) {
        final Minecraft mc = Minecraft.getMinecraft();

        if (this.footsteps.getValue()) {
            for (FootstepData data : this.footstepDataList) {
                final GLUProjection.Projection projection = GLUProjection.getInstance().project(
                        data.x - mc.getRenderManager().viewerPosX, data.y - mc.getRenderManager().viewerPosY,
                        data.z - mc.getRenderManager().viewerPosZ, GLUProjection.ClampMode.NONE, false);
                if (projection.getType() == GLUProjection.Projection.Type.INSIDE) {
                    mc.fontRenderer.drawStringWithShadow("*step*",
                            (float) projection.getX() - mc.fontRenderer.getStringWidth("*step*") / 2.0f,
                            (float) projection.getY(), -1);
                }

                if (Math.abs(System.currentTimeMillis() - data.getTime()) >= 3000) {
                    this.footstepDataList.remove(data);
                }
            }
        }

        for (Entity e : mc.world.loadedEntityList) {
            if (e != null && this.checkFilter(e)) {
                final float[] bounds = this.convertBounds(e, event.getPartialTicks(),
                        event.getScaledResolution().getScaledWidth(), event.getScaledResolution().getScaledHeight());

                if (bounds != null) {
                    if (this.mode.getValue() == Mode.BOX) {
                        RenderUtil.drawOutlineRect(bounds[0], bounds[1], bounds[2], bounds[3], 1.5f, 0xAA000000);
                        RenderUtil.drawOutlineRect(bounds[0] - 0.5f, bounds[1] - 0.5f, bounds[2] + 0.5f,
                                bounds[3] + 0.5f, 0.5f, this.getColor(e));
                    }

                    String name = StringUtils.stripControlCodes(getNameForEntity(e));
                    String heartsFormatted = "";
                    String pingFormatted = "";
                    String distanceFormatted = "";
                    if (!(e instanceof EntityArmorStand)) {
                        distanceFormatted = String.format("[%.2f m]", Minecraft.getMinecraft().player.getDistance(e));
                    }
                    String gameModeText = "";
                    int gameModeColor = 0xFFFFFFFF;

                    final float centerX = bounds[0] + (bounds[2] - bounds[0]) / 2;
                    int startY = (int) bounds[1] - mc.fontRenderer.FONT_HEIGHT - 2;

                    if (this.distance.getValue() && !distanceFormatted.isEmpty()) {
                        drawCenteredText(distanceFormatted, bounds, startY, 0xFFFFFFFF);
                        startY += mc.fontRenderer.FONT_HEIGHT + 2;
                    }

                    if (this.nametag.getValue()) {
                        int color = -1;
                        Friend friend = crack.INSTANCE.getFriendManager().isFriend(e);
                        if (friend != null) {
                            name = friend.getAlias();
                            color = this.friendsColor.getValue().getRGB();
                        }
                        int nameWidth = mc.fontRenderer.getStringWidth(name);
                        mc.fontRenderer.drawStringWithShadow(name, centerX - nameWidth / 2.0f, startY, color);

                        if (e instanceof EntityPlayer && this.Gamemode.getValue()) {
                            gameModeText = getGameModeText((EntityPlayer) e);
                            gameModeColor = getGameModeColor((EntityPlayer) e);
                            int gameModeWidth = mc.fontRenderer.getStringWidth(gameModeText);
                            float gameModeX = centerX - nameWidth / 2.0f - gameModeWidth - 2;
                            if (this.background.getValue()) {
                                RenderUtil.drawRect(gameModeX - 1, startY - 2, gameModeX + gameModeWidth + 1,
                                        startY + mc.fontRenderer.FONT_HEIGHT - 1, 0x75101010);
                            }
                            mc.fontRenderer.drawStringWithShadow(gameModeText, gameModeX, startY, gameModeColor);
                        }

                        if (e instanceof EntityPlayer && this.ping.getValue()) {
                            pingFormatted = getPlayerPing((EntityPlayer) e);
                            int pingWidth = mc.fontRenderer.getStringWidth(pingFormatted);
                            float pingX = centerX + nameWidth / 2.0f + 2;
                            if (this.background.getValue()) {
                                RenderUtil.drawRect(pingX - 1, startY - 2, pingX + pingWidth + 1,
                                        startY + mc.fontRenderer.FONT_HEIGHT - 1, 0x75101010);
                            }
                            mc.fontRenderer.drawStringWithShadow(pingFormatted, pingX, startY, 0xFFFFFFFF);
                        }
                    }

                    if (this.hearts.getValue() && e instanceof EntityLivingBase) {
                        heartsFormatted = formatHearts((EntityLivingBase) e);
                        startY += mc.fontRenderer.FONT_HEIGHT + 2;
                        drawCenteredText(heartsFormatted, bounds, startY, getHealthColor(e));
                    }
                }
            }
        }
    }

    private void drawCenteredText(String text, float[] bounds, int startY, int color) {
        final Minecraft mc = Minecraft.getMinecraft();
        float centerX = bounds[0] + (bounds[2] - bounds[0]) / 2;
        int textWidth = mc.fontRenderer.getStringWidth(text);
        if (this.background.getValue()) {
            RenderUtil.drawRect(centerX - textWidth / 2.0f - 1, startY - 2, centerX + textWidth / 2.0f + 1,
                    startY + mc.fontRenderer.FONT_HEIGHT - 1, 0x75101010);
        }
        mc.fontRenderer.drawStringWithShadow(text, centerX - textWidth / 2.0f, startY, color);
    }

    private String formatHearts(EntityLivingBase entityLiving) {
        float hearts = entityLiving.getHealth() / 2.0f;
        if (this.absorption.getValue() && entityLiving.getAbsorptionAmount() > 0) {
            hearts += entityLiving.getAbsorptionAmount() / 2.0f;
        }
        return (hearts <= 0) ? "*DEAD*" : String.format("%.1f", hearts);
    }

    private String getPlayerPing(EntityPlayer player) {
        final Minecraft mc = Minecraft.getMinecraft();
        try {
            int responseTime = mc.player.connection.getPlayerInfo(player.getUniqueID()).getResponseTime();
            return responseTime + "ms";
        } catch (NullPointerException ignored) {
            return "-1ms";
        }
    }

    private String getGameModeText(EntityPlayer player) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.getConnection() != null) {
            NetworkPlayerInfo playerInfo = mc.getConnection().getPlayerInfo(player.getUniqueID());
            switch (playerInfo.getGameType()) {
                case SURVIVAL:
                    return "[S]";
                case CREATIVE:
                    return "[C]";
                case ADVENTURE:
                    return "[A]";
                case SPECTATOR:
                    return "[SP]";
                default:
                    return "[?]";
            }
        }
        return "[?]";
    }

    private int getGameModeColor(EntityPlayer player) {
        String gameModeText = getGameModeText(player);
        return gameModeColors.getOrDefault(gameModeText, 0xFFFFFFFF);
    }

    @Listener
    public void receivePacket(EventReceivePacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof SPacketSoundEffect) {
                final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();

                if (packet.getCategory() == SoundCategory.NEUTRAL || packet.getCategory() == SoundCategory.PLAYERS) {
                    final String sound = packet.getSound().getSoundName().getPath();
                    if (sound.endsWith(".step") || sound.endsWith(".paddle_land") || sound.endsWith(".gallop")) {
                        this.footstepDataList.add(new FootstepData(packet.getX(), packet.getY(), packet.getZ(),
                                System.currentTimeMillis()));
                    }
                }
            }
        }
    }

    private int getHealthColor(Entity entity) {
        int scale = (int) Math.round(255.0 - (double) ((EntityLivingBase) entity).getHealth() * 255.0
                / (double) ((EntityLivingBase) entity).getMaxHealth());
        int damageColor = 255 - scale << 8 | scale << 16;

        return (255 << 24) | damageColor;
    }

    @Listener
    public void renderName(EventRenderName event) {
        if (event.getEntity() instanceof EntityPlayer) {
            event.setCanceled(true);
        }
    }

    private void cacheOwnerNameFromUUID(UUID uuid) {
        if (this.cachedMobOwners.containsKey(uuid)) {
            return;
        }

        if (this.uuidTimer.passed(500)) {
            try {
                new Thread(() -> {
                    final String url = "https://api.mojang.com/user/profiles/" + uuid.toString() + "/names";
                    try {
                        final String json = IOUtils.toString(new URL(url));
                        if (json.isEmpty()) {
                            return;
                        }
                        final JSONArray array = (JSONArray) JSONValue.parseWithException(json);
                        final JSONObject nameArray = (JSONObject) array.get(array.size() - 1);
                        final String name = (String) nameArray.get("name");
                        this.cachedMobOwners.put(uuid, name);
                    } catch (Exception exception) {
                        this.cachedMobOwners.put(uuid, uuid.toString());
                    }
                }).start();
            } catch (Exception exception) {
                this.cachedMobOwners.put(uuid, uuid.toString());
            }
            this.uuidTimer.reset();
        }
    }

    private String getNameForEntity(Entity entity) {
        if (entity instanceof EntityArmorStand) {
            return "";
        }
        if (entity instanceof EntityItem) {
            final EntityItem item = (EntityItem) entity;
            String itemName = "";

            final int stackSize = item.getItem().getCount();
            if (stackSize > 1) {
                itemName = item.getItem().getDisplayName() + "(" + item.getItem().getCount() + ")";
            } else {
                itemName = item.getItem().getDisplayName();
            }
            return itemName;
        }
        if (entity instanceof EntityEnderCrystal) {
            return "End Crystal";
        }
        if (entity instanceof EntityEnderPearl) {
            return "Ender Pearl";
        }
        if (entity instanceof EntityMinecart) {
            final EntityMinecart minecart = (EntityMinecart) entity;
            return minecart.getCartItem().getDisplayName();
        }
        return entity.getName();
    }

    private boolean checkFilter(Entity entity) {
        boolean ret = false;

        if (this.local.getValue() && (entity == Minecraft.getMinecraft().player)
                && (Minecraft.getMinecraft().gameSettings.thirdPersonView != 0)) {
            ret = true;
        } else if (this.players.getValue() && entity instanceof EntityPlayer
                && entity != Minecraft.getMinecraft().player) {
            ret = true;
        } else if (this.animals.getValue() && entity instanceof IAnimals && !(entity instanceof IMob)) {
            ret = true;
        } else if (this.mobs.getValue() && entity instanceof IMob) {
            ret = true;
        } else if (this.items.getValue() && entity instanceof EntityItem) {
            ret = true;
        } else if (this.crystals.getValue() && entity instanceof EntityEnderCrystal) {
            ret = true;
        } else if (this.vehicles.getValue() && (entity instanceof EntityBoat || entity instanceof EntityMinecart)) {
            ret = true;
        } else if (this.armorStand.getValue() && entity instanceof EntityArmorStand) {
            ret = true;
        } else if (this.pearls.getValue() && entity instanceof EntityEnderPearl) {
            ret = true;
        }

        if (Minecraft.getMinecraft().player.getRidingEntity() != null
                && entity == Minecraft.getMinecraft().player.getRidingEntity()) {
            ret = false;
        }

        return ret;
    }

    private int getColor(Entity entity) {
        int ret = 0xFFFFFFFF;

        if (entity instanceof IAnimals && !(entity instanceof IMob)) {
            ret = this.animalsColor.getValue().getRGB();
        }
        if (entity instanceof IMob) {
            ret = this.mobsColor.getValue().getRGB();
        }
        if (entity instanceof EntityBoat || entity instanceof EntityMinecart) {
            ret = this.vehiclesColor.getValue().getRGB();
        }
        if (entity instanceof EntityItem) {
            ret = this.itemsColor.getValue().getRGB();
        }
        if (entity instanceof EntityEnderCrystal) {
            ret = this.crystalsColor.getValue().getRGB();
        }
        if (entity instanceof EntityEnderPearl) {
            ret = this.pearlsColor.getValue().getRGB();
        }
        if (entity instanceof EntityPlayer) {
            ret = this.playersColor.getValue().getRGB();

            if (entity == Minecraft.getMinecraft().player) {
                ret = -1;
            }

            if (entity.isSneaking()) {
                ret = this.sneakingColor.getValue().getRGB();
            }

            if (crack.INSTANCE.getFriendManager().isFriend(entity) != null) {
                ret = this.friendsColor.getValue().getRGB();
            }
        }
        return ret;
    }

    private float[] convertBounds(Entity e, float partialTicks, int width, int height) {
        float x = -1;
        float y = -1;
        float w = width + 1;
        float h = height + 1;

        final Vec3d pos = MathUtil.interpolateEntity(e, partialTicks);

        AxisAlignedBB bb = e.getEntityBoundingBox();

        if (e instanceof EntityEnderCrystal) {
            bb = new AxisAlignedBB(bb.minX + 0.3f, bb.minY + 0.2f, bb.minZ + 0.3f, bb.maxX - 0.3f, bb.maxY,
                    bb.maxZ - 0.3f);
        }

        if (e instanceof EntityItem) {
            bb = new AxisAlignedBB(bb.minX, bb.minY + 0.7f, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
        }

        bb = bb.expand(0.15f, 0.1f, 0.15f);

        camera.setPosition(Minecraft.getMinecraft().getRenderViewEntity().posX,
                Minecraft.getMinecraft().getRenderViewEntity().posY,
                Minecraft.getMinecraft().getRenderViewEntity().posZ);

        if (!camera.isBoundingBoxInFrustum(bb)) {
            return null;
        }

        final Vec3d[] corners = {
                new Vec3d(bb.minX - bb.maxX + e.width / 2, 0, bb.minZ - bb.maxZ + e.width / 2),
                new Vec3d(bb.maxX - bb.minX - e.width / 2, 0, bb.minZ - bb.maxZ + e.width / 2),
                new Vec3d(bb.minX - bb.maxX + e.width / 2, 0, bb.maxZ - bb.minZ - e.width / 2),
                new Vec3d(bb.maxX - bb.minX - e.width / 2, 0, bb.maxZ - bb.minZ - e.width / 2),

                new Vec3d(bb.minX - bb.maxX + e.width / 2, bb.maxY - bb.minY, bb.minZ - bb.maxZ + e.width / 2),
                new Vec3d(bb.maxX - bb.minX - e.width / 2, bb.maxY - bb.minY, bb.minZ - bb.maxZ + e.width / 2),
                new Vec3d(bb.minX - bb.maxX + e.width / 2, bb.maxY - bb.minY, bb.maxZ - bb.minZ - e.width / 2),
                new Vec3d(bb.maxX - bb.minX - e.width / 2, bb.maxY - bb.minY, bb.maxZ - bb.minZ - e.width / 2)
        };

        for (Vec3d vec : corners) {
            final GLUProjection.Projection projection = GLUProjection.getInstance().project(
                    pos.x + vec.x - Minecraft.getMinecraft().getRenderManager().viewerPosX,
                    pos.y + vec.y - Minecraft.getMinecraft().getRenderManager().viewerPosY,
                    pos.z + vec.z - Minecraft.getMinecraft().getRenderManager().viewerPosZ,
                    GLUProjection.ClampMode.NONE, false);

            x = Math.max(x, (float) projection.getX());
            y = Math.max(y, (float) projection.getY());

            w = Math.min(w, (float) projection.getX());
            h = Math.min(h, (float) projection.getY());
        }

        if (x != -1 && y != -1 && w != width + 1 && h != height + 1) {
            return new float[] { x, y, w, h };
        }

        return null;
    }

    private enum Mode {
        OPAQUE, BOX
    }

    private enum PotionsMode {
        NONE, ICON, TEXT
    }

    private enum HealthMode {
        NONE, BAR, BARTEXT
    }

    @Getter
    @Setter
    public static class FootstepData {
        private double x;
        private double y;
        private double z;
        private long time;

        public FootstepData(double x, double y, double z, long time) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.time = time;
        }

    }

}
