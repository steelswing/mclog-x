/*
 * Ну вы же понимаете, что код здесь только мой?
 * Well, you do understand that the code here is only mine?
 */

package net.steelswing.mclog;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import jline.console.ConsoleReader;

/**
 * File: LoggerHandler.java
 Created on 10.04.2022, 17:37:30
 *
 * @author LWJGL2
 */
public class LoggerHandler extends Logger {

    private final LogDispatcher dispatcher = new LogDispatcher(this);

    public LoggerHandler(String loggerName, ConsoleReader reader) {
        super(loggerName, null);
        setLevel(Level.ALL);

        try {
            ColorWriter consoleHandler = new ColorWriter(reader);
            consoleHandler.setLevel(Level.ALL);
            consoleHandler.setFormatter(new ConciseFormatter(true));
            addHandler(consoleHandler);
        } catch (Exception ex) {
            System.err.println("Could not register logger!");
            ex.printStackTrace();
        }

        dispatcher.start();
    }

    @Override
    public void log(LogRecord record) {
        dispatcher.queue(record);
    }

    public void doLog(LogRecord record) {
        super.log(record);
    }
    
    public void stop() {
        dispatcher.interrupt();
    }
}
