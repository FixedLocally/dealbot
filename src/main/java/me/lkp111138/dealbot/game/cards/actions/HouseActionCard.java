package me.lkp111138.dealbot.game.cards.actions;

public class HouseActionCard extends BuildingActionCard {
    @Override
    public String getCardFunctionalTitle() {
        return "House";
    }

    @Override
    public int currencyValue() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "Builds a house on top of your complete set of properties so its rent will be increased by $ 3M";
    }
}
