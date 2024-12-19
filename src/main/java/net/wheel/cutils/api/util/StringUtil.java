package net.wheel.cutils.api.util;

import java.util.logging.Level;

import net.wheel.cutils.crack;

public final class StringUtil {

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            crack.INSTANCE.getLogger().log(Level.WARNING, "Number format exception thrown when parsing int.");
        }
        return false;
    }

    public static boolean isFloat(String s) {
        try {
            Float.parseFloat(s);
            return true;
        } catch (Exception e) {
            crack.INSTANCE.getLogger().log(Level.WARNING, "Number format exception thrown when parsing float.");
        }
        return false;
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            crack.INSTANCE.getLogger().log(Level.WARNING, "Number format exception thrown when parsing double.");
        }
        return false;
    }

    public static boolean isBoolean(String s) {
        try {
            return Boolean.parseBoolean(s);
        } catch (Exception e) {
            crack.INSTANCE.getLogger().log(Level.WARNING, "Number format exception thrown when parsing boolean.");
        }
        return false;
    }

    public static boolean isLong(String s, int radix) {
        try {
            Long.parseLong(s, radix);
            return true;
        } catch (Exception e) {
            crack.INSTANCE.getLogger().log(Level.WARNING, "Number format exception thrown when parsing long.");
        }
        return false;
    }

    public static boolean isNumber(String s) {
        return isInt(s) || isFloat(s) || isDouble(s) || isLong(s, 16);
    }

    public static float similarityLength(String first, String second) {
        return (float) Math.abs(first.length() - second.length()) / 100;
    }

    public static boolean findMatching(String first, String second) {
        return second.toLowerCase().contains(first.toLowerCase());
    }

    public static double levenshteinDistance(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) {
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0;
        }
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }

    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    public static String intToRoman(int num) {
        StringBuilder sb = new StringBuilder();
        int times = 0;
        String[] romans = new String[] { "I", "IV", "V", "IX", "X", "XL", "L",
                "XC", "C", "CD", "D", "CM", "M" };
        int[] ints = new int[] { 1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500,
                900, 1000 };
        for (int i = ints.length - 1; i >= 0; i--) {
            times = num / ints[i];
            num %= ints[i];
            while (times > 0) {
                sb.append(romans[i]);
                times--;
            }
        }
        return sb.toString();
    }

    public static String insertAt(String original, String insertion, int position) {
        return new StringBuilder(original.length() + insertion.length())
                .append(original, 0, position)
                .append(insertion)
                .append(original, position, original.length())
                .toString();
    }

    public static String insertAt(String original, char insertion, int position) {
        return new StringBuilder(original.length() + 1)
                .append(original, 0, position)
                .append(insertion)
                .append(original, position, original.length())
                .toString();
    }

    public static String removeRange(String s, int start, int end) {
        return new StringBuilder(s.length() + start - end)
                .append(s, 0, start)
                .append(s, end, s.length())
                .toString();
    }
}
