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
        if (groups.length == 2) {
            return translation.RENT_CARD_DESC(groups[0], groups[1]);
        }
        return translation.RAINBOW_RENT_CARD_DESC();
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
        int doubleRentBuff = player.getDoubleRentBuff();
        if (args.length > 0) {
            int group = Integer.parseInt(args[0]);
            int value = player.getGroupRent(group) * doubleRentBuff;
            if (groups.length == 2) {
                player.getGame().collectRentFromAll(value * doubleRentBuff, group);
                player.setDoubleRentBuff(false);
                EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(),
                        translation.COLLECTING_RENT(null, group, value));
                player.getGame().execute(edit);
                String msg = translation.YOU_HAVE_USED_RENT_FOR(getCardFunctionalTitle(), group, value);
                SendMessage send = new SendMessage(player.getTgid(), msg);
                player.getGame().execute(send);
                msg = translation.SOMEONE_HAVE_USED_RENT_FOR(player.getName(), getCardFunctionalTitle(), group, value);
                send = new SendMessage(player.getGame().getGid(), msg);
                player.getGame().execute(send);
            } else {
                List<GamePlayer> players = player.getGame().getGamePlayers();
                if (args.length > 1) {
                    // send them a huge rental bill!
                    int order = Integer.parseInt(args[1]);
                    player.getGame().collectRentFromOne(value * doubleRentBuff, group, order);
                    player.setDoubleRentBuff(false);
                    String victim = players.get(order).getName();
                    EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(),
                            translation.COLLECTING_RENT(victim, group, value * doubleRentBuff));
                    player.getGame().execute(edit);
                    String msg = translation.YOU_HAVE_USED_RENT_FOR_AGAINST(getCardFunctionalTitle(), victim, group, value);
                    SendMessage send = new SendMessage(player.getTgid(), msg);
                    player.getGame().execute(send);
                    msg = translation.SOMEONE_HAVE_USED_RENT_FOR_AGAINST(player.getName(), getCardFunctionalTitle(), victim, group, value);
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
                    buttons[players.size() - 1][0] = new InlineKeyboardButton(translation.CANCEL()).callbackData(nonce + ":use_cancel");
                    EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), translation.RENT_CHOOSE_PLAYER());
                    edit.replyMarkup(new InlineKeyboardMarkup(buttons));
                    player.getGame().execute(edit);
                }
            }
        } else {
            // ask to choose group
            InlineKeyboardButton[][] buttons = new InlineKeyboardButton[groups.length + 1][1];
            EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), translation.RENT_CHOOSE_GROUP());
            int nonce = player.getGame().nextNonce();
            for (int i = 0; i < groups.length; i++) {
                int group = groups[i];
                buttons[i][0] = new InlineKeyboardButton(translation.PROPERTY_GROUP(group) + " ($ " + player.getGroupRent(group) + "M x " + doubleRentBuff + ")")
                        .callbackData(nonce + ":card_arg:" + group);
            }
            buttons[groups.length][0] = new InlineKeyboardButton(translation.CANCEL()).callbackData(nonce + ":use_cancel");
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
