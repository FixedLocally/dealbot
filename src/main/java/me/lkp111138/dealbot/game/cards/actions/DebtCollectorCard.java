package me.lkp111138.dealbot.game.cards.actions;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;

import java.util.List;

public class DebtCollectorCard extends ActionCard {
    public DebtCollectorCard() {
    }

    @Override
    public int currencyValue() {
        return 3;
    }

    @Override
    public String getCardFunctionalTitle() {
        return "Debt Collector";
    }

    @Override
    public String getDescription() {
        return "Collect $ 5M from one player.";
    }

    @Override
    public void use(GamePlayer player, String[] args) {
        // full wildcard: 1 payer
        // double wildcard: all pays
        List<GamePlayer> players = player.getGame().getGamePlayers();
        if (args.length > 0) {
            int order = Integer.parseInt(args[0]);
            // send them a huge debt!
            player.getGame().collectRentFromOne(5, 11, order);
            String victim = players.get(order).getName();
            EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(),
                    "Collecting debt of $ 5M from " + victim);
            player.getGame().execute(edit);
            String msg = String.format("You have used %s against %s.", getCardFunctionalTitle(), victim);
            SendMessage send = new SendMessage(player.getTgid(), msg);
            player.getGame().execute(send);
            msg = String.format("%s have used %s against %s.", player.getName(), getCardFunctionalTitle(), victim);
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
                buttons[j++][0] = new InlineKeyboardButton(user.getName()).callbackData(nonce + ":card_arg:" + i);
            }
            buttons[players.size() - 1][0] = new InlineKeyboardButton("Cancel").callbackData(nonce + ":use_cancel");
            EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), "Choose a player to collect your debt");
            edit.replyMarkup(new InlineKeyboardMarkup(buttons));
            player.getGame().execute(edit);
        }
    }

    @Override
    public String toString() {
        return "DebtCollectorCard{}";
    }
}
