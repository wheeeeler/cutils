package net.wheel.cutils.impl.config;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.config.Configurable;
import net.wheel.cutils.api.util.FileUtil;
import net.wheel.cutils.crack;

public final class ChatGameConfig extends Configurable {

    @Getter
    @Setter
    private Set<String> gameLibrary = new HashSet<>();
    @Getter
    @Setter
    private Map<String, String> triviaMap = new HashMap<>();

    private final File gameLibraryFile;
    private final File triviaFile;

    public ChatGameConfig(File dir) {
        super(FileUtil.createJsonFile(dir, "ChatGameLibrary"));

        gameLibraryFile = new File(dir, "game-library.txt");
        triviaFile = new File(dir, "trivia.txt");

        copyResourceFiles();

        loadGameLibrary();
        loadTriviaData();
    }

    @Override
    public void onLoad(JsonObject jsonObject) {
        super.onLoad(jsonObject);

        JsonArray gameArray = this.getJsonObject().getAsJsonArray("GameLibrary");
        if (gameArray != null) {
            for (JsonElement element : gameArray) {
                gameLibrary.add(element.getAsString().trim());
            }
        }

        JsonObject triviaObject = this.getJsonObject().getAsJsonObject("Trivia");
        if (triviaObject != null) {
            for (Map.Entry<String, JsonElement> entry : triviaObject.entrySet()) {
                triviaMap.put(entry.getKey(), entry.getValue().getAsString());
            }
        }

        crack.INSTANCE.getLogger().log(java.util.logging.Level.INFO,
                "Loaded " + gameLibrary.size() + " entries from game library");
        crack.INSTANCE.getLogger().log(java.util.logging.Level.INFO,
                "Loaded " + triviaMap.size() + " trivia questions");
    }

    @Override
    public void onSave() {
        JsonObject save = new JsonObject();
        JsonArray gameArray = new JsonArray();
        for (String game : gameLibrary) {
            gameArray.add(game);
        }
        save.add("GameLibrary", gameArray);

        JsonObject triviaObject = new JsonObject();
        for (Map.Entry<String, String> entry : triviaMap.entrySet()) {
            triviaObject.addProperty(entry.getKey(), entry.getValue());
        }
        save.add("Trivia", triviaObject);

        this.saveJsonObjectToFile(save);
    }

    private void copyResourceFiles() {
        copyResourceFile("/assets/cutils/CG/game-library.txt", gameLibraryFile);
        copyResourceFile("/assets/cutils/CG/trivia.txt", triviaFile);
    }

    private void copyResourceFile(String resourcePath, File destination) {
        if (!destination.exists()) {
            try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
                if (inputStream == null) {
                    crack.INSTANCE.getLogger().log(java.util.logging.Level.WARNING,
                            "Resource not found: " + resourcePath);
                    return;
                }

                Files.copy(inputStream, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                crack.INSTANCE.getLogger().log(java.util.logging.Level.INFO,
                        "Copied resource file to " + destination.getAbsolutePath());
            } catch (IOException e) {
                crack.INSTANCE.getLogger().log(java.util.logging.Level.SEVERE,
                        "Failed to copy resource file: " + resourcePath, e);
            }
        }
    }

    private void loadGameLibrary() {
        if (gameLibraryFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(gameLibraryFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    gameLibrary.add(line.trim());
                }
                crack.INSTANCE.getLogger().log(java.util.logging.Level.INFO,
                        "Loaded " + gameLibrary.size() + " entries from game library file");
            } catch (IOException e) {
                crack.INSTANCE.getLogger().log(java.util.logging.Level.SEVERE, "Failed to load game library file", e);
            }
        } else {
            crack.INSTANCE.getLogger().log(java.util.logging.Level.WARNING, "Game library file is missing");
        }
    }

    private void loadTriviaData() {
        if (triviaFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(triviaFile))) {
                String line;
                String currentQuestion = null;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("question:")) {
                        currentQuestion = line.substring(9).trim();
                    } else if (line.startsWith("answers:") && currentQuestion != null) {
                        String answer = reader.readLine().trim().replaceAll("\"", "");
                        triviaMap.put(currentQuestion, answer);
                    }
                }
                crack.INSTANCE.getLogger().log(java.util.logging.Level.INFO,
                        "Loaded " + triviaMap.size() + " trivia questions from file");
            } catch (IOException e) {
                crack.INSTANCE.getLogger().log(java.util.logging.Level.SEVERE, "Failed to load trivia file", e);
            }
        } else {
            crack.INSTANCE.getLogger().log(java.util.logging.Level.WARNING, "Trivia file is missing");
        }
    }

}
