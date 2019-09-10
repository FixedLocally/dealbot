package me.lkp111138.dealbot.game.cards.actions;

import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;
import me.lkp111138.dealbot.translation.Translation;

public class BlankActionCard extends ActionCard {
    protected BlankActionCard(Translation translation) {
        super(translation);
    }

    @Override
    public int currencyValue() {
        return 1;
    }

    @Override
    public String getCardFunctionalTitle() {
        return "Blank Action card";
    }

    @Override
    public void use(GamePlayer player, String[] args) {
        player.getGame().log("You've played a card!");
        player.promptForCard();
    }

    @Override
    public String getDescription() {
        return "Does nothing";
    }

    @Override
    public String toString() {
        return "BlankActionCard{}";
    }
}
