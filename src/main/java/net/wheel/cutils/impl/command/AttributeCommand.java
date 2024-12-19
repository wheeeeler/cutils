package net.wheel.cutils.impl.command;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;

public final class AttributeCommand extends Command {

    private static final Map<String, IAttribute> ATTRIBUTE_MAP = new HashMap<>();
    private static final String[] SLOTS = { "mainhand", "offhand", "head", "chest", "legs", "feet" };
    private static final String[] OPERATIONS = { "0", "1", "2" };

    static {
        for (Field field : SharedMonsterAttributes.class.getDeclaredFields()) {
            if (IAttribute.class.isAssignableFrom(field.getType())) {
                try {
                    IAttribute attribute = (IAttribute) field.get(null);
                    ATTRIBUTE_MAP.put(attribute.getName().toLowerCase(), attribute);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public AttributeCommand() {
        super("Attribute", new String[] { "Attr" }, "Add attributes to your held item while in creative mode",
                "Attribute <Attribute Name / All> <Amount> <Operation> <Slot> ([true/false] Replace Existing)");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 5, 7)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        final String attributeName = split[1];
        final String amountStr = split[2];
        final String operationStr = split[3];
        final String slot = split[4];

        if (attributeName != null && amountStr != null && operationStr != null && slot != null) {
            final Minecraft mc = Minecraft.getMinecraft();

            if (!mc.player.isCreative()) {
                crack.INSTANCE.errorChat("need gm1");
                return;
            }

            final ItemStack itemStack = mc.player.getHeldItemMainhand();

            if (itemStack.isEmpty()) {
                crack.INSTANCE.errorChat("no item");
                return;
            }

            NBTTagCompound tagCompound = itemStack.getTagCompound();

            if (tagCompound == null) {
                tagCompound = new NBTTagCompound();
                itemStack.setTagCompound(tagCompound);
            }

            boolean replaceExisting = split.length > 5 && split[5].equalsIgnoreCase("true");

            if (replaceExisting || !tagCompound.hasKey("AttributeModifiers", 9)) {
                tagCompound.setTag("AttributeModifiers", new NBTTagList());
            }

            NBTTagList attributeModifiers = itemStack.getTagCompound().getTagList("AttributeModifiers", 10);

            if (replaceExisting) {
                attributeModifiers = new NBTTagList();
            }

            double amount;
            int operation;
            try {
                amount = Double.parseDouble(amountStr);
                operation = Integer.parseInt(operationStr);
                if (operation < 0 || operation > 2)
                    throw new NumberFormatException();
            } catch (NumberFormatException e) {
                crack.INSTANCE.errorChat("Invalid amount or operation");
                return;
            }

            if (attributeName.equalsIgnoreCase("all")) {
                addAllAttributes(attributeModifiers, amount, operation, slot);
            } else if (ATTRIBUTE_MAP.containsKey(attributeName.toLowerCase())) {
                addAttribute(attributeModifiers, ATTRIBUTE_MAP.get(attributeName.toLowerCase()), amount, operation,
                        slot);
            } else {
                crack.INSTANCE.errorChat("Unknown attribute name: " + attributeName);
                return;
            }

            tagCompound.setTag("AttributeModifiers", attributeModifiers);
            mc.player.connection
                    .sendPacket(new CPacketCreativeInventoryAction(mc.player.inventory.currentItem, itemStack));

            crack.INSTANCE.logChat("Attributes have been set");
        }
    }

    private void addAllAttributes(NBTTagList attributeModifiers, double amount, int operation, String slot) {
        for (Map.Entry<String, IAttribute> entry : ATTRIBUTE_MAP.entrySet()) {
            addAttribute(attributeModifiers, entry.getValue(), amount, operation, slot);
        }
    }

    private void addAttribute(NBTTagList attributeModifiers, IAttribute attribute, double amount, int operation,
            String slot) {
        NBTTagCompound attributeCompound = new NBTTagCompound();
        attributeCompound.setString("AttributeName", attribute.getName());
        attributeCompound.setString("Name", attribute.getName());
        attributeCompound.setDouble("Amount", amount);
        attributeCompound.setInteger("Operation", operation);
        attributeCompound.setLong("UUIDMost", 894654L);
        attributeCompound.setLong("UUIDLeast", 2872L);
        attributeCompound.setString("Slot", slot.toLowerCase());
        attributeModifiers.appendTag(attributeCompound);
    }

    @Override
    public ObjectArrayList<String> getCommandArgs(String[] args) {
        ObjectArrayList<String> suggestions = new ObjectArrayList<>();

        switch (args.length) {
            case 1:
                suggestions.add("all");
                suggestions.addAll(ATTRIBUTE_MAP.keySet());
                break;
            case 3:
                suggestions.add("-2147483647");
                suggestions.add("1024");
                suggestions.add("2147483647");
                break;
            case 4:
                suggestions.addAll(Arrays.asList(OPERATIONS));
                break;
            case 5:
                suggestions.addAll(Arrays.asList(SLOTS));
                break;
            case 6:
                suggestions.add("true");
                suggestions.add("false");
                break;
            default:
                break;
        }
        return suggestions;
    }
}
