/*
 * Ну вы же понимаете, что код здесь только мой?
 * Well, you do understand that the code here is only mine?
 */

package net.steelswing.mclog.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.steelswing.mclog.ChatColor;
import net.steelswing.mclog.ServerConsole;

/**
 *
 * @author MrJavaCoder
 */
public class TestConsole {

    private static final Logger LOG = Logger.getLogger(TestConsole.class.getName());


    public static void main(String[] args) throws Exception {
        ServerConsole console = new ServerConsole().addListener((ServerConsole cns, String cmd) -> {
            if (cmd.equalsIgnoreCase("stop")) {
                System.out.println("stopping");
                cns.setRunning(false); // stop console handler
            }
        });
        AtomicInteger counter = new AtomicInteger(0);
        while (console.isRunning()) {
            if (counter.incrementAndGet() % 10 == 0) {
                LOG.info(ChatColor.AQUA + "Hello world!");
                try {
                    throw new RuntimeException(ChatColor.RED + "Hello debug world!");
                } catch (Exception e) {
                    console.getLogger().log(Level.SEVERE, "Error", e);
                }
            }
            Thread.sleep(20);
        }

    }
}
