package me.lkp111138.dealbot.commands;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetChatMemberResponse;
import com.pengrad.telegrambot.response.SendResponse;
import me.lkp111138.dealbot.Main;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfigCommand implements Command {
    @Override
    public void respond(TelegramBot bot, Message msg, String[] args) {
        if (msg.from().id() == Main.BOT_OWNER) {
            send(bot, msg);
            return;
        }
        bot.execute(new GetChatMember(msg.chat().id(), msg.from().id()), new Callback<GetChatMember, GetChatMemberResponse>() {
            @Override
            public void onResponse(GetChatMember request, GetChatMemberResponse response) {
                ChatMember chatMember = response.chatMember();
                if (chatMember.status() == ChatMember.Status.administrator || chatMember.status() == ChatMember.Status.creator) {
                    send(bot, msg);
                }
            }

            @Override
            public void onFailure(GetChatMember request, IOException e) {

            }
        });
        System.out.println("not sent, no perms");
    }

    private static void send(TelegramBot bot, Message msg) {
        switch (msg.chat().type()) {
            case group:
            case supergroup:
                long gid = msg.chat().id();
                long uid = msg.from().id();
                try (PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT turn_wait_time, pay_time, say_no_time FROM `groups` where gid=?")) {
                    stmt.setLong(1, gid);
                    ResultSet rs = stmt.executeQuery();
                    int turnTime = 0;
                    int payTime = 0;
                    int objectionTime = 0;
                    if (rs.next()) {
                        turnTime = rs.getInt(1);
                        payTime = rs.getInt(2);
                        objectionTime = rs.getInt(3);
                    }
                    SendMessage send = new SendMessage(uid, "群組遊戲設定：<b>" + msg.chat().title() + "</b>");
                    send.parseMode(ParseMode.HTML);
                    send.replyMarkup(markupFromFlags(turnTime, payTime, objectionTime, msg, msg.chat().id()));
                    bot.execute(send, new Callback<SendMessage, SendResponse>() {
                        @Override
                        public void onResponse(SendMessage request, SendResponse response) {
                            System.out.println(response.description());
                        }

                        @Override
                        public void onFailure(SendMessage request, IOException e) {

                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }

    public static boolean callback(TelegramBot bot, CallbackQuery query) {
        boolean processed = false;
        String payload = query.data();
        String[] args = payload.split(":");
        switch (args[0]) {
            case "flags":
                if (args.length > 4) {
                    Message msg = query.message();
                    long gid = Long.parseLong(args[1]);
                    int turnTime = Integer.parseInt(args[2]);
                    int payTime = Integer.parseInt(args[3]);
                    int objectionTime = Integer.parseInt(args[4]);
                    InlineKeyboardMarkup markup = markupFromFlags(turnTime, payTime, objectionTime, query.message(), gid);
                    EditMessageReplyMarkup edit = new EditMessageReplyMarkup(msg.chat().id(), msg.messageId());
                    edit.replyMarkup(markup);
                    bot.execute(edit);
                    return true;
                }
                processed = true;
                break;
            case "config":
                if (args.length > 4) {
                    long gid = Long.parseLong(args[1]);
                    int turnTime = Integer.parseInt(args[2]);
                    int payTime = Integer.parseInt(args[3]);
                    int objectionTime = Integer.parseInt(args[4]);
                    Message msg = query.message();
                    try (PreparedStatement stmt = Main.getConnection().prepareStatement("UPDATE `groups` SET say_no_time=?, turn_wait_time=?, pay_time=? where gid=?")) {
                        stmt.setInt(1, objectionTime);
                        stmt.setInt(2, turnTime);
                        stmt.setInt(3, payTime);
                        stmt.setLong(4, gid);
                        stmt.execute();
                        EditMessageText edit = new EditMessageText(msg.chat().id(), msg.messageId(), "設定成功！");
                        bot.execute(edit);
                        return true;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                processed = true;
                break;
        }
        return processed;
    }

    private static InlineKeyboardMarkup markupFromFlags(int turnTime, int payTime, int objectionTime, Message msg, long gid) {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("出牌時限（秒）：").callbackData("nop"),
                        new InlineKeyboardButton("-15").callbackData(String.format("flags:%s:%s:%s:%s", gid, turnTime - 15, payTime, objectionTime)),
                        new InlineKeyboardButton(turnTime + " 秒").callbackData("nop"),
                        new InlineKeyboardButton("+15").callbackData(String.format("flags:%s:%s:%s:%s", gid, turnTime + 15, payTime, objectionTime))
                },
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("付款時限（秒）：").callbackData("nop"),
                        new InlineKeyboardButton("-5").callbackData(String.format("flags:%s:%s:%s:%s", gid, turnTime, payTime - 5, objectionTime)),
                        new InlineKeyboardButton(payTime + " 秒").callbackData("nop"),
                        new InlineKeyboardButton("+5").callbackData(String.format("flags:%s:%s:%s:%s", gid, turnTime, payTime + 5, objectionTime))
                },
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("作出反對時限（秒）：").callbackData("nop"),
                        new InlineKeyboardButton("-5").callbackData(String.format("flags:%s:%s:%s:%s", gid, turnTime, payTime, objectionTime - 5)),
                        new InlineKeyboardButton(objectionTime + " 秒").callbackData("nop"),
                        new InlineKeyboardButton("+5").callbackData(String.format("flags:%s:%s:%s:%s", gid, turnTime, payTime, objectionTime + 5))
                },
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("確定").callbackData(String.format("config:%s:%s:%s:%s", gid, turnTime, payTime, objectionTime)),
                }
        );
    }
}
