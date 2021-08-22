package eu.rex2go.chat2go.util;

import eu.rex2go.chat2go.Chat2Go;
import org.bukkit.scheduler.BukkitRunnable;

public class ThreadUtil {
    /**
     * Executes a runnable asynchronously.
     *
     * @param runnable The runnable to be executed.
     */
    public static void async(Runnable runnable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskAsynchronously(Chat2Go.getInstance());
    }

    /**
     * Executes a runnable asynchronously.
     *
     * @param runnable The runnable to be executed.
     * @param delay    The amount of ticks to delay the execution.
     */
    public static void async(Runnable runnable, long delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLaterAsynchronously(Chat2Go.getInstance(), delay);
    }

    /**
     * Executes a runnable synchronously.
     *
     * @param runnable The runnable to be executed.
     */
    public static void sync(Runnable runnable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTask(Chat2Go.getInstance());
    }

    public static void sync(Runnable runnable, long delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(Chat2Go.getInstance(), delay);
    }
}
