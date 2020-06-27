package me.sneklingame.rewards.util;

import me.sneklingame.rewards.Rewards;
import me.sneklingame.rewards.files.Config;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker implements Listener {

    private Plugin plugin;

    public UpdateChecker(Rewards plugin) {
        this.plugin = plugin;
    }

    private final String url = "https://api.spigotmc.org/legacy/update.php?resource=";
    private final String id = "78262";
    private String remoteVersion;

    private boolean isAvailable;

    public UpdateChecker() {

    }

    public boolean isAvailable() {
        return isAvailable;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        if (player.hasPermission("rw.update") && Config.get().getBoolean("check-for-updates")) {
            if (isAvailable) {
                TextComponent message = new TextComponent("§aYou can download it here.");
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Download").create()));
                message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/rewards-gui-mysql-vault.78262/"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lNew version of Rewards GUI available!"));
                player.spigot().sendMessage(message);
            }
        }
    }

    public void check() {
        isAvailable = checkUpdate();
    }

    private boolean checkUpdate() {
        System.out.println(Rewards.getPrefix() + "Checking for updates...");
        try {
            String localVersion = plugin.getDescription().getVersion();
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url + id).openConnection();
            connection.setRequestMethod("GET");
            String raw = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();

            if (raw.contains("-")) {
                remoteVersion = raw.split("-")[0].trim();
            } else {
                remoteVersion = raw;
            }

            if (!localVersion.equalsIgnoreCase(remoteVersion)) {
                System.out.println(Rewards.getPrefix() + "New version available (" + remoteVersion + ")");
                return true;
            } else {
                System.out.println(Rewards.getPrefix() + "You are using the latest version");
                return false;
            }

        } catch (IOException e) {
            System.out.println(Rewards.getPrefix() + "Error fetching version information");
            return false;
        }
    }

    public String getRemoteVersion() {
        return remoteVersion;
    }

}
