package net.wheel.cutils.impl.command;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.util.StringUtil;
import net.wheel.cutils.crack;

public final class DirectionClipCommand extends Command {

    private double shiftBoost = 10;
    private int searchRadius = 5;

    public DirectionClipCommand() {
        super("dclip", new String[] { "dc", "directional clip" }, "d", "dclip <Amount> [ShiftBoost] [SearchRadius]");
    }

    @Override
    public void exec(String input) {
        if (!clamp(input, 1, 1000)) {
            printUsage();
            return;
        }

        String[] split = input.split(" ");
        double num = parseArgs(split, 1, 0.0);

        if (split.length > 2)
            shiftBoost = parseArgs(split, 2, shiftBoost, "boost");
        if (split.length > 3)
            searchRadius = (int) parseArgs(split, 3, searchRadius, "search radius");

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            num += shiftBoost;

        Vec3d lookVec = Minecraft.getMinecraft().player.getLookVec();
        Vec3d targetPosition = Minecraft.getMinecraft().player.getPositionVector().add(lookVec.scale(num));

        BlockPos validPosition = validPosLookup(targetPosition, Minecraft.getMinecraft().world);
        setPlayerPos(validPosition);
    }

    private double parseArgs(String[] args, int index, double defaultValue, String... logMessage) {
        if (args.length > index && StringUtil.isDouble(args[index])) {
            double value = Double.parseDouble(args[index]);
            if (logMessage.length > 0)
                crack.INSTANCE.logChat("\u00A7a" + logMessage[0] + " set to " + value);
            return value;
        }
        return defaultValue;
    }

    private void setPlayerPos(BlockPos pos) {
        Minecraft mc = Minecraft.getMinecraft();
        Vec3d centeredPos = new Vec3d(pos).add(0.5, 0, 0.5);

        if (mc.player.getRidingEntity() != null) {
            mc.player.getRidingEntity().setPosition(centeredPos.x, pos.getY(), centeredPos.z);
        } else {
            mc.player.setPosition(centeredPos.x, pos.getY(), centeredPos.z);
        }
    }

    private BlockPos validPosLookup(Vec3d target, World world) {
        BlockPos targetPos = new BlockPos(target);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(targetPos);

        for (int yOffset = 0; yOffset <= searchRadius; yOffset++) {
            for (int direction = -1; direction <= 1; direction += 2) {
                mutablePos.setPos(targetPos.getX(), targetPos.getY() + yOffset * direction, targetPos.getZ());
                if (posValid(mutablePos, world)) {
                    return mutablePos.toImmutable();
                }
            }
        }

        return new BlockPos(targetPos.getX(), world.getHeight(targetPos.getX(), targetPos.getZ()), targetPos.getZ());
    }

    private boolean posValid(BlockPos pos, World world) {
        return isReplaceable(world.getBlockState(pos)) &&
                isReplaceable(world.getBlockState(pos.up())) &&
                !isReplaceable(world.getBlockState(pos.down()));
    }

    private boolean isReplaceable(IBlockState state) {
        return state.getMaterial().isReplaceable();
    }

    @Override
    public ObjectArrayList<String> getCommandArgs(String[] args) {
        ObjectArrayList<String> suggestions = new ObjectArrayList<>();
        String[] commonSuggestions = { "\u00A7a5", "\u00A7a10", "\u00A7a20", "\u00A7a50", "\u00A7a100" };

        if (args.length == 1) {
            addSuggestions(suggestions, commonSuggestions);
        } else if (args.length == 2 || args.length == 3) {
            addSuggestions(suggestions, "\u00A7a10", "\u00A720", "\u00A750");
        }

        return suggestions;
    }

    private void addSuggestions(ObjectArrayList<String> suggestions, String... args) {
        for (String arg : args) {
            suggestions.add(arg);
        }
    }
}
