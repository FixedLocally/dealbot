package me.lkp111138.dealbot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.Main;
import me.lkp111138.dealbot.game.Game;
import me.lkp111138.dealbot.translation.Translation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JoinCommand implements Command {
    @Override
    public void respond(TelegramBot bot, Message msg, String[] args) {
        // joins a game
        long gid = msg.chat().id();
        Game g = Game.byGroup(gid);
        try (Connection conn = Main.getConnection()) {
            // create user profile if there wasn't one
            PreparedStatement stmt = conn.prepareStatement("INSERT IGNORE INTO tg_users (tgid, username) VALUES (?, ?)");
            stmt.setLong(1, msg.from().id());
            stmt.setString(2, msg.from().username());
            stmt.execute();
            stmt.close();
            stmt = conn.prepareStatement("DELETE FROM next_game WHERE tgid=? AND gid=?");
            stmt.setLong(1, msg.from().id());
            stmt.setLong(2, msg.chat().id());
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            bot.execute(new SendMessage(msg.chat().id(), "An error occurred: " + e.getMessage()).replyToMessageId(msg.messageId()));
        }
        if (g == null) {
            bot.execute(new SendMessage(msg.chat().id(), Translation.get(DealBot.lang(gid)).NO_GAME_TO_JOIN()).replyToMessageId(msg.messageId()));
        } else {
            g.addPlayer(msg);
        }
    }
}
