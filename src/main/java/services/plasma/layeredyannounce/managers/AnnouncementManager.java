package services.plasma.layeredyannounce.managers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import services.plasma.layeredyannounce.LayeredyAnnouncePlugin;
import services.plasma.layeredyannounce.models.Announcement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class AnnouncementManager {

    private final LayeredyAnnouncePlugin plugin;
    private final ConfigManager configManager;
    private final BossBarManager bossBarManager;
    private final Gson gson;
    private final Set<String> shownAnnouncementIds;

    public AnnouncementManager(LayeredyAnnouncePlugin plugin, ConfigManager configManager, BossBarManager bossBarManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.bossBarManager = bossBarManager;
        this.gson = new Gson();
        this.shownAnnouncementIds = new HashSet<>();
    }

    public CompletableFuture<Void> checkForAnnouncements() {
        return CompletableFuture.runAsync(() -> {
            try {
                if (configManager.isDebugEnabled()) {
                    plugin.getLogger().info("Checking for announcements...");
                }

                Announcement announcement = fetchAnnouncement();

                if (announcement == null || !announcement.isValid()) {
                    if (configManager.isDebugEnabled()) {
                        plugin.getLogger().info("No valid announcement found");
                    }
                    return;
                }

                boolean shouldShow = shouldShowAnnouncement(announcement);

                if (!shouldShow) {
                    if (configManager.isDebugEnabled()) {
                        plugin.getLogger().info("Announcement already shown or marked as show-once: " + announcement.getId());
                    }
                    return;
                }

                shownAnnouncementIds.add(announcement.getId());

                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    bossBarManager.displayAnnouncement(announcement);
                });

                if (configManager.isDebugEnabled()) {
                    plugin.getLogger().info("Displaying new announcement: " + announcement.getContent());
                }

            } catch (Exception e) {
                plugin.getLogger().warning("Error checking for announcements: " + e.getMessage());
                if (configManager.isDebugEnabled()) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean shouldShowAnnouncement(Announcement announcement) {
        if (announcement.isShowOnce() && shownAnnouncementIds.contains(announcement.getId())) {
            return false;
        }

        return true;
    }

    private Announcement fetchAnnouncement() throws IOException {
        String apiUrl = configManager.getApiUrl();
        String userId = configManager.getUserId();

        String endpoint = apiUrl + "/" + userId;

        if (configManager.isDebugEnabled()) {
            plugin.getLogger().info("Fetching announcement from: " + endpoint);
        }

        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            // Set request properties
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "LayeredyAnnounce-Minecraft-Plugin/1.0.0");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return parseAnnouncementResponse(response.toString());

            } else if (responseCode == 404) {
                if (configManager.isDebugEnabled()) {
                    plugin.getLogger().info("No announcement found for user ID: " + userId);
                }
                return null;
            } else {
                throw new IOException("API returned status code: " + responseCode);
            }

        } finally {
            connection.disconnect();
        }
    }

    private Announcement parseAnnouncementResponse(String jsonResponse) {
        try {
            if (configManager.isDebugEnabled()) {
                plugin.getLogger().info("Parsing response: " + jsonResponse);
            }

            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

            if (!jsonObject.has("hasAnnouncement") || !jsonObject.get("hasAnnouncement").getAsBoolean()) {
                if (configManager.isDebugEnabled()) {
                    plugin.getLogger().info("No active announcement found (hasAnnouncement: false)");
                }
                return null;
            }

            Announcement announcement = gson.fromJson(jsonResponse, Announcement.class);

            if (configManager.isDebugEnabled()) {
                plugin.getLogger().info("Parsed announcement: " + announcement.toString());
            }

            return announcement;

        } catch (Exception e) {
            plugin.getLogger().warning("Error parsing announcement response: " + e.getMessage());
            if (configManager.isDebugEnabled()) {
                plugin.getLogger().warning("Response was: " + jsonResponse);
                e.printStackTrace();
            }
            return null;
        }
    }

    public void clearShownAnnouncements() {
        shownAnnouncementIds.clear();
        if (configManager.isDebugEnabled()) {
            plugin.getLogger().info("Cleared shown announcements cache");
        }
    }

    public int getShownAnnouncementCount() {
        return shownAnnouncementIds.size();
    }

    public CompletableFuture<Boolean> testConnection() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Announcement announcement = fetchAnnouncement();
                if (announcement != null && announcement.isValid()) {
                    plugin.getLogger().info("Connection test successful - found announcement: " + announcement.getContent());
                } else {
                    plugin.getLogger().info("Connection test successful - no active announcements");
                }
                return true;
            } catch (Exception e) {
                plugin.getLogger().warning("Connection test failed: " + e.getMessage());
                return false;
            }
        });
    }
}