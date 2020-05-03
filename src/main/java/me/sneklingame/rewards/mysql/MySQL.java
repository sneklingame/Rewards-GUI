package me.sneklingame.rewards.mysql;

import me.sneklingame.rewards.Rewards;
import me.sneklingame.rewards.files.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.*;


public class MySQL {

    public static Plugin plugin;

    public MySQL(Rewards pl) {
        plugin = pl;
    }

    public static Connection con;
    static ConsoleCommandSender console = Bukkit.getConsoleSender();

    // connect to the database
    public static void connect() {
        if (!isConnected()) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + Config.host + ":" + Config.port + "/" + Config.database, Config.username, Config.password);
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Rewards] Connected to MySQL!");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // disconnect
    public static void disconnect() {
        if (isConnected()) {
            try {
                con.close();
                System.out.println("[Rewards] Disconnected from MySQL!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isConnected() {
        return (con != null);
    }

    public static Connection getConnection() {
        return con;
    }

    public static void createTable(String table) {

        try {
            Statement statement = getConnection().createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS " + table + " (player varchar(200))");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void createColumn(String table, String column, String type) {

        try {
            Statement statement = getConnection().createStatement();
            statement.execute("ALTER TABLE " + table + " ADD COLUMN IF NOT EXISTS " + column + " " + type);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static boolean playerExists(Player player) {

        String playername = player.getDisplayName();
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + Config.table + " WHERE player=?");
            statement.setString(1, playername);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                return true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public static void createPlayer(Player player, long time, String column) {

        String playername = player.getDisplayName();

        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + Config.table + " WHERE player=?");
            statement.setString(1, playername);
            ResultSet results = statement.executeQuery();
            results.next();
            if (!playerExists(player)) {
                PreparedStatement insert = getConnection().prepareStatement("INSERT INTO " + Config.table + " (player, " + column + ") VALUES (?, ?)");
                insert.setString(1, playername);
                insert.setLong(2, time);
                insert.executeUpdate();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static long getTime(Player player, String column) {

        String playername = player.getDisplayName();

        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + Config.table + " WHERE player=?");
            statement.setString(1, playername);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                return results.getLong(column);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    public static void setTime(Player player, long time, String column) {

        String playername = player.getDisplayName();

        try {
            PreparedStatement statement = getConnection().prepareStatement("UPDATE " + Config.table + " SET " + column + "=? WHERE player=?");
            statement.setLong(1, time);
            statement.setString(2, playername);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public static void deleteTable(String table) {

        try {
            Statement statement = getConnection().createStatement();
            statement.execute("DROP TABLE " + table);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public static void deleteRows(String table) {
        try {
            Statement statement = getConnection().createStatement();
            statement.execute("DELETE FROM " + table);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
