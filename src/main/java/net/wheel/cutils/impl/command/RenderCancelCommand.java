package net.wheel.cutils.impl.command;

import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.config.RenderCancelConfig;

public final class RenderCancelCommand extends Command {

    private final String[] addAlias = new String[] { "add" };
    private final String[] removeAlias = new String[] { "remove" };
    private final String[] listAlias = new String[] { "list" };
    private final String[] clearAlias = new String[] { "clear" };

    public RenderCancelCommand() {
        super("RenderCancel", new String[] { "rc" }, "add or remove modids", "RenderCancel Add <modid> / hand\n" +
                "RenderCancel Remove <modid>\n" +
                "RenderCancel List\n" +
                "RenderCancel Clear");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 3)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        if (equals(addAlias, split[1])) {
            if (split.length == 3 && "hand".equalsIgnoreCase(split[2])) {
                addModIdFromHand();
            } else if (split.length == 3) {
                crack.INSTANCE.getRenderCancelManager().addModId(split[2]);
                crack.INSTANCE.getConfigManager().save(RenderCancelConfig.class);
                crack.INSTANCE.logChat("added \247c" + split[2] + "\247f to the RC list");
            } else {
                this.printUsage();
            }
        } else if (equals(removeAlias, split[1])) {
            if (!this.clamp(input, 3, 3)) {
                this.printUsage();
                return;
            }

            crack.INSTANCE.getRenderCancelManager().removeModId(split[2]);
            crack.INSTANCE.getConfigManager().save(RenderCancelConfig.class);
            crack.INSTANCE.logChat("removed \247c" + split[2] + "\247f from the RC list");
        } else if (equals(listAlias, split[1])) {
            if (!this.clamp(input, 2, 2)) {
                this.printUsage();
                return;
            }

            final int size = crack.INSTANCE.getRenderCancelManager().getRenderManagedModIds().size();
            if (size > 0) {
                final TextComponentString msg = new TextComponentString(
                        "\2477Render cancel mod IDs [" + size + "]\247f: ");
                for (String modId : crack.INSTANCE.getRenderCancelManager().getRenderManagedModIds()) {
                    msg.appendSibling(new TextComponentString("\247a" + modId + "\247f, "));
                }
                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(msg);
            } else {
                crack.INSTANCE.logChat("no mods in RC list");
            }
        } else if (equals(clearAlias, split[1])) {
            if (!this.clamp(input, 2, 2)) {
                this.printUsage();
                return;
            }

            crack.INSTANCE.getRenderCancelManager().clearRenderManagedModIds();
            crack.INSTANCE.getConfigManager().save(RenderCancelConfig.class);
            crack.INSTANCE.logChat("cleared RC list");
        } else {
            crack.INSTANCE.errorChat("Unknown input " + "\247f\"" + input + "\"");
            this.printUsage();
        }
    }

    private void addModIdFromHand() {
        final Minecraft mc = Minecraft.getMinecraft();
        final ItemStack itemStack = mc.player.getHeldItemMainhand();

        if (itemStack.isEmpty()) {
            crack.INSTANCE.errorChat("No item in hand");
            return;
        }

        ResourceLocation itemResourceLocation = itemStack.getItem().getRegistryName();
        if (itemResourceLocation == null) {
            crack.INSTANCE.errorChat("Err: No info");
            return;
        }

        String modId = itemResourceLocation.getNamespace();
        crack.INSTANCE.getRenderCancelManager().addModId(modId);
        crack.INSTANCE.getConfigManager().save(RenderCancelConfig.class);
        crack.INSTANCE.logChat("added \247c" + modId + "\247f to the RC list");
    }

    @Override
    public ObjectArrayList<String> getCommandArgs(String[] args) {
        ObjectArrayList<String> suggestions = new ObjectArrayList<>();
        suggestions.addAll(Arrays.asList(addAlias));
        suggestions.addAll(Arrays.asList(removeAlias));
        suggestions.addAll(Arrays.asList(listAlias));
        suggestions.addAll(Arrays.asList(clearAlias));
        return suggestions;
    }
}
