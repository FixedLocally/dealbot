package me.lkp111138.dealbot.game.cards.actions;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;
import me.lkp111138.dealbot.game.cards.PropertyCard;

import java.util.ArrayList;
import java.util.List;

public abstract class BuildingActionCard extends ActionCard {
    @Override
    public final void use(GamePlayer player, String[] args) {
        if (args.length == 0) {
            // choose group
            List<InlineKeyboardButton[]> buttons = new ArrayList<>();
            int nonce = player.getGame().nextNonce();
            for (Integer group : player.getPropertyDecks().keySet()) {
                if (player.getPropertyDecks().get(group).size() >= PropertyCard.propertySetCounts[group]) {
                    buttons.add(new InlineKeyboardButton[]{new InlineKeyboardButton("Group " + group)
                            .callbackData(nonce + ":card_arg:" + group)});
                }
            }
            buttons.add(new InlineKeyboardButton[]{new InlineKeyboardButton("Cancel")
                    .callbackData(nonce + ":use_cancel")});
            EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), "Build this" + getCardFunctionalTitle() + " on which group?");
            edit.replyMarkup(new InlineKeyboardMarkup(buttons.toArray(new InlineKeyboardButton[0][0])));
            player.getGame().execute(edit);
        }
        if (args.length == 1) {
            // group chosen, add to list
            int group = Integer.parseInt(args[0]);
            player.addBuilding(this, group);
            player.promptForCard();
        }
    }
}
