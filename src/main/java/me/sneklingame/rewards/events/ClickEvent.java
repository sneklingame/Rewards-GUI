package me.sneklingame.rewards.events;

import me.sneklingame.rewards.GUI;
import me.sneklingame.rewards.MySQL;
import me.sneklingame.rewards.Rewards;
import me.sneklingame.rewards.files.Config;
import me.sneklingame.rewards.files.Data;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ClickEvent implements Listener {

    //get instance of main class and register the event
    public Plugin plugin;

    public ClickEvent(Rewards pl) {
        pl.getServer().getPluginManager().registerEvents(this, pl);
        plugin = pl;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        Economy economy = Rewards.getEconomy();

        //is the player clicking a valid item?
        if (event.getCurrentItem() != null) {
            //checking the inventory title, if it's a player and if the item count != 0
            if ((event.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', Config.get().getString("title")))) && (event.getWhoClicked() instanceof Player) && (event.getCurrentItem().getAmount() != 0)) {

                int i = 0;
                long money = 0;
                String message;
                String cooldown_message;
                //get all Items in in config.yml
                Set<String> items_set = Config.get().getConfigurationSection("Items").getKeys(false);
                String[] items = items_set.toArray(new String[items_set.size()]);

                event.setCancelled(true);


                //this happens for each item
                while (i < items.length) {
                    if (ChatColor.translateAlternateColorCodes('&', Config.get().getString("Items." + items[i] + ".name")).equals(ChatColor.translateAlternateColorCodes('&', event.getCurrentItem().getItemMeta().getDisplayName()))) {

                        if (Config.get().getLong("Items." + items[i] + ".cooldown") != 0) {
                            if (Config.get().getString("Items." + items[i] + ".permission") != null) {
                                if (player.hasPermission(Config.get().getString("Items." + items[i] + ".permission"))) {

                                    money = Config.get().getLong("Items." + items[i] + ".give-money");
                                    message = ChatColor.translateAlternateColorCodes('&', Config.get().getString("click-message"));
                                    message = Config.replacePlaceholders(message, "%money%", money);
                                    message = Config.replacePlaceholders(message, player);
                                    long time = System.currentTimeMillis() / 1000;
                                    long time_left;
                                    long cooldown = Config.get().getLong("Items." + items[i] + ".cooldown");


                                    if (Config.useMySQL()) {
                                        if (!MySQL.playerExists(player)) {

                                            //MySQL; Player doesn't exist
                                            MySQL.createPlayer(player, time, items[i]);
                                            executeItemActions(player, money, message, economy, items[i]);

                                        } else if (time < MySQL.getTime(player, items[i]) + cooldown) {

                                            //MySQL; Player exists and the cooldown is active
                                            time_left = (MySQL.getTime(player, items[i]) + cooldown) - time;
                                            int days = (int) TimeUnit.SECONDS.toDays(time_left);
                                            long hours = TimeUnit.SECONDS.toHours(time_left) - (days * 24);
                                            long minutes = TimeUnit.SECONDS.toMinutes(time_left) - (TimeUnit.SECONDS.toHours(time_left) * 60);
                                            long seconds = TimeUnit.SECONDS.toSeconds(time_left) - (TimeUnit.SECONDS.toMinutes(time_left) * 60);

                                            //replace placeholders with actual values
                                            cooldown_message = ChatColor.translateAlternateColorCodes('&', Config.get().getString("cooldown-message"));
                                            cooldown_message = Config.replacePlaceholders(cooldown_message, days, hours, minutes, seconds);
                                            cooldown_message = Config.replacePlaceholders(cooldown_message, player);

                                            player.sendMessage(cooldown_message);
                                            playUnavailableSound(player, items[i]);

                                        } else {
                                            //MySQL; Player exists but the cooldown is not active
                                            executeItemActions(player, money, message, economy, items[i]);
                                            MySQL.setTime(player, time, items[i]);

                                        }
                                    } else {
                                        if (!Data.playerExists(player)) {

                                            //YAML; Player doesn't exist
                                            Data.setTime(player, time, items[i]);
                                            executeItemActions(player, money, message, economy, items[i]);

                                        } else if (time < Data.getTime(player, items[i]) + cooldown) {

                                            //YAML; Player exists and the cooldown is active
                                            time_left = (Data.getTime(player, items[i]) + cooldown) - time;
                                            int days = (int) TimeUnit.SECONDS.toDays(time_left);
                                            long hours = TimeUnit.SECONDS.toHours(time_left) - (days * 24);
                                            long minutes = TimeUnit.SECONDS.toMinutes(time_left) - (TimeUnit.SECONDS.toHours(time_left) * 60);
                                            long seconds = TimeUnit.SECONDS.toSeconds(time_left) - (TimeUnit.SECONDS.toMinutes(time_left) * 60);

                                            //replace placeholders with actual values
                                            cooldown_message = ChatColor.translateAlternateColorCodes('&', Config.get().getString("cooldown-message"));
                                            cooldown_message = Config.replacePlaceholders(cooldown_message, days, hours, minutes, seconds);
                                            cooldown_message = Config.replacePlaceholders(cooldown_message, player);

                                            player.sendMessage(cooldown_message);
                                            playUnavailableSound(player, items[i]);

                                        } else {

                                            //YAML; Player exists but the cooldown is not active
                                            executeItemActions(player, money, message, economy, items[i]);
                                            Data.setTime(player, time, items[i]);
                                        }
                                    }


                                } else {
                                    String no_permission = ChatColor.translateAlternateColorCodes('&', Config.get().getString("Items." + items[i] + ".no-permission-message"));
                                    no_permission = Config.replacePlaceholders(no_permission, player);
                                    no_permission = Config.replacePlaceholders(no_permission, "%reward%", ChatColor.translateAlternateColorCodes('&',
                                            Config.get().getString("Items." + items[i] + ".name")));
                                    player.sendMessage(no_permission);
                                }
                            }
                        }

                    }

                    i++;
                }

                if (!Config.get().getBoolean("keep-open")) {
                    player.closeInventory();
                } else {
                    player.closeInventory();
                    GUI.openGUI(player);
                }
            }
        }
    }


    private void executeItemActions(Player player, long money, String message, Economy economy, String item) {


        if (Rewards.getEconomy() != null) {
            if (money != 0) {
                economy.depositPlayer(player, money);
                player.sendMessage(message);
            }
        }

        if (Config.get().getString("Items." + item + ".sound") != null) {
            try {
                Sound sound = Sound.valueOf(Config.get().getString("Items." + item + ".sound"));
                player.playSound(player.getLocation(), sound, 10, 1);
            } catch (IllegalArgumentException e) {
                player.sendMessage("Invalid Sound in config.yml! Check the Wiki for more info:");
                player.sendMessage("https://github.com/sneklingame/Rewards-GUI/wiki");
                plugin.getServer().getLogger().log(Level.WARNING, Rewards.getPrefix() + "Invalid Sound in config.yml!");
            }
        }

        ArrayList<String> commands = (ArrayList<String>) Config.get().getStringList("Items." + item + ".commands");
        int i = 0;

        while (i < commands.size()) {

            String command = commands.get(i);

            if (command.contains("console: ")) {
                command = command.replace("console: ", "");
                command = command.replace("%player%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);

            } else {
                player.performCommand(commands.get(i));
            }
            i++;
        }

    }

    private void playUnavailableSound(Player player, String item) {

        if (Config.get().getString("Items." + item + ".unavailable-sound") != null) {
            try {
                Sound sound = Sound.valueOf(Config.get().getString("Items." + item + ".unavailable-sound"));
                player.playSound(player.getLocation(), sound, 10, 1);
            } catch (IllegalArgumentException e) {
                player.sendMessage("Invalid Sound in config.yml! Check the Wiki for more info:");
                player.sendMessage("https://github.com/sneklingame/Rewards-GUI/wiki");
                plugin.getServer().getLogger().log(Level.WARNING, Rewards.getPrefix() + "Invalid Sound in config.yml!");
            }
        }
    }

}
