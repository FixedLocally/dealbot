package me.lkp111138.dealbot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.game.Game;
import me.lkp111138.dealbot.translation.Translation;

public class FleeCommand implements Command {
    @Override
    public void respond(TelegramBot bot, Message msg, String[] args) {
        Game g = Game.byGroup(msg.chat().id());
        if (g != null && g.removePlayer(msg.from().id())) {
            int remaining = g.playerCount();
            bot.execute(new SendMessage(msg.chat().id(), String.format(Translation.get(g.getLang()).FLEE_ANNOUNCEMENT(), msg.from().id(), msg.from().firstName(), remaining, remaining != 1 ? "s" : "")).parseMode(ParseMode.HTML).replyToMessageId(msg.messageId()));
        }
    }
}
