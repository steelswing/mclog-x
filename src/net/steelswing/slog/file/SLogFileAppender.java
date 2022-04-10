/*
 * Ну вы же понимаете, что код здесь только мой?
 * Well, you do understand that the code here is only mine?
 */

package net.steelswing.slog.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.steelswing.slog.Logger;
import net.steelswing.slog.SLogConfig;

/**
 * File: SLogFileAppender.java
 * Created on 25.01.2022, 16:26:31
 *
 * @author LWJGL2
 */
public class SLogFileAppender {

    private static BufferedWriter cachedFileWriter;

    private static final int SIZE_KB = 1024;
    private static final int SIZE_MB = SIZE_KB * SIZE_KB;
    private static final int SIZE_GB = SIZE_MB * SIZE_KB;
    private static final int SIZE_TB = SIZE_GB * SIZE_KB;

    private static final Object SYNC_OBJECT = new Object();

    public static void append(String string) {
        File loggerFolder = SLogConfig.loggerFolder;

        if (!loggerFolder.exists()) {
            loggerFolder.mkdirs();
        }

        File lastLogFile = new File(loggerFolder, "latest.log");

        try {
            if (!lastLogFile.exists()) {
                lastLogFile.createNewFile();
            }
        } catch (IOException ex) {
            Logger.error("Error when create log file:", ex);
            return;
        }
        synchronized (SYNC_OBJECT) {
            try {
                if (cachedFileWriter != null) {
                    cachedFileWriter.close();
                }
                cachedFileWriter = new BufferedWriter(new FileWriter(lastLogFile, true));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                cachedFileWriter.write(string + '\n');
                cachedFileWriter.flush();

                if (cachedFileWriter != null) {
                    cachedFileWriter.close();
                }
            } catch (IOException ex) {
                Logger.error("Error when appending line:", ex);
            }
//            System.out.println("File siz: " + (lastLogFile.length() / SIZE_MB) + ((lastLogFile.length() / SIZE_MB) > SLogConfig.logSizeMegabytes));
            if ((lastLogFile.length() / SIZE_MB) > SLogConfig.logSizeMegabytes) {
                try {
                    File newName = getFileName(0);
//                    System.out.println("rename to :" + newName);
                    lastLogFile.renameTo(newName);

                    lastLogFile = new File(loggerFolder, "latest.log");
                    if (!lastLogFile.exists()) {
                        lastLogFile.createNewFile();
                    }
                } catch (IOException ex) {
                    Logger.error("Error when create log file:", ex);
                }
            }
        }
    }

    private static File getFileName(int index) {
        File file = new File(SLogConfig.loggerFolder, getDateFormat() + "." + index + ".log");
        if (file.exists()) {
            return getFileName(index + 1);
        }
        return file;
    }

    private static String getDateFormat() {
        final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        return format.format(new Date());
    }
}
