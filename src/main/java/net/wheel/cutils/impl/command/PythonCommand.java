package net.wheel.cutils.impl.command;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.wheel.cutils.api.command.Command;
import net.wheel.cutils.api.gui.hud.component.ConsoleComponent;
import net.wheel.cutils.crack;

public final class PythonCommand extends Command {

    private final ExecutorService executorService;
    private Process pythonProcess;
    private BufferedWriter pythonInput;

    public PythonCommand() {
        super("Python", new String[] { "Py" }, "Python interpreter and script runner", "py <path|code|pip|cutils>");
        this.executorService = Executors.newCachedThreadPool();
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ", 2);
        final String argument = split.length > 1 ? split[1] : "";

        executorService.submit(new ScriptRunnable(argument));
    }

    public class ScriptRunnable implements Runnable {
        private final String argument;

        public ScriptRunnable(String argument) {
            this.argument = argument;
        }

        @Override
        public void run() {
            try {
                if (argument.startsWith("cutils")) {
                    String command = argument.substring(7).trim();
                    crack.INSTANCE.getCommandManager().cutils(command);
                    return;
                }

                String pythonPath = "python";
                String[] command;
                File workingDirectory;

                if (argument.equals("killthreads")) {
                    handleKillThreads();
                    return;
                } else if (argument.startsWith("pip")) {
                    command = buildPipCommand(argument, pythonPath);
                    workingDirectory = getWorkingDirectory();
                    executeCommand(command, workingDirectory);
                } else if (argument.endsWith(".py")) {
                    command = new String[] { pythonPath, argument };
                    workingDirectory = new File(argument).getParentFile();
                    executeCommand(command, workingDirectory);
                } else {
                    if (pythonProcess == null || !pythonProcess.isAlive()) {
                        startPythonInterpreter();
                    }

                    sendToPythonInterpreter(argument);
                }
            } catch (Exception e) {
                logToConsole("Error: " + e.getMessage(), true);
            }
        }

        private void startPythonInterpreter() throws IOException {
            String pythonPath = "python";
            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, "-i", "-c", "");
            pythonProcess = processBuilder.start();

            pythonInput = new BufferedWriter(new OutputStreamWriter(pythonProcess.getOutputStream()));
            crack.INSTANCE.getConsoleComponent().setRunningProcess(pythonProcess);

            Thread outputThread = new Thread(() -> readStream(pythonProcess.getInputStream(), false));
            Thread errorThread = new Thread(() -> readStream(pythonProcess.getErrorStream(), true));

            outputThread.start();
            errorThread.start();
        }

        private void sendToPythonInterpreter(String command) throws IOException {
            if (pythonInput != null) {
                pythonInput.write(command + "\n");
                pythonInput.flush();
            }
        }

        private void handleKillThreads() throws Exception {
            String killCommand;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                killCommand = "taskkill /F /IM python.exe";
            } else {
                killCommand = "pkill -f python";
            }

            ProcessBuilder processBuilder = new ProcessBuilder(killCommand.split(" "));
            Process process = processBuilder.start();
            process.waitFor();
            logToConsole("dead thread", false);
        }

        private String[] buildPipCommand(String argument, String pythonPath) {
            String[] pipArgs = argument.substring(4).split(" ");
            String[] command = new String[pipArgs.length + 3];
            command[0] = pythonPath;
            command[1] = "-m";
            command[2] = "pip";
            System.arraycopy(pipArgs, 0, command, 3, pipArgs.length);
            return command;
        }

        private File getWorkingDirectory() {
            String userHomeDir = System.getProperty("user.home");
            String scriptDirectoryPath = userHomeDir + "\\Documents\\crack";
            File workingDirectory = new File(scriptDirectoryPath);
            if (!workingDirectory.exists()) {
                workingDirectory.mkdirs();
            }
            return workingDirectory;
        }

        private void executeCommand(String[] command, File workingDirectory) throws Exception {
            ProcessBuilder processBuilder = new ProcessBuilder(command).directory(workingDirectory);
            Process process = processBuilder.start();
            crack.INSTANCE.getConsoleComponent().setRunningProcess(process);

            Thread outputThread = new Thread(() -> readStream(process.getInputStream(), false));
            Thread errorThread = new Thread(() -> readStream(process.getErrorStream(), true));

            outputThread.start();
            errorThread.start();

            int exitCode = process.waitFor();
            outputThread.join();
            errorThread.join();

            if (exitCode != 0) {
                logToConsole("Error code: " + exitCode, true);
            }
        }

        private void readStream(InputStream inputStream, boolean isErrorStream) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logToConsole(line, isErrorStream);
                }
            } catch (Exception e) {
                logToConsole("Error reading stream: " + e.getMessage(), true);
            }
        }

        private void logToConsole(String message, boolean isErrorStream) {
            ConsoleComponent.addConsoleOutput(message, isErrorStream ? "\u00A7c" : "\u00A7f");
        }
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(3, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
