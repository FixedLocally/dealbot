package me.lkp111138.dealbot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.LabeledPrice;
import com.pengrad.telegrambot.request.SendInvoice;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.Main;

public class DonateCommand implements Command {
    @Override
    public void respond(TelegramBot bot, Message msg, String[] args) {
        int amount = 2000;
        if (args.length > 1) {
            try {
                amount = (int) (100 * Double.parseDouble(args[1]));
            } catch (NumberFormatException e) {
                // invalid amount
                SendMessage send = new SendMessage(msg.chat().id(), "Please enter a valid donation amount!");
                send.replyToMessageId(msg.messageId());
                bot.execute(send);
                return;
            }
        }
        SendInvoice send = new SendInvoice(msg.from().id(), Main.getConfig("bot.username") + " Donation",
                "Support the bot development", "" + amount, Main.getConfig("bot.stripe_token"),
                "donate_" + amount, "HKD", new LabeledPrice("Donation", amount));
        bot.execute(send);
    }
}
