package me.lkp111138.dealbot.game.cards.actions;

public class HotelActionCard extends BuildingActionCard {
    @Override
    public String getCardFunctionalTitle() {
        return "Hotel";
    }

    @Override
    public int currencyValue() {
        return 4;
    }

    @Override
    public String getDescription() {
        return "Builds a hotel on top of your complete set of properties so its rent will be increased by $ 4M";
    }
}
