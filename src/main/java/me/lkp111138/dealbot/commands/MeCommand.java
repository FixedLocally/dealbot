package me.lkp111138.dealbot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.game.Game;
import me.lkp111138.dealbot.game.GamePlayer;

public class MeCommand implements Command {
    @Override
    public void respond(TelegramBot bot, Message msg, String[] args) {
        Game g = Game.byUser(msg.from().id());
        if (g != null) {
            for (GamePlayer gamePlayer : g.getGamePlayers()) {
                if (gamePlayer.getTgid() == msg.from().id()) {
                    gamePlayer.sendStateForce();
                    break;
                }
            }
            if (msg.chat().type() != Chat.Type.Private) {
                String _msg = g.getTranslation().ME_CMD_PMED();
                SendMessage send = new SendMessage(msg.chat().id(), _msg);
                g.execute(send);
            }
        }
    }
}
