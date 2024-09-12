package me.sirimperivm.fusiondiscordlogs;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.List;

@SuppressWarnings("all")
public class Events implements Listener {

    private FusionDiscordLogs plugin;

    public Events(FusionDiscordLogs plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        String command = event.getCommand();
        List<String> blacklistedCommands = plugin.getConfig().getStringList("log-settings.blacklisted-commands");
        if (blacklistedCommands.contains(command))
            return;
        plugin.sendToDiscord(plugin.getDiscordMessage("console-command")
                .replace("{command-string}", command)
        );
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        List<String> blacklistedCommands = plugin.getConfig().getStringList("log-settings.blacklisted-commands");
        if (blacklistedCommands.contains(command))
            return;
        plugin.sendToDiscord(plugin.getDiscordMessage("player-command")
                .replace("{player}", event.getPlayer().getName())
                .replace("{command-string}", command)
        );
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage();
        plugin.sendToDiscord(plugin.getDiscordMessage("chat")
                .replace("{player}", p.getName())
                .replace("{message}", message)
        );
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        plugin.sendToDiscord(plugin.getDiscordMessage("join")
                .replace("{player}", p.getName())
        );
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        plugin.sendToDiscord(plugin.getDiscordMessage("quit")
                .replace("{player}", p.getName())
        );
    }

}
