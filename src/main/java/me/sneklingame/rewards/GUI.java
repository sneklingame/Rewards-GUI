package me.sneklingame.rewards;


import me.clip.placeholderapi.PlaceholderAPI;
import me.sneklingame.rewards.files.Config;
import me.sneklingame.rewards.files.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;

public class GUI {

    public static Rewards plugin;

    //get instance of main class
    public GUI(Rewards pl) {
        plugin = pl;
    }


    public static void openGUI(Player player) {

        if (!isConfigComplete(player)) {
            return;
        }

        Set<String> items_set = Config.get().getConfigurationSection("Items").getKeys(false);
        String[] items = items_set.toArray(new String[items_set.size()]);
        String title = ChatColor.translateAlternateColorCodes('&', Config.get().getString("title"));
        int rows = Config.get().getInt("rows");
        ArrayList<String> raw_lore;
        ArrayList<String> item_lore;
        ItemStack item;
        long time_left, hours, minutes, seconds;
        int days, j;
        int i = 0;
        int c = 0;


        //create new inventory
        Inventory gui = Bukkit.createInventory(player, rows * 9, title);

        //this happens for every item in config.yml
        while (i < items.length) {

            if (Config.get().getBoolean("fill-blank-space.enabled")) {

                while (c < Config.get().getInt("rows") * 9) {

                    ItemStack fill = new ItemStack(Material.matchMaterial(Config.get().getString("fill-blank-space.item")), 1);
                    ItemMeta fillMeta = fill.getItemMeta();
                    fill.setDurability((short) Config.get().getInt("fill-blank-space.data-value"));
                    fillMeta.setDisplayName(" ");
                    fill.setItemMeta(fillMeta);
                    gui.setItem(c, fill);
                    c++;

                }
            }

            long cooldown = Config.get().getLong("Items." + items[i] + ".cooldown");
            long time = System.currentTimeMillis() / 1000;
            String material = Config.get().getString("Items." + items[i] + ".active-cooldown-type");

            //set the Material according to if the cooldown is active or not
            if (Config.useMySQL()) {
                if (time >= MySQL.getTime(player, items[i]) + cooldown) {
                    material = Config.get().getString("Items." + items[i] + ".type");
                }
            } else {
                if (time >= Data.getTime(player, items[i]) + cooldown) {
                    material = Config.get().getString("Items." + items[i] + ".type");
                }
            }
            j = 0;
            //create the item
            item = new ItemStack(Material.matchMaterial(material), 1);

            //create the meta
            ItemMeta item_meta = item.getItemMeta();
            if (!Config.get().getString("Items." + items[i] + ".name").equals("")) {
                item_meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, Config.get().getString("Items." + items[i] + ".name"))));
            } else {
                item_meta.setDisplayName(" ");
            }

            raw_lore = (ArrayList<String>) Config.get().getStringList("Items." + items[i] + ".lore");
            item_lore = new ArrayList<>();

            //get the lore
            while (j < raw_lore.size()) {
                String lore_line = ChatColor.translateAlternateColorCodes('&', raw_lore.get(j));
                lore_line = Config.replacePlaceholders(lore_line, player);
                lore_line = PlaceholderAPI.setPlaceholders(player, lore_line);
                item_lore.add(lore_line);
                j++;
            }
            //set the lore and meta
            item_meta.setLore(item_lore);

            item_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item_meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            item_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setDurability((short) Config.get().getInt("Items." + items[i] + ".data-value"));

            if (Config.get().getBoolean("Items." + items[i] + ".enchanted")) {
                item_meta.addEnchant(Enchantment.DURABILITY, 1, true);
            }

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

    private static boolean isConfigComplete(Player player) {

        boolean complete = true;

        if (Config.get().getString("title") == null) {
            plugin.getLogger().log(Level.SEVERE, "Missing parameter 'title' in config.yml!");
            player.sendMessage(ChatColor.GOLD + Rewards.getPrefix() + ChatColor.RED + "Missing parameter 'title' in config.yml!");
            complete = false;
        }

        if (Config.get().getInt("rows") == 0) {
            plugin.getLogger().log(Level.SEVERE, "Missing parameter 'rows' in config.yml!");
            player.sendMessage(ChatColor.GOLD + Rewards.getPrefix() + ChatColor.RED + "Missing parameter 'rows' in config.yml!");
            complete = false;
        }

        return complete;
    }

}

