package me.lkp111138.dealbot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.translation.Translation;

public class HelpCommand implements Command {
    @Override
    public void respond(TelegramBot bot, Message msg, String[] args) {
        long sender = msg.from().id();
        if (msg.chat().type() == Chat.Type.Private) {
            SendMessage req = new SendMessage(sender, Translation.get(DealBot.lang(sender)).HELP()).parseMode(ParseMode.HTML);
            bot.execute(req);
        } else {
            SendMessage req = new SendMessage(sender, Translation.get(DealBot.lang(msg.chat().id())).HELP()).parseMode(ParseMode.HTML);
            bot.execute(req);
        }
    }
}
