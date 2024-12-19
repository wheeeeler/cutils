package net.wheel.cutils.impl.management;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import net.wheel.cutils.api.event.EventStageable;
import net.wheel.cutils.api.event.player.EventPlayerUpdate;
import net.wheel.cutils.api.util.Timer;
import net.wheel.cutils.crack;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class ChatManager {

    private final Timer timer = new Timer();

    private final List<String> chatBuffer = new ArrayList<>();

    private World world;

    public ChatManager() {
        crack.INSTANCE.getEventManager().addEventListener(this);
    }

    public void add(String s) {
        this.chatBuffer.add(s);
    }

    public void unload() {
        this.chatBuffer.clear();
        crack.INSTANCE.getEventManager().removeEventListener(this);
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {

            if (this.world != Minecraft.getMinecraft().world) {
                this.world = Minecraft.getMinecraft().world;
                this.chatBuffer.clear();
            }

            for (int i = 0; i < this.chatBuffer.size(); i++) {
                final String s = this.chatBuffer.get(i);
                if (s != null) {
                    if (this.timer.passed(200.0f)) {
                        Minecraft.getMinecraft().player.sendChatMessage(s);
                        this.chatBuffer.remove(s);
                        this.timer.reset();
                        i--;
                    }
                }
            }
        }
    }

}
