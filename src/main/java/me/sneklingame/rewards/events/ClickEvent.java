package me.sneklingame.rewards.events;

import me.sneklingame.rewards.GUI;
import me.sneklingame.rewards.Rewards;
import me.sneklingame.rewards.files.Config;
import me.sneklingame.rewards.mysql.MySQL;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ClickEvent implements Listener {

    public Plugin plugin;

    public ClickEvent(Rewards pl) {
        pl.getServer().getPluginManager().registerEvents(this, pl);
        plugin = pl;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        Economy economy = Rewards.getEconomy();

        if (event.getCurrentItem() != null) {
            if ((event.getClickedInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', Config.get().getString("title")))) && (event.getWhoClicked() instanceof Player) && (event.getCurrentItem().getAmount() != 0)) {

                int i = 0;
                long money = 0;
                String message;
                String cooldown_message;
                Set<String> items_set = Config.get().getConfigurationSection("Items").getKeys(false);
                String[] items = items_set.toArray(new String[items_set.size()]);

                event.setCancelled(true);


                while (i < items.length) {
                    if (ChatColor.translateAlternateColorCodes('&', Config.get().getString("Items." + items[i] + ".name")).equals(ChatColor.translateAlternateColorCodes('&', event.getCurrentItem().getItemMeta().getDisplayName()))) {

                        if (player.hasPermission(Config.get().getString("Items." + items[i] + ".permission"))) {

                            money = Config.get().getLong("Items." + items[i] + ".give-money");
                            message = ChatColor.translateAlternateColorCodes('&', Config.get().getString("click-message"));
                            message = message.replace("%money%", String.valueOf(money));
                            long time = System.currentTimeMillis() / 1000;
                            long time_left;
                            long cooldown = Config.get().getLong("Items." + items[i] + ".cooldown");

                            if (!MySQL.playerExists(player)) {
                                giveMoney(player, money, message, economy);
                                MySQL.createPlayer(player, time, items[i]);
                            } else {
                                if (time < MySQL.getTime(player, items[i]) + cooldown) {

                                    time_left = (MySQL.getTime(player, items[i]) + cooldown) - time;
                                    int days = (int) TimeUnit.SECONDS.toDays(time_left);
                                    long hours = TimeUnit.SECONDS.toHours(time_left) - (days * 24);
                                    long minutes = TimeUnit.SECONDS.toMinutes(time_left) - (TimeUnit.SECONDS.toHours(time_left) * 60);
                                    long seconds = TimeUnit.SECONDS.toSeconds(time_left) - (TimeUnit.SECONDS.toMinutes(time_left) * 60);

                                    cooldown_message = ChatColor.translateAlternateColorCodes('&', Config.get().getString("cooldown-message"));
                                    cooldown_message = cooldown_message.replace("%days%", String.valueOf(days));
                                    cooldown_message = cooldown_message.replace("%hours%", String.valueOf(hours));
                                    cooldown_message = cooldown_message.replace("%minutes%", String.valueOf(minutes));
                                    cooldown_message = cooldown_message.replace("%seconds%", String.valueOf(seconds));

                                    player.sendMessage(cooldown_message);

                                } else {
                                    giveMoney(player, money, message, economy);
                                    MySQL.setTime(player, time, items[i]);
                                }
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Config.get().getString("Items." + items[i] + ".no-permission-message")));
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

    public void giveMoney(Player player, long money, String message, Economy economy) {

        player.sendMessage(message);
        economy.depositPlayer(player, money);

    }

}
