package services.plasma.layeredyannounce;

import org.bukkit.plugin.java.JavaPlugin;
import services.plasma.layeredyannounce.commands.AnnounceCommand;
import services.plasma.layeredyannounce.managers.AnnouncementManager;
import services.plasma.layeredyannounce.managers.BossBarManager;
import services.plasma.layeredyannounce.managers.ConfigManager;
import services.plasma.layeredyannounce.tasks.AnnouncementTask;

public class LayeredyAnnouncePlugin extends JavaPlugin {

    private ConfigManager configManager;
    private AnnouncementManager announcementManager;
    private BossBarManager bossBarManager;
    private AnnouncementTask announcementTask;

    @Override
    public void onEnable() {
        getLogger().info("LayeredyAnnounce by plasma.services is starting...");

        this.configManager = new ConfigManager(this);
        this.bossBarManager = new BossBarManager(this);
        this.announcementManager = new AnnouncementManager(this, configManager, bossBarManager);

        configManager.loadConfig();

        getCommand("layeredyannounce").setExecutor(new AnnounceCommand(this));

        getServer().getPluginManager().registerEvents(new services.plasma.layeredyannounce.listeners.PlayerListener(this), this);

        if (configManager.isEnabled()) {
            startAnnouncementTask();
        }

        getLogger().info("LayeredyAnnounce has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("LayeredyAnnounce is shutting down...");

        if (announcementTask != null) {
            announcementTask.stop();
        }

        if (bossBarManager != null) {
            bossBarManager.clearAllBossBars();
        }

        getLogger().info("LayeredyAnnounce has been disabled!");
    }

    public void startAnnouncementTask() {
        if (announcementTask != null) {
            announcementTask.stop();
        }

        announcementTask = new AnnouncementTask(this, announcementManager);
        announcementTask.start();
        getLogger().info("Started announcement checking task with interval: " + configManager.getCheckInterval() + " seconds");
    }

    public void stopAnnouncementTask() {
        if (announcementTask != null) {
            announcementTask.stop();
            announcementTask = null;
            getLogger().info("Stopped announcement checking task");
        }
    }

    public void reload() {
        getLogger().info("Reloading LayeredyAnnounce configuration...");

        stopAnnouncementTask();

        bossBarManager.clearAllBossBars();

        configManager.loadConfig();

        if (configManager.isEnabled()) {
            startAnnouncementTask();
        }

        getLogger().info("LayeredyAnnounce configuration reloaded!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public AnnouncementManager getAnnouncementManager() {
        return announcementManager;
    }

    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }
}