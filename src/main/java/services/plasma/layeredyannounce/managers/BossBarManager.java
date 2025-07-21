package services.plasma.layeredyannounce.managers;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import services.plasma.layeredyannounce.LayeredyAnnouncePlugin;
import services.plasma.layeredyannounce.models.Announcement;

import java.util.HashSet;
import java.util.Set;

public class BossBarManager {

    private final LayeredyAnnouncePlugin plugin;
    private final Set<BossBar> activeBossBars;
    private final Set<BukkitTask> activeTasks;

    public BossBarManager(LayeredyAnnouncePlugin plugin) {
        this.plugin = plugin;
        this.activeBossBars = new HashSet<>();
        this.activeTasks = new HashSet<>();
    }

    public void displayAnnouncement(Announcement announcement) {
        if (announcement == null || announcement.getMessage() == null || announcement.getMessage().trim().isEmpty()) {
            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getLogger().warning("Attempted to display null or empty announcement");
            }
            return;
        }

        BossBar bossBar = createBossBar(announcement.getMessage());

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("layeredyannounce.see")) {
                bossBar.addPlayer(player);
            }
        }

        activeBossBars.add(bossBar);

        if (plugin.getConfigManager().isDebugEnabled()) {
            plugin.getLogger().info("Displaying announcement to " + bossBar.getPlayers().size() + " players: " + announcement.getMessage());
        }

        int duration = plugin.getConfigManager().getBossBarDuration();
        BukkitTask removeTask = new BukkitRunnable() {
            @Override
            public void run() {
                removeBossBar(bossBar);
            }
        }.runTaskLater(plugin, duration * 20L);

        activeTasks.add(removeTask);
    }

    private BossBar createBossBar(String message) {
        BarColor color = parseBarColor(plugin.getConfigManager().getBossBarColor());
        BarStyle style = parseBarStyle(plugin.getConfigManager().getBossBarStyle());

        return Bukkit.createBossBar(message, color, style);
    }

    private BarColor parseBarColor(String colorString) {
        try {
            return BarColor.valueOf(colorString.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid boss bar color: " + colorString + ". Using BLUE as default.");
            return BarColor.BLUE;
        }
    }

    private BarStyle parseBarStyle(String styleString) {
        try {
            return BarStyle.valueOf(styleString.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid boss bar style: " + styleString + ". Using SOLID as default.");
            return BarStyle.SOLID;
        }
    }

    private void removeBossBar(BossBar bossBar) {
        if (bossBar != null && activeBossBars.contains(bossBar)) {
            bossBar.removeAll();
            activeBossBars.remove(bossBar);

            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getLogger().info("Removed boss bar: " + bossBar.getTitle());
            }
        }
    }

    public void clearAllBossBars() {
        for (BukkitTask task : activeTasks) {
            if (!task.isCancelled()) {
                task.cancel();
            }
        }
        activeTasks.clear();

        for (BossBar bossBar : activeBossBars) {
            bossBar.removeAll();
        }
        activeBossBars.clear();

        if (plugin.getConfigManager().isDebugEnabled()) {
            plugin.getLogger().info("Cleared all boss bars");
        }
    }

    public void addPlayerToBossBars(Player player) {
        if (player.hasPermission("layeredyannounce.see")) {
            for (BossBar bossBar : activeBossBars) {
                bossBar.addPlayer(player);
            }
        }
    }

    public void removePlayerFromBossBars(Player player) {
        for (BossBar bossBar : activeBossBars) {
            bossBar.removePlayer(player);
        }
    }

    public int getActiveBossBarCount() {
        return activeBossBars.size();
    }
}