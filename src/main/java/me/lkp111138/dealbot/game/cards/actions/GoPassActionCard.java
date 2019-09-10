package me.lkp111138.dealbot.game.cards.actions;

import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;
import me.lkp111138.dealbot.game.cards.Card;
import me.lkp111138.dealbot.translation.Translation;

public class GoPassActionCard extends ActionCard {
    public GoPassActionCard(Translation translation) {
        super(translation);
    }

    @Override
    public String getCardFunctionalTitle() {
        return translation.GO_PASS();
    }

    @Override
    public void use(GamePlayer player, String[] args) {
        Card[] cards = new Card[2];
        cards[0] = player.getGame().draw();
        cards[1] = player.getGame().draw();
        player.addHand(cards[0]);
        player.addHand(cards[1]);
        SendMessage send = new SendMessage(player.getTgid(), translation.YOU_HAVE_DRAWN() +
                cards[0].getCardTitle() + "\n" + cards[1].getCardTitle());
        player.getGame().execute(send);
        send = new SendMessage(player.getGame().getGid(), translation.SOMEONE_HAVE_USED(player.getName(), getCardFunctionalTitle()));
        player.getGame().execute(send);
        player.promptForCard();
    }

    @Override
    public int currencyValue() {
        return 1;
    }

    @Override
    public String getDescription() {
        return translation.GO_PASS_DESC();
    }

    @Override
    public String toString() {
        return "GoPassActionCard{}";
    }
}
