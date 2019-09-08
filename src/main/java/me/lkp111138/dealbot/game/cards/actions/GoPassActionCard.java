package me.lkp111138.dealbot.game.cards.actions;

import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;

public class GoPassActionCard extends ActionCard {
    @Override
    public String getCardFunctionalTitle() {
        return "GO Pass";
    }

    @Override
    public void use(GamePlayer player, String[] args) {
        player.addHand(player.getGame().draw());
        player.addHand(player.getGame().draw());
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
}
