package net.wheel.cutils.impl.management;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Getter;

@Getter
public final class FilterManager {

    private List<String> filteredWords = new CopyOnWriteArrayList<>();

    public void addFilteredWord(String word) {
        this.filteredWords.add(word.toLowerCase());
    }

    public void removeFilteredWord(String word) {
        this.filteredWords.remove(word.toLowerCase());
    }

    public void clearFilteredWords() {
        this.filteredWords.clear();
    }

    public void unload() {
        this.filteredWords.clear();
    }
}
