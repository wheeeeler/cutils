package net.wheel.cutils.api.task;

import java.util.List;

import net.wheel.cutils.api.task.basic.BasicTask;

public interface TaskFactory<T extends BasicTask> {

    void removeTask(String taskName);

    void removeTask(T task);

    List<T> getTasks();
}
