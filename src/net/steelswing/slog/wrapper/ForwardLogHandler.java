/*
Ну вы же понимаете, что код здесь только мой?
Well, you do understand that the code here is only mine?
 */

package net.steelswing.slog.wrapper;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import net.steelswing.slog.Logger;

/**
 * File: ForwardLogHandler.java
 * Created on 25.01.2022, 16:07:38
 *
 * @author LWJGL2
 */
public class ForwardLogHandler extends ConsoleHandler {

    @Override
    public void publish(LogRecord record) {
        Throwable exception = record.getThrown();
        Level level = record.getLevel();
        String message = getFormatter().formatMessage(record);

        if (level == Level.SEVERE) {
            Logger.error(message, exception);
        } else if (level == Level.WARNING) {
            Logger.warn(message, exception);
        } else if (level == Level.INFO) {
            Logger.info(message, exception);
        } else if (level == Level.CONFIG) {
            Logger.debug(message, exception);
        } else {
            Logger.info(message, exception);
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}
