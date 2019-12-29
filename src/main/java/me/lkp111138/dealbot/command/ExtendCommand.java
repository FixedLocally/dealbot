package me.lkp111138.dealbot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import me.lkp111138.dealbot.game.Game;

public class ExtendCommand extends GroupOnlyCommand {
    @Override
    public void respondInGroup(TelegramBot bot, Message message, String[] args) {
        long gid = message.chat().id();
        Game game = Game.byGroup(gid);
        if (game != null) {
            game.extend(30);
        }
    }
}
