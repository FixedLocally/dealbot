package me.lkp111138.dealbot.game.cards;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import me.lkp111138.dealbot.game.GamePlayer;

import java.util.Arrays;

public class WildcardPropertyCard extends PropertyCard {
    private final int[] groups;

    public WildcardPropertyCard(int currencyValue, String title, int[] groups) {
        super(currencyValue, title, -1);
        this.groups = groups;
    }

    public int[] getGroups() {
        return groups;
    }

    @Override
    public String getCardTitle() {
        if (groups.length == 2) {
            return "[" + groups[0] + " / " + groups[1] + "] " + title;
        } else {
            return "[Rainbow] " + title;
        }
    }

    @Override
    public void execute(GamePlayer player, String[] args) {
        if (args.length > 0) {
            player.addProperty(this, Integer.parseInt(args[0]));
            player.promptForCard();
        }
        if (args.length == 0) {
            // ask for which group to put this card
            EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), "Use this card on which group?");
            InlineKeyboardButton[][] buttons = new InlineKeyboardButton[groups.length + 1][1];
            for (int i = 0; i < groups.length; i++) {
                buttons[i][0] = new InlineKeyboardButton(String.valueOf(groups[i])).callbackData("card_arg:" + groups[i]);
            }
            buttons[groups.length][0] = new InlineKeyboardButton("Cancel").callbackData("use_cancel");
            edit.replyMarkup(new InlineKeyboardMarkup(buttons));
            player.getGame().execute(edit);
        }
    }

    @Override
    public String toString() {
        return "WildcardPropertyCard{" +
                "groups=" + Arrays.toString(groups) +
                "}";
    }
}
