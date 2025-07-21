package services.plasma.layeredyannounce.managers;

import org.bukkit.configuration.file.FileConfiguration;
import services.plasma.layeredyannounce.LayeredyAnnouncePlugin;

public class ConfigManager {

    private final LayeredyAnnouncePlugin plugin;
    private FileConfiguration config;

    public ConfigManager(LayeredyAnnouncePlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();

        validateConfig();
    }

    private void validateConfig() {
        boolean needsSave = false;

        if (!config.contains("enabled")) {
            config.set("enabled", true);
            needsSave = true;
        }

        if (!config.contains("user-id")) {
            config.set("user-id", "usr_test_1234567890");
            needsSave = true;
        }

        if (!config.contains("check-interval")) {
            config.set("check-interval", 60);
            needsSave = true;
        }

        if (!config.contains("api-url")) {
            config.set("api-url", "https://announce.layeredy.com/api/announce");
            needsSave = true;
        }

        if (!config.contains("bossbar.duration")) {
            config.set("bossbar.duration", 10);
            needsSave = true;
        }

        if (!config.contains("bossbar.color")) {
            config.set("bossbar.color", "BLUE");
            needsSave = true;
        }

        if (!config.contains("bossbar.style")) {
            config.set("bossbar.style", "SOLID");
            needsSave = true;
        }

        if (!config.contains("debug")) {
            config.set("debug", false);
            needsSave = true;
        }

        if (needsSave) {
            plugin.saveConfig();
        }
    }

    public boolean isEnabled() {
        return config.getBoolean("enabled", true);
    }

    public String getUserId() {
        return config.getString("user-id", "usr_test_1234567890");
    }

    public int getCheckInterval() {
        return Math.max(30, config.getInt("check-interval", 60)); // Minimum 30 seconds
    }

    public String getApiUrl() {
        return config.getString("api-url", "https://announce.layeredy.com/api/announce");
    }

    public int getBossBarDuration() {
        return Math.max(5, config.getInt("bossbar.duration", 10)); // Minimum 5 seconds
    }

    public String getBossBarColor() {
        return config.getString("bossbar.color", "BLUE");
    }

    public String getBossBarStyle() {
        return config.getString("bossbar.style", "SOLID");
    }

    public boolean isDebugEnabled() {
        return config.getBoolean("debug", false);
    }

    public void setEnabled(boolean enabled) {
        config.set("enabled", enabled);
        plugin.saveConfig();
    }

    public void setUserId(String userId) {
        config.set("user-id", userId);
        plugin.saveConfig();
    }

    public void setCheckInterval(int interval) {
        config.set("check-interval", Math.max(30, interval));
        plugin.saveConfig();
    }

    public void setApiUrl(String apiUrl) {
        config.set("api-url", apiUrl);
        plugin.saveConfig();
    }

    public void setBossBarDuration(int duration) {
        config.set("bossbar.duration", Math.max(5, duration));
        plugin.saveConfig();
    }

    public void setBossBarColor(String color) {
        config.set("bossbar.color", color);
        plugin.saveConfig();
    }

    public void setBossBarStyle(String style) {
        config.set("bossbar.style", style);
        plugin.saveConfig();
    }

    public void setDebugEnabled(boolean debug) {
        config.set("debug", debug);
        plugin.saveConfig();
    }
}