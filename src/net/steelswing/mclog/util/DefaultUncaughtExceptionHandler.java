/*
 * Ну вы же понимаете, что код здесь только мой?
 * Well, you do understand that the code here is only mine?
 */

package net.steelswing.mclog.util;

import java.lang.Thread.UncaughtExceptionHandler;
import net.steelswing.slog.Logger;

/**
 *
 * @author MrJavaCoder
 */
public class DefaultUncaughtExceptionHandler implements UncaughtExceptionHandler {


    public DefaultUncaughtExceptionHandler() {
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Logger.error("Caught previously unhandled exception :", throwable);
    }
}
