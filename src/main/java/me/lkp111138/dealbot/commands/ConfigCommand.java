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
                int uid = msg.from().id();
                try (PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT fry, collect_place FROM `groups` where gid=?")) {
                    stmt.setLong(1, gid);
                    ResultSet rs = stmt.executeQuery();
                    int fry = 0;
                    int collect_place = 0;
                    if (rs.next()) {
                        fry = rs.getInt(1);
                        collect_place = rs.getInt(2);
                    }
                    int finalCollect_place = collect_place;
                    int finalFry = fry;
                    bot.execute(new GetChatMember(gid, uid), new Callback<GetChatMember, GetChatMemberResponse>() {
                        @Override
                        public void onResponse(GetChatMember getChatMember, GetChatMemberResponse getChatMemberResponse) {
                            SendMessage send = new SendMessage(uid, "群組遊戲設定：<b>" + msg.chat().title() + "</b>");
                            send.parseMode(ParseMode.HTML);
                            send.replyMarkup(markupFromFlags(finalFry, finalCollect_place, msg, msg.chat().id()));
                            bot.execute(send);
                        }

                        @Override
                        public void onFailure(GetChatMember getChatMember, IOException e) {
                            e.printStackTrace();
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
                if (args.length > 2) {
                    Message msg = query.message();
                    long gid = Long.parseLong(args[1]);
                    int flag = Integer.parseInt(args[2]);
                    InlineKeyboardMarkup markup = markupFromFlags(flag >> 1, flag & 1, query.message(), gid);
                    EditMessageReplyMarkup edit = new EditMessageReplyMarkup(msg.chat().id(), msg.messageId());
                    edit.replyMarkup(markup);
                    bot.execute(edit);
                    return true;
                }
                processed = true;
                break;
            case "config":
                if (args.length > 2) {
                    long gid = Long.parseLong(args[1]);
                    int flag = Integer.parseInt(args[2]);
                    Message msg = query.message();
                    System.out.println(flag);
                    try (PreparedStatement stmt = Main.getConnection().prepareStatement("UPDATE `groups` SET fry=?, collect_place=? where gid=?")) {
                        stmt.setInt(1, flag >> 1);
                        stmt.setInt(2, flag & 1);
                        stmt.setLong(3, gid);
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

    private static InlineKeyboardMarkup markupFromFlags(int finalFry, int finalCollect_place, Message msg, long gid) {
        return new InlineKeyboardMarkup(
            new InlineKeyboardButton[]{new InlineKeyboardButton("炒：" + (finalFry > 0 ? "開啟" : "關閉")).callbackData("flags:" + gid + ":" + ((1 - finalFry) * 2 + finalCollect_place))},
            new InlineKeyboardButton[]{new InlineKeyboardButton("收 place：" + (finalCollect_place > 0 ? "開啟" : "關閉")).callbackData("flags:" + gid + ":" + ((1 - finalCollect_place) + finalFry * 2))},
            new InlineKeyboardButton[]{new InlineKeyboardButton("確認").callbackData("config:" + gid + ":" + (finalFry * 2 + finalCollect_place))}
        );
    }
}
