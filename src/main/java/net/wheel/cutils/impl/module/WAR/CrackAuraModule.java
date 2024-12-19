package net.wheel.cutils.impl.module.WAR;

import java.util.Objects;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import net.wheel.cutils.api.event.player.EventUpdateWalkingPlayer;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;

import hand.interactor.voodoo.handler.ListenerPriority;
import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class CrackAuraModule extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final ObjectList<Entity> targets = new ObjectArrayList<>();
    private final Random random = new Random();
    private float cachedRange;
    private double cachedRangeSq;
    private static final int[] OFFSETS = { -1, 0, 1 };
    private static final double PLAYER_REACH_SQ = 5.5 * 5.5;
    private static final float EPSILON = 0.001f;

    public final Value<Mode> mode = new Value<>("Mode", new String[] { "M" }, "Attack mode", Mode.NORMAL);
    public final Value<Boolean> players = new Value<>("players", new String[] { "Player" }, "players", true);
    public final Value<Boolean> mobs = new Value<>("mobs", new String[] { "Mob" }, "mobs", true);
    public final Value<Boolean> animals = new Value<>("animals", new String[] { "Animal" }, "animals", true);
    public final Value<Boolean> vehicles = new Value<>("vehicles", new String[] { "Vehic", "Vehicle" }, "vehicles",
            true);
    public final Value<Boolean> projectiles = new Value<>("projectiles", new String[] { "Projectile", "Proj" },
            "projectiles", true);
    public final Value<Float> range = new Value<>("range", new String[] { "Dist" }, "range", 4.5f, 0.1f, 100.0f, 0.1f);
    public final Value<Boolean> coolDown = new Value<>("cd", new String[] { "CoolD" }, "cd", true);
    public final Value<Boolean> sync = new Value<>("sync", new String[] { "snc" }, "tps sync", true);

    @Override
    public String getMetaData() {
        return this.mode.getValue().name();
    }

    public CrackAuraModule() {
        super("CrackAura", new String[] { "Aura", "CA" }, "winner", "NONE", -1, ModuleType.WAR);
        this.cachedRange = this.range.getValue();
        this.cachedRangeSq = cachedRange * cachedRange;
    }

    @Listener(priority = ListenerPriority.TURBOMAX)
    public void onWalkingUpdate(EventUpdateWalkingPlayer event) {
        if (mc.player == null || mc.world == null || event.getStage() != EventUpdateWalkingPlayer.EventStage.PRE) {
            return;
        }

        updateCache();

        AxisAlignedBB searchBox = mc.player.getEntityBoundingBox().grow(cachedRange);
        double rangeSq = cachedRangeSq;

        targets.clear();
        ObjectArrayList<Entity> entityList = new ObjectArrayList<>(
                mc.world.getEntitiesWithinAABB(Entity.class, searchBox));

        for (int i = 0; i < entityList.size(); i++) {
            Entity entity = entityList.get(i);
            if (mc.player.getDistanceSq(entity) <= rangeSq && entFilter(entity)) {
                targets.add(entity);
            }
        }

        if (!targets.isEmpty()) {
            sortByDist();
            attackTargets();
        }
    }

    private void sortByDist() {
        ObjectArrayList<Entity> entities = (ObjectArrayList<Entity>) targets;
        final int size = entities.size();
        final double[] distances = new double[size];
        for (int i = 0; i < size; i++) {
            Entity entity = entities.get(i);
            double dx = mc.player.posX - entity.posX;
            double dy = mc.player.posY - entity.posY;
            double dz = mc.player.posZ - entity.posZ;
            distances[i] = dx * dx + dy * dy + dz * dz;
        }
        quickSortDist(entities, distances, 0, size - 1);
    }

    private void quickSortDist(ObjectArrayList<Entity> entities, double[] distances, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(entities, distances, low, high);
            quickSortDist(entities, distances, low, pivotIndex - 1);
            quickSortDist(entities, distances, pivotIndex + 1, high);
        }
    }

    private int partition(ObjectArrayList<Entity> entities, double[] distances, int low, int high) {
        double pivot = distances[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (distances[j] < pivot) {
                i++;
                swap(entities, distances, i, j);
            }
        }
        swap(entities, distances, i + 1, high);
        return i + 1;
    }

    private void swap(ObjectArrayList<Entity> entities, double[] distances, int i, int j) {
        Entity tempEntity = entities.get(i);
        entities.set(i, entities.get(j));
        entities.set(j, tempEntity);
        double tempDistance = distances[i];
        distances[i] = distances[j];
        distances[j] = tempDistance;
    }

    private void attackTargets() {
        float ticks = 20.0f - Objects.requireNonNull(mc.getConnection())
                .getPlayerInfo(mc.player.getUniqueID()).getResponseTime();
        boolean canAttack = !this.coolDown.getValue() ||
                mc.player.getCooledAttackStrength(this.sync.getValue() ? -ticks : 0.0f) >= 1;

        if (!canAttack)
            return;

        switch (this.mode.getValue()) {
            case BLADEWALTZ:
                execBladeWaltz(targets.get(0));
                break;
            case DOUBLESTRIKE:
                execDoubleStrike();
                break;
            case NORMAL:
            default:
                execStandardAttack();
                break;
        }

        mc.player.resetCooldown();
    }

    private void execBladeWaltz(Entity target) {
        final int waltzAttacks = 3;
        for (int i = 0; i < waltzAttacks; i++) {
            double randX = (random.nextDouble() * 2 - 1) * 1.5;
            double randY = (random.nextDouble() * 3 - 1.5) * 1.5;
            double randZ = (random.nextDouble() * 2 - 1) * 1.5;
            double newX = target.posX + randX;
            double newY = target.posY + randY;
            double newZ = target.posZ + randZ;
            if (canFitAtPos(newX, newY, newZ, true)) {
                teleportTo(new BlockPos(newX, newY, newZ));
                sendAttackPacket(target);
            }
        }
    }

    private void execStandardAttack() {
        ItemStack stack = mc.player.getHeldItem(EnumHand.OFF_HAND);
        if (!stack.isEmpty() && stack.getItem() == Items.SHIELD) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM,
                    BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
        }

        for (Entity target : targets) {
            if (!target.isEntityAlive())
                continue;
            sendAttackPacket(target);
        }
    }

    private synchronized void updateCache() {
        float currentRange = this.range.getValue();
        if (Math.abs(currentRange - cachedRange) > EPSILON) {
            cachedRange = currentRange;
            cachedRangeSq = cachedRange * cachedRange;
        }
    }

    private void execDoubleStrike() {
        BlockPos validPosition = null;
        for (Entity target : targets) {
            if (validPosition == null) {
                validPosition = findValidPosTarget(target);
                if (validPosition != null) {
                    teleportTo(validPosition);
                }
            }
            if (validPosition != null && canReach(target)) {
                sendAttackPacket(target);
            }
        }
    }

    private boolean canReach(Entity entity) {
        return mc.player.getDistanceSq(entity.posX, entity.posY, entity.posZ) <= PLAYER_REACH_SQ;
    }

    private void sendAttackPacket(Entity target) {
        mc.player.connection.sendPacket(new CPacketUseEntity(target));
    }

    private void teleportTo(BlockPos pos) {
        mc.player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }

    private BlockPos findValidPosTarget(Entity target) {
        BlockPos targetPos = new BlockPos(target.posX, target.posY, target.posZ);
        if (canFitAtPos(targetPos.getX(), targetPos.getY(), targetPos.getZ(), true)) {
            return targetPos;
        }
        for (int offset : OFFSETS) {
            for (int x : OFFSETS) {
                for (int z : OFFSETS) {
                    if (canFitAtPos(targetPos.getX() + x, targetPos.getY() + offset, targetPos.getZ() + z, true)) {
                        return targetPos.add(x, offset, z);
                    }
                }
            }
        }
        return null;
    }

    private boolean canFitAtPos(double x, double y, double z, boolean includeHeight) {
        if (mc.player == null || mc.world == null)
            return false;
        double halfWidth = mc.player.width / 2.0;
        double height = includeHeight ? mc.player.height : 0.0;
        AxisAlignedBB boundingBox = new AxisAlignedBB(
                x - halfWidth, y, z - halfWidth,
                x + halfWidth, y + height, z + halfWidth);
        return !mc.world.collidesWithAnyBlock(boundingBox);
    }

    private boolean entFilter(Entity entity) {
        if (entity == null || !entity.isEntityAlive() || entity == mc.player)
            return false;
        if (entity instanceof EntityPlayer)
            return this.players.getValue() &&
                    !entity.getName().equals(mc.player.getName());
        if (entity instanceof IMob)
            return this.mobs.getValue();
        if (entity instanceof IAnimals)
            return this.animals.getValue();
        if (entity instanceof EntityBoat || entity instanceof EntityMinecart)
            return this.vehicles.getValue();
        if (entity instanceof EntityShulkerBullet || entity instanceof EntityFireball)
            return this.projectiles.getValue();
        return false;
    }

    private enum Mode {
        NORMAL, BLADEWALTZ, DOUBLESTRIKE
    }
}
