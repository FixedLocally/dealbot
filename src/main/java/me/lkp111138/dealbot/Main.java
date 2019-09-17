package me.lkp111138.dealbot;

import com.pengrad.telegrambot.TelegramBot;
import me.lkp111138.dealbot.game.Game;
import okhttp3.OkHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Main {
    public static final int BOT_OWNER = 389061708;

    private static Connection conn;
    private static Properties config = new Properties();

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        // read config
        try {
            File file = new File("dealbot.cfg");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            config.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
            // abort
            System.exit(1);
            return;
        }
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s&useSSL=false&allowPublicKeyRetrieval=true", config.get("db.host"), config.get("db.name"), config.get("db.user"), config.get("db.pwd")));
        OkHttpClient retrying_client = new OkHttpClient.Builder().retryOnConnectionFailure(true).build();
        TelegramBot bot = new TelegramBot.Builder(config.getProperty("bot.token")).okHttpClient(retrying_client).build();
        new DealBot(bot);
        Game.init(bot);
        System.out.println("Initialization complete\n");
    }

    public static Connection getConnection() throws SQLException {
        try {
            PreparedStatement stmt = conn.prepareStatement("select ? as num");
            stmt.setInt(1, 42);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt("num") == 42) {
                return conn;
            }
            return getConnection();
        } catch (SQLException e) {
            try {
                conn = DriverManager.getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s&useSSL=false&autoReconnect=true", config.get("db.host"), config.get("db.name"), config.get("db.user"), config.get("db.pwd")));
                return conn;
            } catch (SQLException e1) {
                System.err.println("Couldn't reconnect!");
                e1.printStackTrace();
                throw e;
            }
        }
    }

    public static String getConfig(String key) {
        return config.getProperty(key);
    }
}
