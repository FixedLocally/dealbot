package me.lkp111138.dealbot.game.cards.actions;

import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;

/**
 * The 'Deal Breaker' card. Takes one complete set of property from an opponent.
 */
public class DealBreakerActionCard extends ActionCard {
    @Override
    public int currencyValue() {
        return 5;
    }

    @Override
    public String getCardTitle() {
        return "Deal Breaker";
    }

    @Override
    public void execute(GamePlayer player, String[] args) {
//        int deckId = (int) args[1];
//        GamePlayer victim = (GamePlayer) args[0];
        // TODO
    }
}
