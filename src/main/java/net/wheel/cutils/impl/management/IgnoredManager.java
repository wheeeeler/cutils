package net.wheel.cutils.impl.management;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.ignore.Ignored;

@Getter
@Setter
public final class IgnoredManager {

    private List<Ignored> ignoredList = new CopyOnWriteArrayList<>();

    public void add(String name) {
        this.ignoredList.add(new Ignored(name));

    }

    public Ignored find(String name) {
        for (Ignored ignored : this.ignoredList) {
            if (ignored.getName().equalsIgnoreCase(name)) {
                return ignored;
            }
        }
        return null;
    }

    public void unload() {
        this.ignoredList.clear();
    }

}
