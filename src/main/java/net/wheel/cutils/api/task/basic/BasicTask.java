package net.wheel.cutils.api.task.basic;

import net.wheel.cutils.api.task.Task;

public abstract class BasicTask implements Task {

    private String name;

    private int priority;

    private boolean online;

    public BasicTask(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public boolean isOnline() {
        return this.online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
