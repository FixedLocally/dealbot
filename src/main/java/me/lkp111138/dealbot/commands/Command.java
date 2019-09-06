package me.lkp111138.dealbot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;

public interface Command {
    /**
     * @param bot  the bot the received the command
     * @param msg  the message concerned
     * @param args the command call, split by spaces
     */
    void respond(TelegramBot bot, Message msg, String[] args);
}
