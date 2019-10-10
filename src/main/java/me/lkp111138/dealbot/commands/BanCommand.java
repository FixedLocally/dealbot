package me.lkp111138.dealbot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.Main;

public class BanCommand implements Command {
    private final String mode;

    public BanCommand(String mode) {
        this.mode = mode;
    }

    @Override
    public void respond(TelegramBot bot, Message msg, String[] args) {
        // only bot owner can ban people
        if (msg.from().id() == Main.BOT_OWNER) {
            try {
                long tgid = Long.parseLong(args[1]);
                int time = Integer.parseInt(args[2]);
                String[] _args = new String[args.length - 3];
                System.arraycopy(args, 3, _args, 0, args.length - 3);
                System.out.println("executing ban for " + tgid);
                if (DealBot.executeBan(tgid, mode, time, String.join(" ", _args))) {
                    bot.execute(new SendMessage(msg.chat().id(), "Success").replyToMessageId(msg.messageId()));
                }
            } catch (NumberFormatException e) {
                bot.execute(new SendMessage(msg.chat().id(), "Error: " + e.getMessage()).replyToMessageId(msg.messageId()));
            }
        }
    }
}
