package me.lkp111138.dealbot.game.cards.actions;

import me.lkp111138.dealbot.translation.Translation;

public class HotelActionCard extends BuildingActionCard {
    public HotelActionCard(Translation translation) {
        super(translation);
    }

    @Override
    public int currencyValue() {
        return 4;
    }

    @Override
    public String getDescription() {
        return translation.BUILDING_DESC(getCardFunctionalTitle(), 4);
    }

    @Override
    public String getCardFunctionalTitle() {
        return translation.HOTEL();
    }
}
