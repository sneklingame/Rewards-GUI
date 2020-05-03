package me.sneklingame.rewards;

import me.sneklingame.rewards.commands.RewardCommand;
import me.sneklingame.rewards.events.ClickEvent;
import me.sneklingame.rewards.files.Config;
import me.sneklingame.rewards.files.DataFile;
import me.sneklingame.rewards.files.Messages;
import me.sneklingame.rewards.mysql.MySQL;
import metrics.Metrics;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Rewards extends JavaPlugin {


    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    public final String prefix = "[" + getDescription().getPrefix() + "] ";


    @Override
    public void onEnable() {

        GUI gui = new GUI(this);
        ClickEvent clickEvent = new ClickEvent(this);
        MySQL mySQL = new MySQL(this);
        Metrics metrics = new Metrics(this, 7382);
        Config config = new Config(this);

        //load config.yml
        saveResource("config.yml", false);
        Config.setup();
        Config.get().options().copyDefaults(true);

        //load messages.yml
        saveResource("messages.yml", false);
        Messages.setup();
        Messages.get().options().copyDefaults(true);

        showStartingMessage();

        //setup MySQL or YAML
        setupDataStorageMethod(getDataStorageMethod());

        //register rewards command
        getCommand("reward").setExecutor(new RewardCommand(this));

        //disable the plugin if no economy plugin is found
        if (!setupEconomy()) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

    }

    @Override
    public void onDisable() {
        //if MySQL is enabled in config, disconnect from the database
        if (getDataStorageMethod().equalsIgnoreCase("mysql")) {
            MySQL.disconnect();
        }

    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public void setupMySQL() {

        if (getDataStorageMethod().equalsIgnoreCase("mysql")) {
            MySQL.connect();
            if (!MySQL.isConnected()) {
                getServer().getLogger().log(Level.SEVERE, prefix + " FAILED TO CONNECT TO MYSQL! DISABLING PLUGIN.");
                getServer().getPluginManager().disablePlugin(this);
            } else {
                String table = getConfig().getString("table");
                MySQL.createTable(table);

                Set<String> items_set = getConfig().getConfigurationSection("Items").getKeys(false);
                String[] items = items_set.toArray(new String[items_set.size()]);
                int i = 0;

                while (i < items.length) {
                    MySQL.createColumn(table, items[i], "int");
                    i++;
                }
            }
        }

    }

    public static Economy getEconomy() {
        return econ;
    }

    public void showStartingMessage() {

        System.out.println(prefix + " -----------------------------");
        System.out.println(prefix + " " + getDescription().getName() + " v " + getDescription().getVersion());
        System.out.println(prefix + " Created by Sneklingame");
        System.out.println(prefix);
        System.out.println(prefix + " Storage method: " + getDataStorageMethod());
        System.out.println(prefix);
        System.out.println(prefix + " Plugin loaded successfully!");
        System.out.println(prefix + " -----------------------------");
    }

    public void setupDataStorageMethod(String method) {
        //MySQL setup
        if (method.equalsIgnoreCase("mysql")) {
            setupMySQL();

            //YAML setup
        } else if (method.equalsIgnoreCase("yaml")) {
            DataFile.setup();
            DataFile.get().options().copyDefaults(true);
            DataFile.save();
            //invalid data storage method in config.yml
        } else {
            getServer().getLogger().log(Level.SEVERE, prefix + "INVALID DATA STORAGE TYPE in config.yml! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public String getDataStorageMethod() {

        String type = getConfig().getString("storage-method");

        if (type.equalsIgnoreCase("mysql")) {
            return "MySQL";

        } else if (type.equalsIgnoreCase("yaml")) {
            return "YAML";

        } else {
            return "undefined";
        }
    }
}
