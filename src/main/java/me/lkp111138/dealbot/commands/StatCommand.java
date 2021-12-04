package me.lkp111138.dealbot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.Main;
import me.lkp111138.dealbot.translation.Translation;

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
        Translation translation;
        if (msg.chat().type() == Chat.Type.Private) {
            translation = Translation.get(DealBot.lang(msg.from().id()));
        } else {
            translation = Translation.get(DealBot.lang(msg.chat().id()));
        }
        try (Connection conn = Main.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT won_count, game_count, game_minutes, cards_played, currency_collected, properties_collected, rent_collected FROM tg_users WHERE tgid=?");
            stmt.setLong(1, target.id());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if (rs.getInt(4) > 0) {
                    String sb;
                    sb = translation.STATS(target.id(), target.firstName(), rs.getInt(1), rs.getInt(2), rs.getFloat(3), rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getInt(7));
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
