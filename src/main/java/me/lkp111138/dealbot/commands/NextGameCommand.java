package me.lkp111138.dealbot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.Main;
import me.lkp111138.dealbot.translation.Translation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NextGameCommand implements Command {
    @Override
    public void respond(TelegramBot bot, Message msg, String[] args) {
        try (PreparedStatement stmt = Main.getConnection().prepareStatement("replace into next_game values (?, ?)")) {
            long gid = msg.chat().id();
            long uid = msg.from().id();
            stmt.setLong(1, uid);
            stmt.setLong(2, gid);
            stmt.execute();
            SendMessage send = new SendMessage(uid, Translation.get(DealBot.lang(gid)).NEXT_GAME_QUEUED(msg.chat().title()));
            // add a cancel button
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
                    new InlineKeyboardButton[]{new InlineKeyboardButton(Translation.get(DealBot.lang(gid)).CANCEL()).callbackData("cancel:" + gid)}
            );
            send.replyMarkup(keyboard);
            bot.execute(send);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean callback(TelegramBot bot, CallbackQuery query) {
        long tgid = query.from().id();
        int mid = query.message().messageId();
        String payload = query.data();
        String[] args = payload.split(":");
        AnswerCallbackQuery answer = new AnswerCallbackQuery(query.id());
        if ("cancel".equals(args[0])) {
            long gid = Long.parseLong(args[1]);
            try (Connection conn = Main.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("delete from next_game where tgid=? and gid=?");
                stmt.setLong(1, tgid);
                stmt.setLong(2, gid);
                stmt.executeUpdate();
                bot.execute(answer);
                bot.execute(new DeleteMessage(tgid, mid));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}