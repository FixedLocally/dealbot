package me.lkp111138.dealbot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.Main;
import me.lkp111138.dealbot.translation.Translation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AchvCommand implements Command {
    @Override
    public void respond(TelegramBot bot, Message msg, String[] args) {
        User target = msg.from();
        if (msg.replyToMessage() != null) {
            target = msg.replyToMessage().from();
        }
        try (PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT achv FROM achv_log WHERE tgid=?")) {
            Translation translation = Translation.get(DealBot.lang(msg.chat().id()));
            stmt.setInt(1, target.id());
            ResultSet rs = stmt.executeQuery();
            StringBuilder sb = new StringBuilder(translation.ACHV_UNLOCKED());
            int count = 0;
            while (rs.next()) {
                ++count;
                sb.append(translation.ACHIEVEMENT_TITLE(rs.getString(1)));
            }
            sb.append(String.format(translation.A_TOTAL_OF(), count));
            bot.execute(new SendMessage(msg.chat().id(), sb.toString()).replyToMessageId(msg.messageId()).parseMode(ParseMode.Markdown));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
