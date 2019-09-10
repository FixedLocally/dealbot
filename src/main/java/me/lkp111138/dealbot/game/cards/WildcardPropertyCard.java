package me.lkp111138.dealbot.game.cards;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.translation.Translation;

import java.util.ArrayList;
import java.util.Arrays;

public class WildcardPropertyCard extends PropertyCard {
    private final int[] groups;

    public WildcardPropertyCard(int currencyValue, String title, int[] groups, Translation translation) {
        super(currencyValue, title, -1, translation);
        this.groups = groups;
    }

    public int[] getGroups() {
        return groups;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    @Override
    public String getCardTitle() {
        if (groups.length == 2) {
            return "[" + groups[0] + " / " + groups[1] + "] " + title;
        } else {
            return title;
        }
    }

    @Override
    public String getDescription() {
        return getCardTitle();
    }

    @Override
    public void execute(GamePlayer player, String[] args) {
        if (args.length > 0) {
            player.addProperty(this, Integer.parseInt(args[0]));
            player.promptForCard();
            SendMessage send = new SendMessage(player.getTgid(), translation.YOU_PLACED_PROP_AS(getCardTitle(), Integer.parseInt(args[0])));
            player.getGame().execute(send);
            send = new SendMessage(player.getGame().getGid(), translation.SOMEONE_PLACED_PROP_AS(player.getName(), getCardTitle(), Integer.parseInt(args[0])));
            player.getGame().execute(send);
        }
        if (args.length == 0) {
            // ask for which group to put this card
            EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), translation.WILDCARD_CHOOSE_GROUP());
            InlineKeyboardButton[][] buttons = new InlineKeyboardButton[groups.length + 1][1];
            int nonce = player.getGame().nextNonce();
            for (int i = 0; i < groups.length; i++) {
                int possessed = player.getPropertyDecks().getOrDefault(groups[i], new ArrayList<>()).size();
                int total = PropertyCard.propertySetCounts[groups[i]];
                buttons[i][0] = new InlineKeyboardButton(groups[i] + " (" + possessed + "/" + total + ")").callbackData(nonce + ":card_arg:" + groups[i]);
            }
            buttons[groups.length][0] = new InlineKeyboardButton(translation.CANCEL()).callbackData(nonce + ":use_cancel");
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
