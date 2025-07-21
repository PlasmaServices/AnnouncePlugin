package services.plasma.layeredyannounce.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import services.plasma.layeredyannounce.LayeredyAnnouncePlugin;
import services.plasma.layeredyannounce.managers.AnnouncementManager;

public class AnnouncementTask {

    private final LayeredyAnnouncePlugin plugin;
    private final AnnouncementManager announcementManager;
    private BukkitTask task;

    public AnnouncementTask(LayeredyAnnouncePlugin plugin, AnnouncementManager announcementManager) {
        this.plugin = plugin;
        this.announcementManager = announcementManager;
    }

    public void start() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }

        int interval = plugin.getConfigManager().getCheckInterval();

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getConfigManager().isEnabled()) {
                    if (plugin.getConfigManager().isDebugEnabled()) {
                        plugin.getLogger().info("Plugin is disabled, skipping announcement check");
                    }
                    return;
                }

                if (plugin.getConfigManager().isDebugEnabled()) {
                    plugin.getLogger().info("Running scheduled announcement check");
                }

                announcementManager.checkForAnnouncements()
                        .exceptionally(throwable -> {
                            plugin.getLogger().warning("Error during scheduled announcement check: " + throwable.getMessage());
                            if (plugin.getConfigManager().isDebugEnabled()) {
                                throwable.printStackTrace();
                            }
                            return null;
                        });
            }
        }.runTaskTimerAsynchronously(plugin, 20L, interval * 20L);

        if (plugin.getConfigManager().isDebugEnabled()) {
            plugin.getLogger().info("Started announcement checking task with " + interval + " second interval");
        }
    }

    public void stop() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
            task = null;

            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getLogger().info("Stopped announcement checking task");
            }
        }
    }

    public boolean isRunning() {
        return task != null && !task.isCancelled();
    }
}