package me.lkp111138.dealbot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import me.lkp111138.dealbot.game.Game;

public class FleeCommand extends GroupOnlyCommand {
    @Override
    public void respondInGroup(TelegramBot bot, Message message, String[] args) {
        long groupId = message.chat().id();
        Game game = Game.byGroup(groupId);
        if (game != null) {
            game.removePlayer(message);
        }
    }
}
