package me.lkp111138.dealbot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.DealBot;

public abstract class GroupOnlyCommand implements Command {
    @Override
    public final void respond(TelegramBot bot, Message message, String[] args) {
        DealBot dealBot = (DealBot) bot;
        long chatId = message.chat().id();
        switch (message.chat().type()) {
            case group:
            case supergroup:
                respondInGroup(bot, message, args);
                break;
            default:
                bot.execute(new SendMessage(chatId, dealBot.translate(chatId, "misc.run_in_group")));
                break;
        }
    }

    public abstract void respondInGroup(TelegramBot bot, Message message, String[] args);
}
