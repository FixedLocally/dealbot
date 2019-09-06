package me.lkp111138.dealbot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.Main;
import me.lkp111138.dealbot.game.Game;

public class MaintModeCommand implements Command {
    @Override
    public void respond(TelegramBot bot, Message msg, String[] args) {
        if (msg.from().id() == Main.BOT_OWNER) {
            Game.maintMode = !Game.maintMode;
            bot.execute(new SendMessage(Main.BOT_OWNER, "Maintenance mode: " + Game.maintMode));
        }
    }
}
