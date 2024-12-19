package net.wheel.cutils.impl.module.CRACK;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import net.wheel.cutils.api.chatgamehandler.ChatGameHandler;
import net.wheel.cutils.api.module.Module;
import net.wheel.cutils.api.util.Timer;
import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;

public final class ChatGameResolverModule extends Module {

    private static final Logger LOGGER = LogManager.getLogger(ChatGameResolverModule.class);

    public final Value<Mode> mode = new Value<>("Mode", new String[] { "Mode", "M" }, "The timer mode to use",
            Mode.STATIC);
    public final Value<Float> staticTimer = new Value<>("Static Timer", new String[] { "Static Time" },
            "Time in seconds before sending CG answer", 5.0f, 0.01f, 60.0f, 0.01f);
    public final Value<Float> minRandomTimer = new Value<>("Random MIN", new String[] { "Min Rand Timer" },
            "Min time in seconds before sending CG answer", 5.0f, 0.01f, 60.0f, 0.01f);
    public final Value<Float> maxRandomTimer = new Value<>("Random MAX", new String[] { "Max Rand Timer" },
            "Max time in seconds before sending CG answer", 5.0f, 0.01f, 60.0f, 0.01f);

    private final ObjectList<String> recentMessages = new ObjectArrayList<>();
    private boolean chatGamesActive = false;
    private static boolean isWaitingForMatch = false;

    private final Timer gameTimer = new Timer();
    private final Timer timeoutTimer = new Timer();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private static ScheduledFuture<?> gameTask;

    public ChatGameResolverModule() {
        super("ChatGame", new String[] { "cgr" }, "Auto-solves chat games", "NONE", -1, ModuleType.CRACK);
    }

    @Override
    public void onEnable() {
        ChatGameHandler.setResolverInstance(this);
        ChatGameHandler.initialize();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        resetState();
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    private String removeServerTags(String input) {
        return input.replaceAll("\\[Server]", "").trim();
    }

    public double getTimer() {
        if (mode.getValue() == Mode.RANDOM) {
            return minRandomTimer.getValue() + (maxRandomTimer.getValue() - minRandomTimer.getValue()) * Math.random();
        } else {
            return staticTimer.getValue();
        }
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (!isEnabled()) {
            return;
        }

        if (event.getType() != ChatType.SYSTEM) {
            return;
        }

        ITextComponent message = event.getMessage();
        String msgText = message.getUnformattedText();
        msgText = stripColors(msgText);
        msgText = removeServerTags(msgText);

        if (msgText.startsWith("[D]")) {
            return;
        }

        if (msgText.contains("Chat Games")) {
            chatGamesActive = true;
            recentMessages.clear();
            timeoutTimer.reset();
        }

        if (chatGamesActive) {
            recentMessages.add(msgText);
            if (msgText.contains("got rewards!") || msgText.contains("Nobody got it in time")) {
                resetState();
                return;
            }

            if (recentMessages.size() >= 4) {
                StringBuilder concatenatedMessages = new StringBuilder();
                for (String recentMessage : recentMessages) {
                    concatenatedMessages.append(recentMessage).append(" ");
                }
                String finalMessage = concatenatedMessages.toString().trim();
                if (!finalMessage.contains("got rewards!") && !finalMessage.contains("Nobody got it in time")) {
                    scheduleGame(finalMessage);
                }
                chatGamesActive = false;
                recentMessages.clear();
            }
        }

        if (chatGamesActive && timeoutTimer.passed(5000)) {
            resetState();
            crack.INSTANCE.logChat("\u00a7c" + "CG" + "\u00a7f Timeout, task cancelled");
        }
    }

    private void scheduleGame(String concatenatedMessages) {
        double timer = getTimer();
        LOGGER.info("Received messages: {}", concatenatedMessages);
        processMessage(concatenatedMessages, timer);
    }

    private void processMessage(String message, double timer) {
        cancelGameTask();
        isWaitingForMatch = timer <= 5.0;
        long delay = (long) (timer * 1000);

        if (message.contains("The first to type '")) {
            gameTask = scheduler.schedule(() -> ChatGameHandler.handleChatGame(message), delay, TimeUnit.MILLISECONDS);
        } else if (message.contains("The first to solve '")) {
            gameTask = scheduler.schedule(() -> ChatGameHandler.handleMathGame(message), delay, TimeUnit.MILLISECONDS);
        } else if (message.contains("The first to unshuffle '")) {
            gameTask = scheduler.schedule(() -> ChatGameHandler.handleUnshuffleGame(message), delay,
                    TimeUnit.MILLISECONDS);
        } else if (message.contains("The first to unscramble '")) {
            gameTask = scheduler.schedule(() -> ChatGameHandler.handleUnscrambleGame(message), delay,
                    TimeUnit.MILLISECONDS);
        } else if (message.contains("The first to unreverse '")) {
            gameTask = scheduler.schedule(() -> ChatGameHandler.handleUnreverseGame(message), delay,
                    TimeUnit.MILLISECONDS);
        } else if (message.contains("The first to unshuffle & unreverse '")) {
            gameTask = scheduler.schedule(() -> ChatGameHandler.handleUnshuffleUnreverseGame(message), delay,
                    TimeUnit.MILLISECONDS);
        } else if (message.contains("The first to fill in '")) {
            gameTask = scheduler.schedule(() -> ChatGameHandler.handleFillInGame(message), delay,
                    TimeUnit.MILLISECONDS);
        } else if (!message.contains("got rewards!") && !message.contains("Nobody got it in time")) {
            gameTask = scheduler.schedule(() -> ChatGameHandler.handleTrivia(message), delay, TimeUnit.MILLISECONDS);
        } else {
            crack.INSTANCE.logChat("\u00a7c" + "CG" + "\u00a7f No game found, task cancelled");
        }

        gameTimer.reset();
    }

    private void cancelGameTask() {
        if (gameTask != null && !gameTask.isDone()) {
            gameTask.cancel(true);
        }
    }

    private void resetState() {
        cancelGameTask();
        chatGamesActive = false;
        recentMessages.clear();
        timeoutTimer.reset();
        gameTimer.reset();
    }

    private String stripColors(String input) {
        return input.replaceAll("(?i)" + TextFormatting.BLACK.toString(), "")
                .replaceAll("(?i)" + TextFormatting.DARK_BLUE.toString(), "")
                .replaceAll("(?i)" + TextFormatting.DARK_GREEN.toString(), "")
                .replaceAll("(?i)" + TextFormatting.DARK_AQUA.toString(), "")
                .replaceAll("(?i)" + TextFormatting.DARK_RED.toString(), "")
                .replaceAll("(?i)" + TextFormatting.DARK_PURPLE.toString(), "")
                .replaceAll("(?i)" + TextFormatting.GOLD.toString(), "")
                .replaceAll("(?i)" + TextFormatting.GRAY.toString(), "")
                .replaceAll("(?i)" + TextFormatting.DARK_GRAY.toString(), "")
                .replaceAll("(?i)" + TextFormatting.BLUE.toString(), "")
                .replaceAll("(?i)" + TextFormatting.GREEN.toString(), "")
                .replaceAll("(?i)" + TextFormatting.AQUA.toString(), "")
                .replaceAll("(?i)" + TextFormatting.RED.toString(), "")
                .replaceAll("(?i)" + TextFormatting.LIGHT_PURPLE.toString(), "")
                .replaceAll("(?i)" + TextFormatting.YELLOW.toString(), "")
                .replaceAll("(?i)" + TextFormatting.WHITE.toString(), "");
    }

    public void setWaitingForMatch(boolean waiting) {
        isWaitingForMatch = waiting;
    }

    private enum Mode {
        RANDOM, STATIC
    }
}
