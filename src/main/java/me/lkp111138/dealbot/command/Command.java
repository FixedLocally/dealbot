package me.lkp111138.dealbot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;

public interface Command {
    /**
     * Processes the command.
     * @param message The Message object that contains the command
     * @param args The arguments passed to the command, much like C-style argv
     */
    void respond(TelegramBot bot, Message message, String[] args);
}
