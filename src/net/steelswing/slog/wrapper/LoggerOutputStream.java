/*
 * Ну вы же понимаете, что код здесь только мой?
 * Well, you do understand that the code here is only mine?
 */

package net.steelswing.slog.wrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import net.steelswing.slog.LogLevel;
import net.steelswing.slog.Logger;

/**
 * File: LoggerOutputStream.java
 * Created on 25.01.2022, 16:04:52
 *
 * @author LWJGL2
 */
public class LoggerOutputStream extends ByteArrayOutputStream {

    private final String separator = System.getProperty("line.separator");
    private final LogLevel level;

    public LoggerOutputStream(LogLevel level) {
        super();
        this.level = level;
    }

    @Override
    public void flush() throws IOException {
        synchronized (this) {
            super.flush();
            String record = this.toString();
            super.reset();
            if ((record.length() > 0) && (!record.equals(separator))) {
                Logger.log(level, record);
            }
        }
    }
}
