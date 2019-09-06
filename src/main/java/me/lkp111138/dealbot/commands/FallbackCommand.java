package me.lkp111138.dealbot.commands;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import java.io.IOException;

public class FallbackCommand implements Command {
    @Override
    public void respond(TelegramBot bot, Message msg, String[] args) {
        if (msg.chat().type() == Chat.Type.Private) {
            bot.execute(new SendMessage(msg.chat().id(), "Unknown command.").replyToMessageId(msg.messageId()), new Callback<SendMessage, SendResponse>() {
                @Override
                public void onResponse(SendMessage request, SendResponse response) {
                    System.out.print(response);
                }

                @Override
                public void onFailure(SendMessage request, IOException e) {

                }
            });
        }
    }
}
