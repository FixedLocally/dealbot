package me.lkp111138.dealbot.game.cards.actions;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;
import me.lkp111138.dealbot.translation.Translation;

import java.util.List;

public class DebtCollectorCard extends ActionCard {
    public DebtCollectorCard(Translation translation) {
        super(translation);
    }

    @Override
    public int currencyValue() {
        return 3;
    }

    @Override
    public String getDescription() {
        return translation.DEBT_COLLECTOR_DESC();
    }

    @Override
    public String getCardFunctionalTitle() {
        return translation.DEBT_COLLECTOR();
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
                    translation.DEBT_COLLECTOR_PROMPT(victim));
            player.getGame().execute(edit);
            SendMessage send = new SendMessage(player.getGame().getGid(), translation.SOMEONE_HAVE_USED_AGAINST(player.getName(), getCardFunctionalTitle(), victim));
            player.getGame().execute(send);
            String msg = translation.YOU_HAVE_USED_AGAINST(getCardFunctionalTitle(), victim);
            send = new SendMessage(player.getTgid(), msg);
            player.getGame().execute(send);
            msg = translation.SOMEONE_HAVE_USED_AGAINST(player.getName(), getCardFunctionalTitle(), victim);
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
            buttons[players.size() - 1][0] = new InlineKeyboardButton(translation.CANCEL()).callbackData(nonce + ":use_cancel");
            EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), translation.DEBT_COLLECTOR_CHOOSE_PROMPT());
            edit.replyMarkup(new InlineKeyboardMarkup(buttons));
            player.getGame().execute(edit);
        }
    }

    @Override
    public String toString() {
        return "DebtCollectorCard{}";
    }
}
