package me.lkp111138.dealbot.game.cards.actions;

import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;

public class BlankActionCard extends ActionCard {
    @Override
    public int currencyValue() {
        return 1;
    }

    @Override
    public String getCardFunctionalTitle() {
        return "Blank Action Card";
    }

    @Override
    public void use(GamePlayer player, String[] args) {
        player.getGame().log("You've played a card!");
        player.promptForCard();
    }

    @Override
    public String toString() {
        return "BlankActionCard{}";
    }
}
