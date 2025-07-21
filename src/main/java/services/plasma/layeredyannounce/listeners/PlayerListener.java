package services.plasma.layeredyannounce.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import services.plasma.layeredyannounce.LayeredyAnnouncePlugin;

public class PlayerListener implements Listener {

    private final LayeredyAnnouncePlugin plugin;

    public PlayerListener(LayeredyAnnouncePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getBossBarManager().addPlayerToBossBars(event.getPlayer());

        if (plugin.getConfigManager().isDebugEnabled()) {
            plugin.getLogger().info("Added player " + event.getPlayer().getName() + " to active boss bars");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getBossBarManager().removePlayerFromBossBars(event.getPlayer());

        if (plugin.getConfigManager().isDebugEnabled()) {
            plugin.getLogger().info("Removed player " + event.getPlayer().getName() + " from boss bars");
        }
    }
}