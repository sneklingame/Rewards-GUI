package me.sneklingame.rewards;


import me.sneklingame.rewards.files.Config;
import me.sneklingame.rewards.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Set;

public class GUI {

    public static Rewards plugin;

    //get instance of main class
    public GUI(Rewards pl) {
        plugin = pl;
    }


    public static void openGUI(Player player) {

        //create new inventory
        Inventory gui = Bukkit.createInventory(player, Config.get().getInt("rows") * 9, ChatColor.translateAlternateColorCodes('&', Config.get().getString("title")));

        Set<String> items_set = Config.get().getConfigurationSection("Items").getKeys(false);
        String[] items = items_set.toArray(new String[items_set.size()]);
        ArrayList<String> raw_lore;
        ArrayList<String> item_lore;
        ItemStack item;
        int i = 0;
        int j = 0;

        //this happens for every item in config.yml
        while (i < items.length) {

            long cooldown = Config.get().getLong("Items." + items[i] + ".cooldown");
            long time = System.currentTimeMillis() / 1000;
            String material = "MINECART";

            //set the Material according to if the cooldown is active or not
            if (time >= MySQL.getTime(player, items[i]) + cooldown) {
                material = Config.get().getString("Items." + items[i] + ".type");
            }

            j = 0;
            //create the item
            item = new ItemStack(Material.matchMaterial(material), 1);

            //create the meta
            ItemMeta item_meta = item.getItemMeta();
            item_meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Config.get().getString("Items." + items[i] + ".name")));
            raw_lore = (ArrayList<String>) Config.get().getStringList("Items." + items[i] + ".lore");
            item_lore = new ArrayList<>();

            //get the lore
            while (j < raw_lore.size()) {
                item_lore.add(ChatColor.translateAlternateColorCodes('&', raw_lore.get(j)));
                j++;
            }
            //set the lore and meta
            item_meta.setLore(item_lore);
            item.setItemMeta(item_meta);

            //place the item in the inventory
            gui.setItem(Config.get().getInt("Items." + items[i] + ".slot"), item);

            //dump some variables just to be safe
            item_lore.clear();
            raw_lore.clear();
            i++;

        }
        //open the inventory
        player.openInventory(gui);

    }
}

