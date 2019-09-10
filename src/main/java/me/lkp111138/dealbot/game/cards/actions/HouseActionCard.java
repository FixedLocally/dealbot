package me.lkp111138.dealbot.game.cards.actions;

import me.lkp111138.dealbot.translation.Translation;

public class HouseActionCard extends BuildingActionCard {
    protected HouseActionCard(Translation translation) {
        super(translation);
    }

    @Override
    public int currencyValue() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "Builds a house on top of your complete set of properties so its rent will be increased by $ 3M";
    }

    @Override
    public String getCardFunctionalTitle() {
        return null;
    }
}
