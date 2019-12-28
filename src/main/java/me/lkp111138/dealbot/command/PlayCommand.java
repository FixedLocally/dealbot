package me.lkp111138.dealbot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.game.Game;
import me.lkp111138.dealbot.game.exception.ConcurrentGameException;

import java.sql.SQLException;

public class PlayCommand implements Command {
    @Override
    public void respond(TelegramBot bot, Message message, String[] args) {
        DealBot dealBot = (DealBot) bot;
        long gid = message.chat().id();
        switch (message.chat().type()) {
            case group:
            case supergroup:
                try {
                    new Game(dealBot, message.chat().id(), message);
                } catch (ConcurrentGameException e) {
                    bot.execute(new SendMessage(gid, dealBot.translate(gid, "game.is_running", e.getGid())));
                } catch (SQLException e) {
                    bot.execute(new SendMessage(gid, dealBot.translate(gid, "error.generic", e.getMessage())));
                }
                break;
            default:
                bot.execute(new SendMessage(gid, dealBot.translate(gid, "misc.run_in_group")));
                break;
        }
    }
}
