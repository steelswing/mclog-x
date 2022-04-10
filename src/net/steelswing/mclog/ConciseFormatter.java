/*
Ну вы же понимаете, что код здесь только мой?
Well, you do understand that the code here is only mine?
 */

package net.steelswing.mclog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
/**
 * File: ConciseFormatter.java
 * Created on 10.04.2022, 17:40:26
 *
 * @author LWJGL2
 */
public class ConciseFormatter extends Formatter {

    private final DateFormat date = new SimpleDateFormat(System.getProperty("log_date_format", "HH:mm:ss"));
    private final boolean coloured;

    public ConciseFormatter(boolean coloured) {
        this.coloured = coloured;
    }

    @Override
    public String format(LogRecord record) {
        StringBuilder formatted = new StringBuilder();

        formatted.append("[");
        formatted.append(date.format(record.getMillis()));
        formatted.append(" ");
        appendLevel(formatted, record.getLevel());
        formatted.append("] ");
        formatted.append(formatMessage(record));
        formatted.append('\n');

        if (record.getThrown() != null) {
            StringWriter writer = new StringWriter();
            record.getThrown().printStackTrace(new PrintWriter(writer));
            formatted.append(writer);
        }

        return formatted.toString();
    }

    private void appendLevel(StringBuilder builder, Level level) {
        if (!coloured) {
            builder.append(level.getLocalizedName());
            return;
        }

        ChatColor color;

        if (level == Level.INFO) {
            color = ChatColor.BLUE;
        } else if (level == Level.WARNING) {
            color = ChatColor.YELLOW;
        } else if (level == Level.SEVERE) {
            color = ChatColor.RED;
        } else {
            color = ChatColor.AQUA;
        }

        builder.append(color).append(level.getLocalizedName()).append(ChatColor.RESET);
    }
}