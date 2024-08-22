package me.sirimperivm.fusiondiscordlogs;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.bukkit.Bukkit.getPluginManager;

@SuppressWarnings("all")
public final class FusionDiscordLogs extends JavaPlugin{

    private FusionDiscordLogs plugin;

    private String webhookUrl;
    private boolean canContinue = true;

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();
        webhookUrl = getConfig().getString("webhook-url", null);
        if (webhookUrl == null) {
            canContinue = false;
            getPluginManager().disablePlugin(plugin);
            return;
        }

        getServer().getPluginManager().registerEvents(new Events(plugin), this);

        sendToDiscord(getDiscordMessage("server-start"));
    }

    @Override
    public void onDisable() {
        if (canContinue) {
            sendToDiscord(getDiscordMessage("server-stop"));
        }
    }

    private String getDate() {
        String defaulTimeFormat = getConfig().getString("date-format", "[dd/MM/yyyy HH:mm:ss]");
        String defaulTimeZone = getConfig().getString("date-timezone", "Europe/Rome");
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(defaulTimeFormat);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(defaulTimeZone));
        return simpleDateFormat.format(now);
    }

    public String getDiscordMessage(String section) {
        return plugin.getConfig().getString("discord-messages." + section);
    }

    public void sendToDiscord(String message) {
        message = getDate() + message;
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String jsonPayload = "{\"content\": \"" + message + "\"}";

            try (OutputStream os = connection.getOutputStream()) {
                OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
                osw.write(jsonPayload);
                osw.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                getLogger().warning("Errore durante l'invio al webhook: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
