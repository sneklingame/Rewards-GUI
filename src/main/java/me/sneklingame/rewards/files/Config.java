package me.sneklingame.rewards.files;

import me.sneklingame.rewards.Rewards;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class Config {

    private static Plugin plugin;

    public Config(Rewards plugin) {
        Config.plugin = plugin;
    }

    public static File file;
    private static FileConfiguration datafile;

    public static String host;
    public static int port;
    public static String database;
    public static String table;
    public static String username;
    public static String password;

    FileConfiguration config;
    File cfile;



    public static void loadConfig() {

        host = get().getString("host");
        port = get().getInt("port");
        database = get().getString("database");
        table = get().getString("table");
        username = get().getString("username");
        password = get().getString("password");


    }

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
        //load MySQL data from config
        loadConfig();
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
