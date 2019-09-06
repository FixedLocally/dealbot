package me.lkp111138.dealbot.game.cards.actions;

import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;

public class BlankActionCard extends ActionCard {
    @Override
    public int currencyValue() {
        return 1;
    }

    @Override
    public String getCardTitle() {
        return "Blank Action Card";
    }

    @Override
    public void execute(GamePlayer player, Object[] args) {

    }
}
