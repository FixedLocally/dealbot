package me.lkp111138.dealbot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import me.lkp111138.dealbot.Main;
import me.lkp111138.dealbot.game.Game;

public class KillGameCommand implements Command {
    @Override
    public void respond(TelegramBot bot, Message msg, String[] args) {
        switch (msg.chat().type()) {
            case Private:
            case channel:
                return;
        }
        // only bot owner can kill game
        if (msg.from().id() == Main.BOT_OWNER) {
            if (Game.byGroup(msg.chat().id()) != null) {
                Game.byGroup(msg.chat().id()).kill();
            }
        }
    }
}
