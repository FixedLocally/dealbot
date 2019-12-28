package me.lkp111138.dealbot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.DealBot;

public class FallbackCommand implements Command {
    @Override
    public void respond(TelegramBot bot, Message message, String[] args) {
        if (message.chat().type() == Chat.Type.Private || true) {
            int sender = message.from().id();
            DealBot dealBot = (DealBot) bot;
            bot.execute(new SendMessage(message.chat().id(), dealBot.translate(sender, "misc.unknown_cmd")));
        }
    }
}
