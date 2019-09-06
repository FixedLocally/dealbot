package me.lkp111138.dealbot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.ForwardMessage;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.Main;

public class FeedbackCommand implements Command {
    @Override
    public void respond(TelegramBot bot, Message msg, String[] args) {
        if (msg.replyToMessage() != null) {
            // replied, forward the message
            Message fwd_msg = msg.replyToMessage();
            ForwardMessage req = new ForwardMessage(Main.BOT_OWNER, fwd_msg.chat().id(), fwd_msg.messageId());
            bot.execute(req);
        }
//        ForwardMessage req = new ForwardMessage(Main.BOT_OWNER, msg.chat().id(), msg.messageId());
//        bot.execute(req);
        StringBuilder comment = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            comment.append(args[i]).append(' ');
        }
        if (comment.length() > 0 || msg.replyToMessage() != null) {
            // forward to owner
            String message = String.format("Feedback from <a href=\"tg://user?id=%d\">%s</a>" +
                            "in %s [%d]:\n%s", msg.from().id(), msg.from().firstName(), msg.chat().title(),
                    msg.chat().id(), comment);
            SendMessage req = new SendMessage(Main.BOT_OWNER, message);
            req.parseMode(ParseMode.HTML);
            bot.execute(req);
        }

    }
}
