package me.sneklingame.rewards.commands;

import me.sneklingame.rewards.GUI;
import me.sneklingame.rewards.files.Config;
import me.sneklingame.rewards.files.Data;
import me.sneklingame.rewards.mysql.MySQL;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class RewardCommand implements CommandExecutor {

    public static Plugin plugin;

    //get instance of main class
    public RewardCommand(Plugin pl) {
        plugin = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        final String no_permission = ChatColor.RED + "You don't have permission to execute this command";
        final String only_players = "This command can only be executed by a player";

        //for players
        if (sender instanceof Player) {
            Player player = (Player) sender;

            //if there are no arguments
            if (args.length == 0) {

                if (player.hasPermission("rw.open.others")) {
                    GUI.openGUI(player);
                } else {
                    player.sendMessage(no_permission);
                }
                //if there is 1 argument
            } else if (args.length == 1) {

                //if the argument = ...
                switch (args[0].toLowerCase()) {

                    //rw help
                    case "help": {
                        if (player.hasPermission("rw.help")) {
                            player.sendMessage("");
                            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + plugin.getDescription().getName() + ChatColor.WHITE + " v " + plugin.getDescription().getVersion());
                            player.sendMessage("");
                            player.sendMessage(ChatColor.YELLOW + "Usage:");
                            player.sendMessage("");
                            player.sendMessage(ChatColor.YELLOW + "/rw" + ChatColor.WHITE + " - opens the GUI");
                            player.sendMessage(ChatColor.YELLOW + "/rw <player>" + ChatColor.WHITE + " - opens the GUI for another player");
                            player.sendMessage(ChatColor.YELLOW + "/rw help" + ChatColor.WHITE + " - shows this page");
                            player.sendMessage(ChatColor.YELLOW + "/rw reset" + ChatColor.WHITE + " - resets all cooldowns");
                            player.sendMessage(ChatColor.YELLOW + "/rw reload" + " - reloads configuration");
                        } else {
                            player.sendMessage(no_permission);
                        }
                        break;
                    }
                    //rw reset
                    case "reset": {
                        if (player.hasPermission("rw.reset")) {
                            if (Config.useMySQL()) {
                                MySQL.deleteRows(Config.get().getString("table"));
                            } else {
                                Data.empty();
                                Data.reload();
                            }
                            player.sendMessage(ChatColor.YELLOW + "All data has been reset!");
                        } else {
                            player.sendMessage(no_permission);
                        }
                        break;
                    }
                    //rw reload
                    case "reload": {
                        if (player.hasPermission("rw.reload")) {
                            Config.reload();
                            Data.reload();
                            player.sendMessage(ChatColor.GREEN + "Configuration reloaded.");
                            break;
                        }
                    }
                    //rw <player>
                    default: {
                        if (player.hasPermission("rw.open.others")) {
                            //does target player exist?
                            if (plugin.getServer().getPlayerExact(args[0]) != null) {
                                //do they have permission?

                                //is target player online?
                                if (plugin.getServer().getPlayerExact(args[0]).isOnline()) {

                                    //open GUI to target player
                                    GUI.openGUI(plugin.getServer().getPlayerExact(args[0]));

                                }

                                //target player is not online or doesn't exist
                            } else {
                                player.sendMessage(ChatColor.RED + "Player " + args[0] + " is currently not online.");
                            }
                            //doesn't have permission
                        } else {
                            player.sendMessage(no_permission);
                        }
                        break;
                    }
                }

                //multiple arguments
            } else {
                player.sendMessage(ChatColor.RED + "Usage: " + command.getUsage());
            }
            //for console
        } else {
            if (args.length == 1) {

                switch (args[0].toLowerCase()) {

                    //rw help
                    case "help": {
                        System.out.println(" ");
                        System.out.println(plugin.getDescription().getName() + " v " + plugin.getDescription().getVersion());
                        System.out.println(" ");
                        System.out.println("Usage:");
                        System.out.println(" ");
                        System.out.println("/rw" + " - opens the GUI");
                        System.out.println("/rw <player>" + " - opens the GUI for another player");
                        System.out.println("/rw help" + " - shows this page");
                        System.out.println("/rw reset" + " - resets all cooldowns");
                        System.out.println("/rw reload" + " - reloads configuration");
                        break;
                    }
                    //rw reset
                    case "reset": {
                        if (Config.useMySQL()) {
                            MySQL.deleteRows(Config.get().getString("table"));
                        } else {
                            Data.empty();
                            Data.reload();
                        }
                        System.out.println("All data has been reset!");
                        break;
                    }
                    //rw reload
                    case "reload": {
                        Config.reload();
                        Data.reload();
                        System.out.println("Configuration reloaded.");
                        break;
                    }
                    //anything else
                    default: {
                        System.out.println("Argument " + args[0] + " not recognized.");
                        break;
                    }
                }


            } else {
                System.out.println("This command was either not found or cannot be executed by the console");
            }
        }

        return true;
    }

}

