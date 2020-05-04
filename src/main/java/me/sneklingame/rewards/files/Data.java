package me.sneklingame.rewards.files;

import me.sneklingame.rewards.Rewards;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class Data {

    private static Plugin plugin;

    private Data(Rewards plugin) {
        Data.plugin = plugin;
    }

    private static File file;
    private static FileConfiguration datafile;

    public static void setup() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("Rewards").getDataFolder(), "data.yml");

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

    public static long getTime(Player player, String item) {

        String playername = player.getName();
        return get().getLong(playername + "." + item);
    }

    public static void setTime(Player player, long time, String item) {

        String playername = player.getName();
        get().set(playername + "." + item, time);
        save();
    }

    public static boolean playerExists(Player player) {
        return get().contains(player.getName());
    }

    public static void delete() {
        file.delete();
    }

    public static void empty() {
        delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
