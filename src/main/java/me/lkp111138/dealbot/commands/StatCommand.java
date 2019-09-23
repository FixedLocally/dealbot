package me.lkp111138.dealbot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatCommand implements Command {
    @Override
    public void respond(TelegramBot bot, Message msg, String[] args) {
        User target = msg.from();
        if (msg.replyToMessage() != null) {
            target = msg.replyToMessage().from();
        }
        try (Connection conn = Main.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT won_count, game_count, game_minutes, cards_played, currency_collected, properties_collected, rent_collected FROM tg_users WHERE tgid=?");
            stmt.setInt(1, target.id());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if (rs.getInt(4) > 0) {
                    String sb = String.format("Statistics for <a href=\"tg://user?id=%d\">%s</a>\n", target.id(), target.firstName()) +
                            String.format("Won/Total games: %d / %d (%.2f%%)\n", rs.getInt(1), rs.getInt(2), 100.0 * rs.getInt(1) / rs.getInt(2)) +
                            String.format("Total in game minutes: %.1f\n", rs.getFloat(3)) +
                            String.format("Number of cards played: %d\n", rs.getInt(4)) +
                            String.format("Currency collected: $ %dM\n", rs.getInt(5)) +
                            String.format("Properties collected: %d\n", rs.getInt(6)) +
                            String.format("Rent collected: $ %dM\n", rs.getInt(7));
                    bot.execute(new SendMessage(msg.chat().id(), sb).replyToMessageId(msg.messageId()).parseMode(ParseMode.HTML));
                } else {
                    bot.execute(new SendMessage(msg.chat().id(), "You haven't played a game yet!").replyToMessageId(msg.messageId()).parseMode(ParseMode.HTML));
                }
            } else {
                bot.execute(new SendMessage(msg.chat().id(), "You haven't played a game yet!").replyToMessageId(msg.messageId()).parseMode(ParseMode.HTML));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
