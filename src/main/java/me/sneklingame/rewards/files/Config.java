package me.sneklingame.rewards.files;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class Config {


    public static File file;
    private static FileConfiguration datafile;

    FileConfiguration config;
    File cfile;


    public static void setup() {
        //initialize file
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("Rewards").getDataFolder(), "config.yml");

        //create new file if it doesn't exist
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //load configuration
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

    public static boolean useMySQL() {
        return get().getString("storage-method").equalsIgnoreCase("mysql");
    }

    public static String replacePlaceholders(String string, Player player) {

        string = string.replace("%player%", player.getName());
        return string;
    }

    public static String replacePlaceholders(String string, String placeholder, String replace) {

        string = string.replace(placeholder, replace);
        return string;
    }

    public static String replacePlaceholders(String string, String placeholder, long replace) {

        string = string.replace(placeholder, String.valueOf(replace));
        return string;
    }

    public static String replacePlaceholders(String string, int days, long hours, long minutes, long seconds) {

        string = string.replace("%days%", String.valueOf(days));
        string = string.replace("%hours%", String.valueOf(hours));
        string = string.replace("%minutes%", String.valueOf(minutes));
        string = string.replace("%seconds%", String.valueOf(seconds));

        return string;
    }

}
