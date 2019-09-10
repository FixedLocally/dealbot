package me.lkp111138.dealbot.game.cards.actions;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;
import me.lkp111138.dealbot.translation.Translation;

import java.util.Arrays;
import java.util.List;

public class RentActionCard extends ActionCard {
    private final int[] groups;

    public RentActionCard(int[] groups, Translation translation) {
        super(translation);
        this.groups = groups;
    }

    @Override
    public int currencyValue() {
        return this.groups.length > 2 ? 3 : 1;
    }

    @Override
    public String getDescription() {
        String players = groups.length == 2 ? "all players" : "a player";
        String _groups = "any group";
        if (groups.length == 2) {
            _groups = "group " + groups[0] + " or " + groups[1];
        }
        return "Collect rent for " + _groups + " from " + players;
    }

    @Override
    public String getCardFunctionalTitle() {
        if (groups.length == 2) {
            return translation.WILDCARD_RENT(groups[0], groups[1]);
        } else {
            return translation.RAINBOW_RENT();
        }
    }

    @Override
    public void use(GamePlayer player, String[] args) {
        // full wildcard: 1 payer
        // double wildcard: all pays
        boolean doubleRentBuff = player.isDoubleRentBuff();
        if (args.length > 0) {
            int group = Integer.parseInt(args[0]);
            int value = player.getGroupRent(group);
            if (groups.length == 2) {
                player.getGame().collectRentFromAll(value * (doubleRentBuff ? 2 : 1), group);
                player.setDoubleRentBuff(false);
                EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(),
                        "Collecting rent from everybody for group " + group);
                player.getGame().execute(edit);
                String msg = String.format("You have used %s for group %s.", getCardFunctionalTitle(), group);
                SendMessage send = new SendMessage(player.getTgid(), msg);
                player.getGame().execute(send);
                msg = String.format("%s have used %s for group %s.", player.getName(), getCardFunctionalTitle(), group);
                send = new SendMessage(player.getGame().getGid(), msg);
                player.getGame().execute(send);
            } else {
                List<GamePlayer> players = player.getGame().getGamePlayers();
                if (args.length > 1) {
                    // send them a huge rental bill!
                    int order = Integer.parseInt(args[1]);
                    player.getGame().collectRentFromOne(value * (doubleRentBuff ? 2 : 1), group, order);
                    player.setDoubleRentBuff(false);
                    String victim = players.get(order).getName();
                    EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(),
                            "Collecting rent from " + victim + " for group " + group);
                    player.getGame().execute(edit);
                    String msg = String.format("You have used %s against %s for group %s.",
                            getCardFunctionalTitle(), victim, group);
                    SendMessage send = new SendMessage(player.getTgid(), msg);
                    player.getGame().execute(send);
                    msg = String.format("%s have used %s against %s for group %s.", player.getName(),
                            getCardFunctionalTitle(), victim, group);
                    send = new SendMessage(player.getGame().getGid(), msg);
                    player.getGame().execute(send);
                } else {
                    InlineKeyboardButton[][] buttons = new InlineKeyboardButton[players.size()][1];
                    int nonce = player.getGame().nextNonce();
                    int j = 0;
                    for (int i = 0; i < players.size(); i++) {
                        GamePlayer user = players.get(i);
                        if (user.getTgid() == player.getTgid()) {
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
                buttons[i][0] = new InlineKeyboardButton("Group " + group + " ($ " + player.getGroupRent(group) * (doubleRentBuff ? 2 : 1) + "M)")
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
