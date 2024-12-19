package net.wheel.cutils.impl.command;

import java.util.Arrays;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventReceivePacket;
import net.wheel.cutils.api.event.player.EventPlayerUpdate;
import net.wheel.cutils.api.notification.Notification;
import net.wheel.cutils.api.util.MathUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.module.hidden.CommandsModule;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class CalcStrongholdCommand extends Command {

    private final String[] resetAlias = new String[] { "Reset" };
    private Vec3d firstStart;
    private Vec3d firstEnd;
    private Vec3d secondStart;
    private Vec3d secondEnd;

    public CalcStrongholdCommand() {
        super("CalcStronghold", new String[] { "CS", "FindStronghold", "cstrong" },
                "Calculates where the nearest stronghold is", "CalcStronghold\nCalcStronghold Reset");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 1, 2)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        if (split.length > 1) {
            if (equals(resetAlias, split[1])) {
                crack.INSTANCE.logChat("Reset Stronghold finder");
                this.firstStart = null;
                this.firstEnd = null;
                this.secondStart = null;
                this.secondEnd = null;
                crack.INSTANCE.getEventManager().removeEventListener(this);
            } else {
                crack.INSTANCE.errorChat("Unknown input " + "\247f\"" + input + "\"");
            }
        } else {
            crack.INSTANCE.getEventManager().addEventListener(this);
            crack.INSTANCE.logChat("Please throw the first Eye Of Ender");
        }
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (this.firstStart != null && this.firstEnd != null && this.secondStart != null
                    && this.secondEnd != null) {
                final double[] start = new double[] { this.secondStart.x, this.secondStart.z, this.secondEnd.x,
                        this.secondEnd.z };
                final double[] end = new double[] { this.firstStart.x, this.firstStart.z, this.firstEnd.x,
                        this.firstEnd.z };
                final double[] intersection = MathUtil.calcIntersection(start, end);

                if (Double.isNaN(intersection[0]) || Double.isNaN(intersection[1]) || Double.isInfinite(intersection[0])
                        || Double.isInfinite(intersection[1])) {
                    crack.INSTANCE.errorChat("Error lines are parallel");
                    crack.INSTANCE.getEventManager().removeEventListener(this);
                    return;
                }

                final double dist = Minecraft.getMinecraft().player.getDistance(intersection[0],
                        Minecraft.getMinecraft().player.posY, intersection[1]);

                final TextComponentString component = new TextComponentString(
                        "Stronghold found " + ChatFormatting.GRAY + (int) dist + "m away");
                final String coords = String.format("X: %s, Y: ?, Z: %s\nClick to add a Waypoint",
                        (int) intersection[0], (int) intersection[1]);
                final CommandsModule cmds = (CommandsModule) crack.INSTANCE.getModuleManager()
                        .find(CommandsModule.class);

                if (cmds != null) {
                    component.setStyle(new Style()
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(coords)))
                            .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                    cmds.prefix.getValue() + "Waypoints add Stronghold " + intersection[0] + " "
                                            + Minecraft.getMinecraft().player.posY + " " + intersection[1])));

                }

                crack.INSTANCE.logcChat(component);
                crack.INSTANCE.getNotificationManager().addNotification("",
                        "Stronghold found " + ChatFormatting.GRAY + (int) dist + "m away", Notification.Type.SUCCESS,
                        3000);
                this.firstStart = null;
                this.firstEnd = null;
                this.secondStart = null;
                this.secondEnd = null;
                crack.INSTANCE.getEventManager().removeEventListener(this);
            }
        }
    }

    @Listener
    public void receivePacket(EventReceivePacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof SPacketSpawnObject) {
                final SPacketSpawnObject packet = (SPacketSpawnObject) event.getPacket();
                if (packet.getType() == 72) {
                    if (this.firstStart == null) {
                        this.firstStart = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
                        crack.INSTANCE.logChat("Found first Eye Of Ender start");
                    } else {
                        if (this.secondStart == null) {
                            this.secondStart = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
                            crack.INSTANCE.logChat("Found second Eye Of Ender start");
                        }
                    }
                }
            }
            if (event.getPacket() instanceof SPacketSoundEffect) {
                final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
                if (packet.getSound() == SoundEvents.ENTITY_ENDEREYE_DEATH) {
                    if (this.firstEnd == null) {
                        this.firstEnd = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
                        crack.INSTANCE.logChat("Found first Eye Of Ender end");
                        crack.INSTANCE.logChat("Please throw the second Eye Of Ender");
                    } else {
                        if (this.secondEnd == null) {
                            this.secondEnd = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
                            crack.INSTANCE.logChat("Found second Eye Of Ender end");
                        }
                    }
                }
            }
        }
    }

    @Override
    public ObjectArrayList<String> getCommandArgs(String[] args) {
        ObjectArrayList<String> suggestions = new ObjectArrayList<>();

        if (args.length == 1) {
            suggestions.addAll(Arrays.asList(resetAlias));
        }

        return suggestions;
    }
}
