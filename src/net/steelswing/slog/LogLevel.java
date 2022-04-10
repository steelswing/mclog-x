/*
 * Ну вы же понимаете, что код здесь только мой?
 * Well, you do understand that the code here is only mine?
 */

package net.steelswing.slog;

/**
 * File: LogLevel.java
 * Created on 25.01.2022, 16:05:04
 *
 * @author LWJGL2
 */
public enum LogLevel {
    INFO("STD"), ERROR("ERROR"), WARN("WARN"), DEBUG("DEBUG");
    public final String prefix;

    private LogLevel(String prefix) {
        this.prefix = prefix;
    }
    
}
