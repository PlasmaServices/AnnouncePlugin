package services.plasma.layeredyannounce.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import services.plasma.layeredyannounce.LayeredyAnnouncePlugin;
import services.plasma.layeredyannounce.models.Announcement;

@CommandAlias("layeredyannounce|announce|lannounce")
@Description("Layeredy Announce plugin commands")
@CommandPermission("layeredyannounce.admin")
public class AnnounceCommand extends BaseCommand {

    private final LayeredyAnnouncePlugin plugin;

    public AnnounceCommand(LayeredyAnnouncePlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @HelpCommand
    @Subcommand("help")
    @Description("Show help for LayeredyAnnounce commands")
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("reload")
    @Description("Reload the plugin configuration")
    public void onReload(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Reloading LayeredyAnnounce configuration...");
        plugin.reload();
        sender.sendMessage(ChatColor.GREEN + "Configuration reloaded successfully!");
    }

    @Subcommand("status")
    @Description("Show detailed plugin status")
    public void onStatus(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== LayeredyAnnounce Status ===");
        sender.sendMessage(ChatColor.GRAY + "Enabled: " + (plugin.getConfigManager().isEnabled() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
        sender.sendMessage(ChatColor.GRAY + "User ID: " + ChatColor.WHITE + plugin.getConfigManager().getUserId());
        sender.sendMessage(ChatColor.GRAY + "Check Interval: " + ChatColor.WHITE + plugin.getConfigManager().getCheckInterval() + " seconds");
        sender.sendMessage(ChatColor.GRAY + "API URL: " + ChatColor.WHITE + plugin.getConfigManager().getApiUrl());
        sender.sendMessage(ChatColor.GRAY + "Full Endpoint: " + ChatColor.WHITE + plugin.getConfigManager().getApiUrl() + "/" + plugin.getConfigManager().getUserId());
        sender.sendMessage(ChatColor.GRAY + "Boss Bar Duration: " + ChatColor.WHITE + plugin.getConfigManager().getBossBarDuration() + " seconds");
        sender.sendMessage(ChatColor.GRAY + "Boss Bar Color: " + ChatColor.WHITE + plugin.getConfigManager().getBossBarColor());
        sender.sendMessage(ChatColor.GRAY + "Boss Bar Style: " + ChatColor.WHITE + plugin.getConfigManager().getBossBarStyle());
        sender.sendMessage(ChatColor.GRAY + "Active Boss Bars: " + ChatColor.WHITE + plugin.getBossBarManager().getActiveBossBarCount());
        sender.sendMessage(ChatColor.GRAY + "Shown Announcements: " + ChatColor.WHITE + plugin.getAnnouncementManager().getShownAnnouncementCount());
        sender.sendMessage(ChatColor.GRAY + "Debug Mode: " + (plugin.getConfigManager().isDebugEnabled() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
    }

    @Subcommand("test")
    @Description("Test API connection and display a sample boss bar")
    public void onTest(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Testing connection to Layeredy Announce API...");

        plugin.getAnnouncementManager().testConnection()
                .thenAccept(success -> {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        if (success) {
                            sender.sendMessage(ChatColor.GREEN + "Connection test successful!");
                        } else {
                            sender.sendMessage(ChatColor.RED + "Connection test failed! Check console for details.");
                        }
                    });
                });

        // Also test boss bar functionality
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            Announcement testAnnouncement = new Announcement(
                    "ann_test_" + System.currentTimeMillis(),
                    "Test announcement from LayeredyAnnounce by plasma.services"
            );

            plugin.getBossBarManager().displayAnnouncement(testAnnouncement);
            sender.sendMessage(ChatColor.GREEN + "Test boss bar displayed!");
        });
    }

    @Subcommand("check")
    @Description("Manually check for new announcements")
    public void onCheck(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Manually checking for announcements...");

        plugin.getAnnouncementManager().checkForAnnouncements()
                .thenRun(() -> {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        sender.sendMessage(ChatColor.GREEN + "Manual announcement check completed!");
                    });
                })
                .exceptionally(throwable -> {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        sender.sendMessage(ChatColor.RED + "Error during manual check: " + throwable.getMessage());
                    });
                    return null;
                });
    }

    @Subcommand("clear")
    @Description("Clear all active boss bars and announcement cache")
    public void onClear(CommandSender sender) {
        plugin.getBossBarManager().clearAllBossBars();
        plugin.getAnnouncementManager().clearShownAnnouncements();
        sender.sendMessage(ChatColor.GREEN + "Cleared all boss bars and announcement cache!");
    }

    @Subcommand("debug")
    @Description("Toggle debug mode on or off")
    @CommandCompletion("on|off")
    public void onDebug(CommandSender sender, @Values("on,off") String mode) {
        if (mode.equalsIgnoreCase("on") || mode.equalsIgnoreCase("true")) {
            plugin.getConfigManager().setDebugEnabled(true);
            sender.sendMessage(ChatColor.GREEN + "Debug mode enabled!");
        } else if (mode.equalsIgnoreCase("off") || mode.equalsIgnoreCase("false")) {
            plugin.getConfigManager().setDebugEnabled(false);
            sender.sendMessage(ChatColor.GREEN + "Debug mode disabled!");
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /layeredyannounce debug <on|off>");
        }
    }
}