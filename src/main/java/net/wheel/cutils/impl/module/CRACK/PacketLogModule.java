package net.wheel.cutils.impl.module.CRACK;

import java.lang.reflect.Field;
import java.util.logging.Level;

import net.minecraft.network.Packet;
import net.minecraft.util.StringUtils;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.network.EventReceivePacket;
import net.wheel.cutils.api.event.network.EventSendPacket;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class PacketLogModule extends Module {

    public final Value<Boolean> incoming = new Value<Boolean>("Incoming", new String[] { "in" },
            "Log incoming packets when enabled", true);
    public final Value<Boolean> outgoing = new Value<Boolean>("Outgoing", new String[] { "out" },
            "Log outgoing packets when enabled", true);
    public final Value<Boolean> chat = new Value<Boolean>("Chat", new String[] { "ch" },
            "dont use it will just cancerspam your chat", true);
    public final Value<Boolean> console = new Value<Boolean>("Console", new String[] { "con" },
            "Logs packet traffic to console", true);
    public final Value<Boolean> data = new Value<Boolean>("Data", new String[] { "dat" },
            "Include data about the packet's class in the log when enabled", true);
    private Packet[] packets;

    public PacketLogModule() {
        super("PacketLogModule", new String[] { "pktlgr" }, "Log incoming and/or outgoing packets to console", "NONE",
                -1,
                ModuleType.CRACK);
    }

    @Listener
    public void onToggle() {
        super.onToggle();
        this.packets = null;
    }

    @Listener
    public void receivePacket(EventReceivePacket event) {
        if (this.incoming.getValue()) {
            if (event.getStage() == EventStageable.EventStage.PRE) {
                if (this.console.getValue()) {
                    crack.INSTANCE.getLogger().log(Level.INFO,
                            "\2477IN: \247r" + event.getPacket().getClass().getSimpleName() + " {");

                    if (this.data.getValue()) {
                        try {

                            Class clazz = event.getPacket().getClass();

                            while (clazz != Object.class) {

                                for (Field field : clazz.getDeclaredFields()) {
                                    if (field != null) {
                                        if (!field.isAccessible()) {
                                            field.setAccessible(true);
                                        }
                                        crack.INSTANCE.getLogger().log(Level.INFO,
                                                StringUtils.stripControlCodes("      " + field.getType().getSimpleName()
                                                        + " " + field.getName() + " = "
                                                        + field.get(event.getPacket())));
                                    }
                                }

                                clazz = clazz.getSuperclass();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    crack.INSTANCE.getLogger().log(Level.INFO, "}");
                }

                if (this.chat.getValue()) {
                    crack.INSTANCE.logChat("\2477IN: \247r" + event.getPacket().getClass().getSimpleName() + " {");

                    if (this.data.getValue()) {
                        try {

                            Class clazz = event.getPacket().getClass();

                            while (clazz != Object.class) {

                                for (Field field : clazz.getDeclaredFields()) {
                                    if (field != null) {
                                        if (!field.isAccessible()) {
                                            field.setAccessible(true);
                                        }
                                        crack.INSTANCE.logChat(StringUtils
                                                .stripControlCodes("      " + field.getType().getSimpleName() + " "
                                                        + field.getName() + " = " + field.get(event.getPacket())));
                                    }
                                }

                                clazz = clazz.getSuperclass();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    crack.INSTANCE.logChat("}");
                }
            }
        }
    }

    @Listener
    public void sendPacket(EventSendPacket event) {
        if (this.outgoing.getValue()) {
            if (event.getStage() == EventStageable.EventStage.PRE) {
                if (this.console.getValue()) {
                    crack.INSTANCE.getLogger().log(Level.INFO,
                            "\2477OUT: \247r" + event.getPacket().getClass().getSimpleName() + " {");

                    if (this.data.getValue()) {
                        try {

                            Class clazz = event.getPacket().getClass();

                            while (clazz != Object.class) {

                                for (Field field : clazz.getDeclaredFields()) {
                                    if (field != null) {
                                        if (!field.isAccessible()) {
                                            field.setAccessible(true);
                                        }
                                        crack.INSTANCE.getLogger().log(Level.INFO,
                                                StringUtils.stripControlCodes("      " + field.getType().getSimpleName()
                                                        + " " + field.getName() + " = "
                                                        + field.get(event.getPacket())));
                                    }
                                }

                                clazz = clazz.getSuperclass();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    crack.INSTANCE.getLogger().log(Level.INFO, "}");
                }

                if (this.chat.getValue()) {
                    crack.INSTANCE.logChat("\2477OUT: \247r" + event.getPacket().getClass().getSimpleName() + " {");

                    if (this.data.getValue()) {
                        try {

                            Class clazz = event.getPacket().getClass();

                            while (clazz != Object.class) {

                                for (Field field : clazz.getDeclaredFields()) {
                                    if (field != null) {
                                        if (!field.isAccessible()) {
                                            field.setAccessible(true);
                                        }
                                        crack.INSTANCE.logChat(StringUtils
                                                .stripControlCodes("      " + field.getType().getSimpleName() + " "
                                                        + field.getName() + " = " + field.get(event.getPacket())));
                                    }
                                }

                                clazz = clazz.getSuperclass();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    crack.INSTANCE.logChat("}");
                }
            }
        }
    }
}
