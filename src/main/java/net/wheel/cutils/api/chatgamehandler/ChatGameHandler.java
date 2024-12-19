package net.wheel.cutils.api.chatgamehandler;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import net.wheel.cutils.crack;
import net.wheel.cutils.impl.module.CRACK.ChatGameResolverModule;

public class ChatGameHandler {

    private static final ObjectOpenHashSet<String> gameLibrary = new ObjectOpenHashSet<>();
    private static final Object2ObjectOpenHashMap<String, String> triviaMap = new Object2ObjectOpenHashMap<>();
    private static ChatGameResolverModule resolverInstance;

    public static void setResolverInstance(ChatGameResolverModule resolver) {
        resolverInstance = resolver;
    }

    public static void initialize() {
        loadGameLibrary();
        loadTriviaData();
    }

    private static void loadGameLibrary() {
        gameLibrary.clear();
        if (crack.INSTANCE.getConfigManager() != null
                && crack.INSTANCE.getConfigManager().getChatGameConfig() != null) {
            gameLibrary.addAll(crack.INSTANCE.getConfigManager().getChatGameConfig().getGameLibrary());
            crack.INSTANCE.getLogger().log(Level.INFO, "Loaded " + gameLibrary.size() + " entries from game library");
        } else {
            crack.INSTANCE.getLogger().log(Level.SEVERE,
                    "Failed to load game library: ConfigManager or ChatGameConfig is null");
        }
    }

    private static void loadTriviaData() {
        if (crack.INSTANCE.getConfigManager() != null
                && crack.INSTANCE.getConfigManager().getChatGameConfig() != null) {
            triviaMap.putAll(crack.INSTANCE.getConfigManager().getChatGameConfig().getTriviaMap());
            crack.INSTANCE.getLogger().log(Level.INFO, "Loaded " + triviaMap.size() + " trivia questions");
        } else {
            crack.INSTANCE.getLogger().log(Level.SEVERE,
                    "Failed to load trivia data: ConfigManager or ChatGameConfig is null");
        }
    }

    public static void handleTrivia(String message) {
        for (Object2ObjectOpenHashMap.Entry<String, String> entry : triviaMap.object2ObjectEntrySet()) {
            if (message.contains(entry.getKey())) {
                String answer = entry.getValue();
                sendChatMessage(answer);
                return;
            }
        }
        crack.INSTANCE.getLogger().log(Level.INFO, "No matching trivia question found");
    }

    public static void handleChatGame(String message) {
        String target = extractTarget(message, "type");
        if (target != null) {
            sendChatMessage(target);
        }
    }

    public static void handleMathGame(String message) {
        String expression = extractTarget(message, "solve");
        if (expression != null) {
            double result = evaluateExpression(expression);
            DecimalFormat df = new DecimalFormat("#");
            sendChatMessage(df.format(result));
        }
    }

    public static void handleUnshuffleGame(String message) {
        String target = extractTarget(message, "unshuffle");
        if (target != null) {
            String result = findCorrectUnshuffle(target);
            if (result != null) {
                sendChatMessage(result);
            }
        }
    }

    public static void handleUnscrambleGame(String message) {
        String target = extractTarget(message, "unscramble");
        if (target != null) {
            String result = findCorrectUnscramble(target);
            if (result != null) {
                sendChatMessage(result);
            }
        }
    }

    public static void handleUnreverseGame(String message) {
        String target = extractTarget(message, "unreverse");
        if (target != null) {
            String result = findCorrectUnreverse(target);
            if (result != null) {
                sendChatMessage(result);
            }
        }
    }

    public static void handleUnshuffleUnreverseGame(String message) {
        String target = extractTarget(message, "unshuffle & unreverse");
        if (target != null) {
            String result = findCorrectUnshuffleUnreverse(target);
            if (result != null) {
                sendChatMessage(result);
            }
        }
    }

    public static void handleFillInGame(String message) {
        String target = extractTarget(message, "fill in");
        if (target != null) {
            String result = findCorrectFillIn(target);
            if (result != null) {
                sendChatMessage(result);
            }
        }
    }

    private static String extractTarget(String message, String action) {
        int startIndex = message.indexOf(action + " '") + action.length() + 2;
        int endIndex = message.lastIndexOf("'");
        if (startIndex > -1 && endIndex > -1) {
            return message.substring(startIndex, endIndex).replace("'", "").replace("[Server]", "")
                    .replaceAll("\\s+", " ").trim();
        }
        return null;
    }

    private static String findCorrectUnshuffle(String shuffled) {
        String[] shuffledWordsArray = shuffled.split("\\s+");
        Arrays.sort(shuffledWordsArray);
        ObjectList<String> shuffledWords = new ObjectArrayList<>(Arrays.asList(shuffledWordsArray));

        for (String entry : gameLibrary) {
            String[] entryWordsArray = entry.split("\\s+");
            Arrays.sort(entryWordsArray);
            ObjectList<String> entryWords = new ObjectArrayList<>(Arrays.asList(entryWordsArray));
            if (shuffledWords.equals(entryWords)) {
                return entry;
            }
        }
        return null;
    }

    private static String findCorrectUnscramble(String scrambled) {
        char[] scrambledChars = scrambled.toLowerCase().toCharArray();
        Arrays.sort(scrambledChars);
        String sortedScrambled = new String(scrambledChars);

        for (String entry : gameLibrary) {
            char[] entryChars = entry.toLowerCase().toCharArray();
            Arrays.sort(entryChars);
            String sortedEntry = new String(entryChars);
            if (sortedScrambled.equals(sortedEntry)) {
                return entry;
            }
        }
        return null;
    }

    private static String findCorrectUnreverse(String reversed) {
        String unreversed = new StringBuilder(reversed).reverse().toString();

        for (String entry : gameLibrary) {
            if (unreversed.equals(entry)) {
                return entry;
            }
        }
        return null;
    }

    private static String findCorrectUnshuffleUnreverse(String shuffledReversed) {
        String unreversed = new StringBuilder(shuffledReversed).reverse().toString();
        String[] unreversedWordsArray = unreversed.split("\\s+");
        Arrays.sort(unreversedWordsArray);
        ObjectList<String> unreversedWords = new ObjectArrayList<>(Arrays.asList(unreversedWordsArray));

        for (String entry : gameLibrary) {
            String[] entryWordsArray = entry.split("\\s+");
            Arrays.sort(entryWordsArray);
            ObjectList<String> entryWords = new ObjectArrayList<>(Arrays.asList(entryWordsArray));
            if (unreversedWords.equals(entryWords)) {
                return entry;
            }
        }
        return null;
    }

    private static String findCorrectFillIn(String fillIn) {
        for (String entry : gameLibrary) {
            if (matchesFillInPattern(entry, fillIn)) {
                return extractFillInLetters(entry, fillIn);
            }
        }
        return null;
    }

    private static boolean matchesFillInPattern(String entry, String fillIn) {
        if (entry.length() != fillIn.length()) {
            return false;
        }
        for (int i = 0; i < entry.length(); i++) {
            if (fillIn.charAt(i) != '_' && fillIn.charAt(i) != entry.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private static String extractFillInLetters(String entry, String fillIn) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < entry.length(); i++) {
            if (fillIn.charAt(i) == '_') {
                result.append(entry.charAt(i));
            } else if (fillIn.charAt(i) == ' ') {
                result.append(' ');
            }
        }
        return result.toString();
    }

    private static void sendChatMessage(String message) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player != null) {
            player.sendChatMessage(message);
            if (resolverInstance != null) {
                resolverInstance.setWaitingForMatch(false);
            }
        }
    }

    private static double evaluateExpression(String expression) {
        String[] parts;
        char operator = ' ';
        if (expression.contains(" x ")) {
            parts = expression.split(" x ");
            operator = '*';
        } else if (expression.contains(" + ")) {
            parts = expression.split(" \\+ ");
            operator = '+';
        } else if (expression.contains(" - ")) {
            parts = expression.split(" - ");
            operator = '-';
        } else if (expression.contains(" / ")) {
            parts = expression.split(" / ");
            operator = '/';
        } else {
            throw new IllegalArgumentException("Unknown operator in expression: " + expression);
        }

        double operand1 = Double.parseDouble(parts[0].trim());
        double operand2 = Double.parseDouble(parts[1].trim());

        switch (operator) {
            case '*':
                return operand1 * operand2;
            case '+':
                return operand1 + operand2;
            case '-':
                return operand1 - operand2;
            case '/':
                return operand1 / operand2;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }
}
