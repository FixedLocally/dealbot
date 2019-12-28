package me.lkp111138.dealbot.game;

import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.Main;
import me.lkp111138.dealbot.game.exception.ConcurrentGameException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Game {
    /**
     * Constructs the game in preparation to start it
     * @param bot The DealBot instance
     * @param gid The group ID that attempted to start the game
     * @throws ConcurrentGameException when a game for the group already exists
     * @throws SQLException when the database connection returned an error
     */
    public Game(DealBot bot, long gid) throws ConcurrentGameException, SQLException {
        // Check for duplicated games
        Connection conn = Main.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT gid FROM games WHERE gid=?")) {

        }
    }
}
