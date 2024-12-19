package net.wheel.cutils.api.value;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.annotation.Nullable;

public class Regex {
    private String patternString;
    private Pattern pattern;

    public Regex() {
        this.setPatternString("");
    }

    public Regex(String patternString) {
        this.setPatternString(patternString);
    }

    public String getPatternString() {
        return this.patternString;
    }

    public void setPatternString(String patternString) {
        this.patternString = patternString;
        if (patternString.equals("")) {
            this.pattern = null;
            return;
        }

        try {
            this.pattern = Pattern.compile(patternString);
        } catch (PatternSyntaxException exception) {
            this.pattern = null;
        }
    }

    @Nullable
    public Pattern getPattern() {
        return this.pattern;
    }

    @Override
    public String toString() {
        return this.patternString;
    }
}
