package net.wheel.cutils.impl.command;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.math.RayTraceResult;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;

public final class NbtCommand extends Command {

    private NBTTagCompound clipboard;

    public NbtCommand() {
        super("nbt", new String[] { "nbt" }, "Allows manipulation of NBT data",
                "nbt hand\n" +
                        "nbt block\n" +
                        "nbt set <key> <value>\n" +
                        "nbt remove <key>\n" +
                        "nbt copy\n" +
                        "nbt paste");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 4)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        switch (split[1].toLowerCase()) {
            case "hand":
                displayHandNbt();
                break;
            case "block":
                displayBlockNbt();
                break;
            case "set":
                if (split.length == 4)
                    setHandNbt(split[2], split[3]);
                else
                    this.printUsage();
                break;
            case "remove":
                if (split.length == 3)
                    removeHandNbt(split[2]);
                else
                    this.printUsage();
                break;
            case "copy":
                copyHandNbt();
                break;
            case "paste":
                pasteHandNbt();
                break;
            default:
                crack.INSTANCE.errorChat("Unknown input \u00A7f\"" + input + "\"");
                this.printUsage();
                break;
        }
    }

    private void displayHandNbt() {
        ItemStack itemStack = Minecraft.getMinecraft().player.getHeldItemMainhand();
        if (itemStack.isEmpty()) {
            crack.INSTANCE.errorChat("No item in hand.");
            return;
        }

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            crack.INSTANCE.logChat("NBT Data:\n" + formatNbt(tagCompound, 0));
        } else {
            crack.INSTANCE.logChat("\u00A7cNo NBT data found.");
        }
    }

    private void displayBlockNbt() {
        RayTraceResult rayTraceResult = Minecraft.getMinecraft().objectMouseOver;
        if (rayTraceResult == null || rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK) {
            crack.INSTANCE.errorChat("No block targeted.");
            return;
        }

        Block block = Minecraft.getMinecraft().world.getBlockState(rayTraceResult.getBlockPos()).getBlock();
        if (block == Blocks.AIR) {
            crack.INSTANCE.errorChat("No block targeted.");
        } else {
            crack.INSTANCE
                    .logChat("Block Info: " + block.getLocalizedName() + " (ID: " + Block.getIdFromBlock(block) + ")");
        }
    }

    private void setHandNbt(String key, String value) {
        if (!Minecraft.getMinecraft().player.capabilities.isCreativeMode) {
            crack.INSTANCE.errorChat("Creative mode is required to modify NBT data.");
            return;
        }

        ItemStack itemStack = Minecraft.getMinecraft().player.getHeldItemMainhand();
        if (itemStack.isEmpty()) {
            crack.INSTANCE.errorChat("No item in hand.");
            return;
        }

        NBTTagCompound tagCompound = itemStack.getOrCreateSubCompound("tag");
        setNestedNbtValue(tagCompound, key, value);
        crack.INSTANCE.logChat("Set NBT \u00A76" + key + "\u00A7f to \u00A76" + value);
    }

    private void setNestedNbtValue(NBTTagCompound compound, String key, String value) {
        String[] keyParts = key.split("\\.");
        NBTTagCompound currentCompound = compound;

        for (int i = 0; i < keyParts.length - 1; i++) {
            String part = keyParts[i];
            if (currentCompound.hasKey(part, 10)) {
                currentCompound = currentCompound.getCompoundTag(part);
            } else if (currentCompound.hasKey(part, 9)) {
                NBTTagList list = currentCompound.getTagList(part, 10);
                int index = Integer.parseInt(keyParts[++i]);
                if (index < 0 || index >= list.tagCount()) {
                    crack.INSTANCE.errorChat("Index out of bounds for key: " + key);
                    return;
                }
                currentCompound = list.getCompoundTagAt(index);
            } else {
                NBTTagCompound newCompound = new NBTTagCompound();
                currentCompound.setTag(part, newCompound);
                currentCompound = newCompound;
            }
        }
        updateNbtValue(currentCompound, keyParts[keyParts.length - 1], value);
    }

    private void updateNbtValue(NBTTagCompound compound, String key, String value) {
        try {
            if (value.matches("-?\\d+")) {
                compound.setInteger(key, Integer.parseInt(value));
            } else if (value.matches("-?\\d+\\.\\d+")) {
                compound.setDouble(key, Double.parseDouble(value));
            } else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                compound.setBoolean(key, Boolean.parseBoolean(value));
            } else {
                compound.setString(key, value);
            }
        } catch (NumberFormatException e) {
            crack.INSTANCE.errorChat("Invalid value format for key: " + key);
        }
    }

    private void removeHandNbt(String key) {
        if (!Minecraft.getMinecraft().player.capabilities.isCreativeMode) {
            crack.INSTANCE.errorChat("Creative mode is required to modify NBT data.");
            return;
        }

        ItemStack itemStack = Minecraft.getMinecraft().player.getHeldItemMainhand();
        if (itemStack.isEmpty()) {
            crack.INSTANCE.errorChat("No item in hand.");
            return;
        }

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey(key)) {
            tagCompound.removeTag(key);
            crack.INSTANCE.logChat("Removed NBT key \u00A76" + key);
        } else {
            crack.INSTANCE.errorChat("NBT key \u00A76" + key + "\u00A7f not found.");
        }
    }

    private void copyHandNbt() {
        ItemStack itemStack = Minecraft.getMinecraft().player.getHeldItemMainhand();
        if (itemStack.isEmpty()) {
            crack.INSTANCE.errorChat("No item in hand.");
            return;
        }

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            clipboard = tagCompound.copy();
            crack.INSTANCE.logChat("NBT data copied to clipboard.");
        } else {
            crack.INSTANCE.errorChat("No NBT data to copy.");
        }
    }

    private void pasteHandNbt() {
        if (!Minecraft.getMinecraft().player.capabilities.isCreativeMode) {
            crack.INSTANCE.errorChat("Creative mode is required to modify NBT data.");
            return;
        }

        if (clipboard == null) {
            crack.INSTANCE.errorChat("Clipboard is empty.");
            return;
        }

        ItemStack itemStack = Minecraft.getMinecraft().player.getHeldItemMainhand();
        if (itemStack.isEmpty()) {
            crack.INSTANCE.errorChat("No item in hand.");
            return;
        }

        itemStack.setTagCompound(clipboard.copy());
        crack.INSTANCE.logChat("NBT data pasted from clipboard.");
    }

    private String formatNbt(NBTBase nbt, int indent) {
        StringBuilder sb = new StringBuilder();
        String indentStr = repeat("  ", indent);

        if (nbt instanceof NBTTagCompound) {
            NBTTagCompound compound = (NBTTagCompound) nbt;
            sb.append("{\n");
            for (String key : compound.getKeySet()) {
                sb.append(indentStr).append("  ").append(key).append(": ")
                        .append(formatNbt(compound.getTag(key), indent + 1)).append("\n");
            }
            sb.append(indentStr).append("}");
        } else if (nbt instanceof NBTTagList) {
            NBTTagList list = (NBTTagList) nbt;
            sb.append("[\n");
            for (int i = 0; i < list.tagCount(); i++) {
                sb.append(indentStr).append("  ").append(formatNbt(list.get(i), indent + 1)).append("\n");
            }
            sb.append(indentStr).append("]");
        } else if (nbt instanceof NBTTagString) {
            sb.append("\"").append(((NBTTagString) nbt).getString()).append("\"");
        } else if (nbt instanceof NBTTagInt) {
            sb.append(((NBTTagInt) nbt).getInt());
        } else if (nbt instanceof NBTTagDouble) {
            sb.append(((NBTTagDouble) nbt).getDouble());
        } else if (nbt instanceof NBTTagFloat) {
            sb.append(((NBTTagFloat) nbt).getFloat());
        } else if (nbt instanceof NBTTagLong) {
            sb.append(((NBTTagLong) nbt).getLong());
        } else if (nbt instanceof NBTTagShort) {
            sb.append(((NBTTagShort) nbt).getShort());
        } else if (nbt instanceof NBTTagByte) {
            sb.append(((NBTTagByte) nbt).getByte());
        } else if (nbt instanceof NBTTagIntArray) {
            sb.append("[I; ").append(Arrays.toString(((NBTTagIntArray) nbt).getIntArray())).append("]");
        } else if (nbt instanceof NBTTagByteArray) {
            sb.append("[B; ").append(Arrays.toString(((NBTTagByteArray) nbt).getByteArray())).append("]");
        } else {
            sb.append(nbt.toString());
        }

        return sb.toString();
    }

    private String repeat(String str, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}
