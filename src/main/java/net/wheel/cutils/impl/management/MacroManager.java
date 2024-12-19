package net.wheel.cutils.impl.management;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.macro.Macro;

@Getter
@Setter
public final class MacroManager {

    private List<Macro> macroList = new ArrayList<>();

    public MacroManager() {

    }

    public Macro find(String name) {
        for (Macro macro : this.macroList) {
            if (macro.getName().equalsIgnoreCase(name)) {
                return macro;
            }
        }
        return null;
    }

    public void addMacro(String name, String key, String macro) {
        this.macroList.add(new Macro(name, key, macro));
    }

    public void unload() {
        this.macroList.clear();
    }

}
