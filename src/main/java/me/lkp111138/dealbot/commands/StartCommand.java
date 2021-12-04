package me.lkp111138.dealbot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StartCommand implements Command {
    @Override
    public void respond(TelegramBot bot, Message msg, String[] args) {
        // init the user if needed
        try (Connection conn = Main.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT IGNORE INTO tg_users (tgid, username) VALUES (?, ?)");
            stmt.setLong(1, msg.from().id());
            stmt.setString(2, msg.from().username());
            stmt.execute();
            if (msg.chat().type() == Chat.Type.Private) {
                bot.execute(new SendMessage(msg.chat().id(), "Hello! Thank you for starting me. Add me to a group and use /play to start a game.").replyToMessageId(msg.messageId()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            bot.execute(new SendMessage(msg.chat().id(), "An error occured: " + e.getMessage()).replyToMessageId(msg.messageId()));
        }
    }
}
