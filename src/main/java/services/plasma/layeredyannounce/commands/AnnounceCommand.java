package services.plasma.layeredyannounce.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import services.plasma.layeredyannounce.LayeredyAnnouncePlugin;
import services.plasma.layeredyannounce.models.Announcement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnnounceCommand implements CommandExecutor, TabCompleter {

    private final LayeredyAnnouncePlugin plugin;

    public AnnounceCommand(LayeredyAnnouncePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("layeredyannounce.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload":
                handleReload(sender);
                break;

            case "status":
                handleStatus(sender);
                break;

            case "test":
                handleTest(sender);
                break;

            case "check":
                handleCheck(sender);
                break;

            case "clear":
                handleClear(sender);
                break;

            case "debug":
                handleDebug(sender, args);
                break;

            case "help":
            default:
                sendHelpMessage(sender);
                break;
        }

        return true;
    }

    private void handleReload(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Reloading LayeredyAnnounce configuration...");
        plugin.reload();
        sender.sendMessage(ChatColor.GREEN + "Configuration reloaded successfully!");
    }

    private void handleStatus(CommandSender sender) {
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

    private void handleTest(CommandSender sender) {
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

    private void handleCheck(CommandSender sender) {
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

    private void handleClear(CommandSender sender) {
        plugin.getBossBarManager().clearAllBossBars();
        plugin.getAnnouncementManager().clearShownAnnouncements();
        sender.sendMessage(ChatColor.GREEN + "Cleared all boss bars and announcement cache!");
    }

    private void handleDebug(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /layeredyannounce debug <on|off>");
            return;
        }

        String mode = args[1].toLowerCase();
        if (mode.equals("on") || mode.equals("true")) {
            plugin.getConfigManager().setDebugEnabled(true);
            sender.sendMessage(ChatColor.GREEN + "Debug mode enabled!");
        } else if (mode.equals("off") || mode.equals("false")) {
            plugin.getConfigManager().setDebugEnabled(false);
            sender.sendMessage(ChatColor.GREEN + "Debug mode disabled!");
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /layeredyannounce debug <on|off>");
        }
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== LayeredyAnnounce Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/layeredyannounce reload" + ChatColor.GRAY + " - Reload configuration");
        sender.sendMessage(ChatColor.YELLOW + "/layeredyannounce status" + ChatColor.GRAY + " - Show plugin status");
        sender.sendMessage(ChatColor.YELLOW + "/layeredyannounce test" + ChatColor.GRAY + " - Test API connection and boss bar");
        sender.sendMessage(ChatColor.YELLOW + "/layeredyannounce check" + ChatColor.GRAY + " - Manually check for announcements");
        sender.sendMessage(ChatColor.YELLOW + "/layeredyannounce clear" + ChatColor.GRAY + " - Clear all boss bars and cache");
        sender.sendMessage(ChatColor.YELLOW + "/layeredyannounce debug <on|off>" + ChatColor.GRAY + " - Toggle debug mode");
        sender.sendMessage(ChatColor.YELLOW + "/layeredyannounce help" + ChatColor.GRAY + " - Show this help message");
        sender.sendMessage(ChatColor.GRAY + "Plugin by " + ChatColor.WHITE + "plasma.services");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("layeredyannounce.admin")) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("reload", "status", "test", "check", "clear", "debug", "help");
            List<String> completions = new ArrayList<>();

            for (String subCommand : subCommands) {
                if (subCommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }

            return completions;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            return Arrays.asList("on", "off");
        }

        return new ArrayList<>();
    }
}