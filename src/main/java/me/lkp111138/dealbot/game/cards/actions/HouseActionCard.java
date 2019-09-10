package me.lkp111138.dealbot.game.cards.actions;

import me.lkp111138.dealbot.translation.Translation;

public class HouseActionCard extends BuildingActionCard {
    public HouseActionCard(Translation translation) {
        super(translation);
    }

    @Override
    public int currencyValue() {
        return 3;
    }

    @Override
    public String getDescription() {
        return translation.BUILDING_DESC(getCardFunctionalTitle(), 3);
    }

    @Override
    public String getCardFunctionalTitle() {
        return translation.HOUSE();
    }
}
