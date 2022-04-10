
package net.steelswing.slog;

import java.io.PrintStream;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.steelswing.slog.file.SLogFileAppender;
import net.steelswing.slog.wrapper.ForwardLogHandler;
import net.steelswing.slog.wrapper.LoggerOutputStream;

public class Logger {

    public static final String DEF_STRING = "DEF";
    private static volatile boolean inited;

    private static PrintStream systemOut;
    private static PrintStream systemErr;

    public static boolean debugEnabled;
    public static LogAppender appender;

    private Logger() {
    }

    private static void init() {
        if (inited) {
            return;
        }
        if (!inited) {
            inited = true;
        }

        systemOut = System.out;
        systemErr = System.err;

        System.setOut(new PrintStream(new LoggerOutputStream(LogLevel.INFO), true));
        System.setErr(new PrintStream(new LoggerOutputStream(LogLevel.WARN), true));

        // default java logger 
        java.util.logging.Logger global = java.util.logging.Logger.getLogger("");
        global.setUseParentHandlers(false);
        for (java.util.logging.Handler handler : global.getHandlers()) {
            global.removeHandler(handler);
        }
        global.addHandler(new ForwardLogHandler());
    }

    public static void info(Object... objs) {
        log(LogLevel.INFO, objs);
    }

    public static void error(Object... objs) {
        log(LogLevel.ERROR, objs);
    }

    public static void warn(Object... objs) {
        log(LogLevel.WARN, objs);
    }

    public static void debug(Object... objs) {
        log(LogLevel.DEBUG, objs);
    }

    public static void log(Object... objs) {
        LogLevel level = LogLevel.INFO;
        for (Object obj : objs) {
            if (obj instanceof Throwable) {
                level = LogLevel.ERROR;
                break;
            }
        }

        log(level, objs);
    }

    public static void log(String obj1, Object... objs) {
        Object[] objects = new Object[objs.length + 1];
        objects[0] = obj1;
        System.arraycopy(objs, 0, objects, 1, objs.length);
        log(objects);
    }

    public static void log(LogLevel level, Object... objs) {
        init();
        if (!debugEnabled && level == LogLevel.DEBUG) {
            return;
        }

        String timeFormat = getDateFormat();

        StringBuilder buffer = new StringBuilder();
        boolean notEmpty = false;

        for (int i = 0; i < objs.length; i++) {
            Object obj = objs[i];
            if (obj == null) {
                continue;
            }

            if (obj instanceof Throwable) {
                ((Throwable) obj).printStackTrace();
            } else {
                buffer.append(String.valueOf(obj) + " ");
                notEmpty = true;
            }
        }
        if (notEmpty) {
            String prefix = SLogConfig.appendPrefix ? (timeFormat + " " + level + "]: ") : "";
            print(prefix + buffer.toString());
        }
    }


    private static final ExecutorService executor = Executors.newFixedThreadPool(5);

    private static void print(final String string) {
        if (appender != null) {
            if (appender.append(string)) {
                systemOut.println(string);
                SLogFileAppender.append(string);
            }
        } else {
            systemOut.println(string);
            SLogFileAppender.append(string);
        }
    }

//    public static void test() {
//            SLogFileAppender.append("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaasfgvhbjnkml,;.lkmjnhbgvfcdxfcgvhbnjkaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaasfgvhbjnkml,;.lkmjnhbgvfcdxfcgvhbnjkaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaasfgvhbjnkml,;.lkmjnhbgvfcdxfcgvhbnjkaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaasfgvhbjnkml,;.lkmjnhbgvfcdxfcgvhbnjkaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaasfgvhbjnkml,;.lkmjnhbgvfcdxfcgvhbnjkaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaasfgvhbjnkml,;.lkmjnhbgvfcdxfcgvhbnjkaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaasfgvhbjnkml,;.lkmjnhbgvfcdxfcgvhbnjkaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaasfgvhbjnkml,;.lkmjnhbgvfcdxfcgvhbnjkaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaasfgvhbjnkml,;.lkmjnhbgvfcdxfcgvhbnjkaaaaaaaaaaaaaaaaaaaaaaaaaaaaasfgvhbjnkml,;.lkmjnhbgvfcdxfcgvhbnjk");
//    }

    private static String getDateFormat() {
        final Date date = new Date();
        final String timeFormat = "[" + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "";

        return timeFormat;
    }


    public static interface LogAppender {

        public boolean append(String message);
    }



}
