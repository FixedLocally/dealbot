package me.lkp111138.dealbot.game.cards.actions;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;

import java.util.Arrays;

public class RentActionCard extends ActionCard {
    private final int[] groups;

    public RentActionCard(int[] groups) {
        this.groups = groups;
    }

    @Override
    public int currencyValue() {
        return this.groups.length > 2 ? 3 : 1;
    }

    @Override
    public String getCardFunctionalTitle() {
        String _groups = "Rainbow";
        if (groups.length == 2) {
            _groups = groups[0] + " / " + groups[1];
        }
        return "Action Card - Rent [" + _groups + "]";
    }

    @Override
    public void use(GamePlayer player, String[] args) {
        // full wildcard: 1 payer
        // double wildcard: all pays
        if (args.length > 0) {
            int group = Integer.parseInt(args[0]);
            int value = player.getGroupRent(group);
            player.getGame().collectRentFromAll(group, value);
        } else {
            // ask to choose group
            InlineKeyboardButton[][] buttons = new InlineKeyboardButton[groups.length + 1][1];
            SendMessage send = new SendMessage(player.getTgid(), "Choose a group to collect rent for:");
            for (int i = 0; i < groups.length; i++) {
                int group = groups[i];
                buttons[i][0] = new InlineKeyboardButton("Group " + group + " ($ " + player.getGroupRent(group) + "M)")
                        .callbackData("card_arg:" + group);
            }
            buttons[groups.length][0] = new InlineKeyboardButton("Cancel").callbackData("use_cancel");
            send.replyMarkup(new InlineKeyboardMarkup(buttons));
            player.getGame().execute(send);
        }
    }

    @Override
    public String toString() {
        return "RentActionCard{" +
                "groups=" + Arrays.toString(groups) +
                "}";
    }
}
