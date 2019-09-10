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
        return "Builds a hotel on top of your complete set of properties so its rent will be increased by $ 4M";
    }

    @Override
    public String getCardFunctionalTitle() {
        return translation.HOTEL();
    }
}
