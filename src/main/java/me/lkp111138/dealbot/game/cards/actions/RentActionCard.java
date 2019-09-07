package me.lkp111138.dealbot.game.cards.actions;

import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;

import java.util.Arrays;

public class RentActionCard extends ActionCard {
    private final int[] groups;

    public RentActionCard(int[] groups) {
        this.groups = groups;
    }

    @Override
    public int currencyValue() {
        return this.groups.length > 2 ? 3 : 1;
    }

    @Override
    public String getCardFunctionalTitle() {
        return "Action Card - Rent";
    }

    @Override
    public void use(GamePlayer player, String[] args) {
        // full wildcard: 1 payer
        // double wildcard: all pays
        player.promptForCard();
    }

    @Override
    public String toString() {
        return "RentActionCard{" +
                "groups=" + Arrays.toString(groups) +
                "}";
    }
}
