/*
 * Ну вы же понимаете, что код здесь только мой?
 * Well, you do understand that the code here is only mine?
 */

package net.steelswing.mclog;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import jline.console.ConsoleReader;
import net.steelswing.mclog.util.DefaultUncaughtExceptionHandler;
import net.steelswing.slog.Logger;
import net.steelswing.slog.SLogConfig;
import org.fusesource.jansi.AnsiConsole;

/**
 *
 * @author MrJavaCoder
 */
public class ServerConsole {

    private List<InputListener> inputListeners = new ArrayList<>();

    protected boolean running = true;
    protected ConsoleReader reader;
    public static boolean useJLine = true;

    private LoggerHandler logger;

    public ServerConsole() throws Exception {
        SLogConfig.appendPrefix = false; // no append default prefix
        if (System.console() == null && System.getProperty("jline.terminal") == null) {
            System.setProperty("jline.terminal", "jline.UnsupportedTerminal");
            useJLine = false;
        }

        AnsiConsole.systemInstall();

        try {
            reader = new ConsoleReader(System.in, System.out);
            reader.setExpandEvents(false); // Avoid parsing exceptions for uncommonly used event designators
        } catch (IOException e) {
            try {
                // Try again with useJLine disabled for Windows users without C++ 2008 Redistributable
                System.setProperty("jline.terminal", "jline.UnsupportedTerminal");
                System.setProperty("user.language", "en");
                useJLine = false;
                reader = new ConsoleReader(System.in, System.out);
                reader.setExpandEvents(false);
            } catch (IOException ex) {
                Logger.warn((String) null, ex);
            }
        }

        Thread thread = new Thread("Server console handler") {
            @Override
            public void run() {
                // CraftBukkit start
                jline.console.ConsoleReader bufferedreader = reader;

                // MC-33041, SPIGOT-5538: if System.in is not valid due to javaw, then return
                try {
                    System.in.available();
                } catch (IOException ex) {
                    return;
                }
                // CraftBukkit end

                String s;

                try {
                    // CraftBukkit start - JLine disabling compatibility
                    while (running) {
                        if (useJLine) {
                            s = bufferedreader.readLine(">", null);
                        } else {
                            s = bufferedreader.readLine();
                        }

                        // SPIGOT-5220: Throttle if EOF (ctrl^d) or stdin is /dev/null
                        if (s == null) {
                            try {
                                Thread.sleep(50L);
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }
                            continue;
                        }
                        if (s.trim().length() > 0) { // Trim to filter lines which are just spaces
                            invokeCommand(s);
                        }
                        // CraftBukkit end
                    }

                } catch (IOException ioexception) {
                    Logger.error("Exception handling console input", ioexception);
                }

                if (logger != null) {
                    for (Handler handler : logger.getHandlers()) {
                        handler.close();
                    }
                    logger.stop();
                }
            }
        };

        java.util.logging.Logger global = java.util.logging.Logger.getLogger("");
        global.setUseParentHandlers(false);
        for (java.util.logging.Handler handler : global.getHandlers()) {
            global.removeHandler(handler);
        }
        ColorWriter consoleHandler = new ColorWriter(reader);
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(new ConciseFormatter(true));
        global.addHandler(consoleHandler);

        logger = new LoggerHandler("SLogger", reader);
        System.setErr(new PrintStream(new LoggingOutputStream(logger, Level.SEVERE), true));
        System.setOut(new PrintStream(new LoggingOutputStream(logger, Level.INFO), true));

        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler());
        thread.start();

    }

    /**
     * Internal method
     *
     * @param command
     */
    protected void invokeCommand(String command) {
        for (int i = 0; i < inputListeners.size(); i++) {
            inputListeners.get(i).onCommand(this, command);
        }
    }

    public ServerConsole addListener(InputListener listener) {
        inputListeners.add(listener);
        return this;
    }

    /**
     * Method for remove InputListener
     *
     * @param listener
     * @return true, if listener removed.
     */
    public boolean removeListener(InputListener listener) {
        for (int i = 0; i < inputListeners.size(); i++) {
            if (inputListeners.get(i).equals(listener)) {
                inputListeners.remove(i);
                return true;
            }
        }

        return false;
    }

    public List<InputListener> getInputListeners() {
        return inputListeners;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public ConsoleReader getReader() {
        return reader;
    }

    public void setReader(ConsoleReader reader) {
        this.reader = reader;
    }

    public static boolean isUseJLine() {
        return useJLine;
    }

    public static void setUseJLine(boolean useJLine) {
        ServerConsole.useJLine = useJLine;
    }

    public LoggerHandler getLogger() {
        return logger;
    }
}
