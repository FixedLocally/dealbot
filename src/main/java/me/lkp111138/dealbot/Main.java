package me.lkp111138.dealbot;

import org.apache.commons.dbcp2.BasicDataSource;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class Main {
    public static final int BOT_OWNER = 389061708;

    private static Connection conn;
    private static BasicDataSource ds = new BasicDataSource();
    private static Properties config = new Properties();

    public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException {
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
        ds.setUrl(String.format("jdbc:mysql://%s/%s?user=%s&password=%s&useSSL=false&allowPublicKeyRetrieval=true", config.get("db.host"), config.get("db.name"), config.get("db.user"), config.get("db.pwd")));
        ds.setUsername(config.getProperty("db.user"));
        ds.setPassword(config.getProperty("db.pass"));
        ds.setMinIdle(3);
        ds.setMaxIdle(30);
        ds.setMaxOpenPreparedStatements(3000);
        DealBot bot = new DealBot(config.getProperty("bot.token"));
        System.out.println("Initialization complete");
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static String getConfig(String key) {
        return config.getProperty(key);
    }
}
