package net.wheel.cutils.impl.command;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.util.StringUtil;
import net.wheel.cutils.crack;

public final class SpawnEggCommand extends Command {

    private final String[] listAlias = new String[] { "List", "L" };
    private final String[] giveAlias = new String[] { "Give", "G" };

    public SpawnEggCommand() {
        super("SpawnEgg", new String[] { "SEgg" }, "Spawns any entity's egg while in creative",
                "SpawnEgg Give <Entity>\nSpawnEgg List");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 3)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");
        final Minecraft mc = Minecraft.getMinecraft();

        if (!mc.player.isCreative()) {
            crack.INSTANCE.errorChat("Creative mode is required to use this command.");
            return;
        }

        if (equals(listAlias, split[1])) {
            displayEntityList();
        } else if (equals(giveAlias, split[1]) && split.length == 3) {
            giveSpawnEgg(split[2]);
        } else {
            crack.INSTANCE.errorChat("Unknown input \u00A7f\"" + input + "\"");
            this.printUsage();
        }
    }

    private void displayEntityList() {
        Set<ResourceLocation> entitySet = new ObjectArraySet<>(EntityList.getEntityNameList());
        int size = entitySet.size();

        if (size > 0) {
            TextComponentString msg = new TextComponentString("\u00A77Entities [" + size + "]\u00A7f: ");
            int index = 0;
            for (ResourceLocation res : entitySet) {
                msg.appendSibling(new TextComponentString(
                        "\u00A7a" + res.getPath() + "\u00A7f" + (index < size - 1 ? ", " : "")));
                index++;
            }
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(msg);
        } else {
            crack.INSTANCE.logChat("No entities available.");
        }
    }

    private void giveSpawnEgg(String entityName) {
        ResourceLocation entityResource = findEntity(entityName);

        if (entityResource != null) {
            ItemStack spawnEgg = createSpawnEgg(entityResource);
            int slot = findEmptyHotbarSlot();

            Minecraft.getMinecraft().player.connection.sendPacket(new CPacketCreativeInventoryAction(
                    36 + (slot != -1 ? slot : Minecraft.getMinecraft().player.inventory.currentItem), spawnEgg));
            crack.INSTANCE.logChat("Gave you a spawn egg for entity: \u00A76" + entityResource.getPath() + "\u00A7f.");
        } else {
            ResourceLocation similar = findSimilarEntity(entityName);
            crack.INSTANCE.errorChat("Unknown entity \u00A7f\"" + entityName + "\".");
            if (similar != null) {
                crack.INSTANCE.logChat("Did you mean \u00A7c" + similar.getPath() + "\u00A7f?");
            }
        }
    }

    private ItemStack createSpawnEgg(ResourceLocation entityResource) {
        ItemStack spawnEgg = new ItemStack(Item.getItemById(383));
        NBTTagCompound tagCompound = new NBTTagCompound();
        NBTTagCompound entityTag = new NBTTagCompound();

        entityTag.setString("id", entityResource.toString());
        tagCompound.setTag("EntityTag", entityTag);
        spawnEgg.setTagCompound(tagCompound);

        return spawnEgg;
    }

    private ResourceLocation findEntity(String name) {
        for (ResourceLocation res : EntityList.getEntityNameList()) {
            if (res.getPath().equalsIgnoreCase(name)) {
                return res;
            }
        }
        return null;
    }

    private ResourceLocation findSimilarEntity(String name) {
        double maxScore = Double.MIN_VALUE;
        ResourceLocation bestMatch = null;

        for (ResourceLocation res : EntityList.getEntityNameList()) {
            double score = StringUtil.levenshteinDistance(name, res.getPath());
            if (score > maxScore) {
                maxScore = score;
                bestMatch = res;
            }
        }
        return bestMatch;
    }

    private int findEmptyHotbarSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);
            if (stack.getItem() == Items.AIR) {
                return i;
            }
        }
        return -1;
    }
}
