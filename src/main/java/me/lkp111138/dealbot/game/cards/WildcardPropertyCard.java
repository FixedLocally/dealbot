package me.lkp111138.dealbot.game.cards;

public class WildcardPropertyCard extends PropertyCard {
    private final int[] groups;

    public WildcardPropertyCard(int currencyValue, String title, int[] groups) {
        super(currencyValue, title, -1);
        this.groups = groups;
    }

    public int[] getGroups() {
        return groups;
    }
}
