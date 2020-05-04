package me.sneklingame.rewards.mysql;

import me.sneklingame.rewards.files.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.sql.*;


public class MySQL {

    public static Connection con;
    static ConsoleCommandSender console = Bukkit.getConsoleSender();

    private static final String host = Config.get().getString("host");
    private static final int port = Config.get().getInt("port");
    private static final String database = Config.get().getString("database");
    private static final String username = Config.get().getString("username");
    private static final String password = Config.get().getString("password");
    private static final String table = Config.get().getString("table");

    public static void connect() {

        if (!isConnected()) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Rewards] Connected to MySQL!");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

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

        String playername = player.getName();
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + table + " WHERE player=?");
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

        String playername = player.getName();

        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + table + " WHERE player=?");
            statement.setString(1, playername);
            ResultSet results = statement.executeQuery();
            results.next();
            if (!playerExists(player)) {
                PreparedStatement insert = getConnection().prepareStatement("INSERT INTO " + table + " (player, " + column + ") VALUES (?, ?)");
                insert.setString(1, playername);
                insert.setLong(2, time);
                insert.executeUpdate();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static long getTime(Player player, String column) {

        String playername = player.getName();

        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + table + " WHERE player=?");
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

        String playername = player.getName();

        try {
            PreparedStatement statement = getConnection().prepareStatement("UPDATE " + table + " SET " + column + "=? WHERE player=?");
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
