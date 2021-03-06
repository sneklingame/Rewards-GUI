package me.sneklingame.rewards.files;

import me.sneklingame.rewards.Rewards;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class Messages {

    private static Plugin plugin;

    private Messages(Rewards plugin) {
        Messages.plugin = plugin;
    }

    public static File file;
    private static FileConfiguration datafile;

    public static void setup() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("Rewards").getDataFolder(), "messages.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        datafile = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() {
        return datafile;
    }

    public static void save() {
        try {
            datafile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reload() {
        datafile = YamlConfiguration.loadConfiguration(file);
    }

}
