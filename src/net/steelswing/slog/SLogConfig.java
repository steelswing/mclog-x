/*
 * Ну вы же понимаете, что код здесь только мой?
 * Well, you do understand that the code here is only mine?
 */

package net.steelswing.slog;

import java.io.File;

/**
 * File: SLogConfig.java
 * Created on 25.01.2022, 16:27:32
 *
 * @author LWJGL2
 */
public class SLogConfig {

    public static boolean savingLogs = true;
    public static File loggerFolder = new File("logs");

    public static int logSizeMegabytes = 10;
    public static boolean appendPrefix = true;
}
