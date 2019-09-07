package me.lkp111138.dealbot.game.cards.actions;

import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;

import java.util.Arrays;
import java.util.List;

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
            if (groups.length == 2) {
                player.getGame().collectRentFromAll(value, group);
                EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), "Collecting rent from everybody for group " + group);
                player.getGame().execute(edit);
            } else {
                List<User> players = player.getGame().getPlayers();
                if (args.length > 1) {
                    // send them a huge rental bill!
                    int order = Integer.parseInt(args[1]);
                    player.getGame().collectRentFromOne(value, group, order);
                } else {
                    InlineKeyboardButton[][] buttons = new InlineKeyboardButton[players.size() + 1][1];
                    for (int i = 0; i < players.size(); i++) {
                        User user = players.get(i);
                        if (user.id() == player.getTgid()) {
                            continue;
                        }
                        buttons[j++][0] = new InlineKeyboardButton(user.getName()).callbackData(nonce + ":card_arg:" + group + ":" + i);
                    }
                    buttons[players.size() - 1][0] = new InlineKeyboardButton("Cancel").callbackData(nonce + ":use_cancel");
                    EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), "Choose a player to collect this rent");
                    edit.replyMarkup(new InlineKeyboardMarkup(buttons));
                    player.getGame().execute(edit);
                }
            }
        } else {
            // ask to choose group
            InlineKeyboardButton[][] buttons = new InlineKeyboardButton[groups.length + 1][1];
            EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), "Choose a group to collect rent for:");
            int nonce = player.getGame().nextNonce();
            for (int i = 0; i < groups.length; i++) {
                int group = groups[i];
                buttons[i][0] = new InlineKeyboardButton("Group " + group + " ($ " + player.getGroupRent(group) + "M)")
                        .callbackData(nonce + ":card_arg:" + group);
            }
            buttons[groups.length][0] = new InlineKeyboardButton("Cancel").callbackData(nonce + ":use_cancel");
            edit.replyMarkup(new InlineKeyboardMarkup(buttons));
            player.getGame().execute(edit);
        }
    }

    @Override
    public String toString() {
        return "RentActionCard{" +
                "groups=" + Arrays.toString(groups) +
                "}";
    }
}
