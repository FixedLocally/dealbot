package me.lkp111138.dealbot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import me.lkp111138.dealbot.game.Game;

public class ExtendCommand implements Command {
    @Override
    public void respond(TelegramBot bot, Message msg, String[] args) {
        Game g = Game.byGroup(msg.chat().id());
        if (g != null) {
            g.extend();
        }
    }
}
