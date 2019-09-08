package me.lkp111138.dealbot.game.cards.actions;

import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;
import me.lkp111138.dealbot.game.cards.Card;

public class GoPassActionCard extends ActionCard {
    @Override
    public String getCardFunctionalTitle() {
        return "GO Pass";
    }

    @Override
    public void use(GamePlayer player, String[] args) {
        Card[] cards = new Card[2];
        cards[0] = player.getGame().draw();
        cards[1] = player.getGame().draw();
        player.addHand(cards[0]);
        player.addHand(cards[1]);
        SendMessage send = new SendMessage(player.getTgid(), "You have drawn the following cards from the deck:\n" +
                cards[0].getCardTitle() + "\n" + cards[1].getCardTitle());
        player.getGame().execute(send);
        send = new SendMessage(player.getGame().getGid(), player.getName() + " has used " + getCardFunctionalTitle());
        player.getGame().execute(send);
        player.promptForCard();
    }

    @Override
    public int currencyValue() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Draw two cards from the deck";
    }

    @Override
    public String toString() {
        return "GoPassActionCard{}";
    }
}
