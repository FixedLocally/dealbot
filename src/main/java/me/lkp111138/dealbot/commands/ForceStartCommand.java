package me.lkp111138.dealbot.commands;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.response.GetChatMemberResponse;
import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.Main;
import me.lkp111138.dealbot.game.Game;

import java.io.IOException;

public class ForceStartCommand implements Command {
    @Override
    public void respond(TelegramBot bot, Message msg, String[] args) {
        switch (msg.chat().type()) {
            case Private:
            case channel:
                return;
        }
        final Game game = Game.byGroup(msg.chat().id());
        if (game == null || game.started()) {
            return;
        }
        // whitelist
        if (msg.from().id() == Main.BOT_OWNER) {
            if (DealBot.queryBan(msg.from().id()) == null) {
                game.tryStart();
            }
            return;
        }
        bot.execute(new GetChatMember(msg.chat().id(), msg.from().id()), new Callback<GetChatMember, GetChatMemberResponse>() {
            @Override
            public void onResponse(GetChatMember request, GetChatMemberResponse response) {
                ChatMember chatMember = response.chatMember();
                if (chatMember.status() == ChatMember.Status.administrator || chatMember.status() == ChatMember.Status.creator) {
                    // kill game
                    game.tryStart();
                }
            }

            @Override
            public void onFailure(GetChatMember request, IOException e) {

            }
        });
    }
}
