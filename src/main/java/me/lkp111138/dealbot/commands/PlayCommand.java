package me.lkp111138.dealbot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.Main;
import me.lkp111138.dealbot.game.ConcurrentGameException;
import me.lkp111138.dealbot.game.Game;
import me.lkp111138.dealbot.game.GroupInfo;
import me.lkp111138.dealbot.translation.Translation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayCommand implements Command {
    @Override
    public void respond(TelegramBot bot, Message msg, String[] args) {
        // ono LOGIC :EYES:
        if (msg.chat().type() == Chat.Type.Private) {
            // private chat cya
            bot.execute(new SendMessage(msg.chat().id(), "You cannot play in private! Try using /" + args[0] + " in a group."));
        } else {
            Game g = Game.byGroup(msg.chat().id());
            if (g == null) {
                try (Connection conn = Main.getConnection()) {
                    PreparedStatement stmt = conn.prepareStatement("SELECT wait_time, turn_wait_time, lang, protest_mode, pay_time, say_no_time FROM `groups` WHERE gid=?");
                    stmt.setLong(1, msg.chat().id());
                    ResultSet rs = stmt.executeQuery();
                    GroupInfo info;
                    int turnWait = 45;
                    int wait = 120;
                    int payTime = 20;
                    int sayNoTime = 10;
                    if (rs.next()) {
                        turnWait = rs.getInt(2);
                        payTime = rs.getInt(5);
                        sayNoTime = rs.getInt(6);
                        wait = rs.getInt(1);
                        if (rs.getInt(4) > 0) {
                            bot.execute(new SendMessage(msg.chat().id(), Translation.get(DealBot.lang(msg.chat().id())).JOIN_69_PROTEST()).replyToMessageId(msg.messageId()));
//                            return;
                        }
                    } else {
                        // insert
                        PreparedStatement stmt1 = conn.prepareStatement("INSERT INTO `groups` (gid) VALUES (?)");
                        stmt1.setLong(1, msg.chat().id());
                        stmt1.execute();
                        stmt1.close();
                    }
                    info = new GroupInfo(turnWait, payTime, sayNoTime);
                    try {
                        new Game(msg, wait, info);
                    } catch (ConcurrentGameException e) {
                        if (e.getGame().started()) {
                            bot.execute(new SendMessage(msg.chat().id(), Translation.get(DealBot.lang(msg.chat().id())).GAME_STARTED()).replyToMessageId(msg.messageId()));
                        } else {
                            bot.execute(new SendMessage(msg.chat().id(), Translation.get(DealBot.lang(msg.chat().id())).GAME_STARTING()).replyToMessageId(msg.messageId()));
                        }
                    }
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    bot.execute(new SendMessage(msg.chat().id(), Translation.get(DealBot.lang(msg.chat().id())).ERROR() + e.getMessage()).replyToMessageId(msg.messageId()));
                }
            } else {
                if (g.started()) {
                    bot.execute(new SendMessage(msg.chat().id(), Translation.get(DealBot.lang(msg.chat().id())).GAME_STARTED()).replyToMessageId(msg.messageId()));
                } else {
//                    bot.execute(new SendMessage(msg.chat().id(), Translation.get(DealBot.lang(msg.chat().id())).GAME_STARTING()).replyToMessageId(msg.messageId()));
                    // as if it's a join
                    new JoinCommand().respond(bot, msg, args);
                }
            }
        }
    }
}
