package me.lkp111138.dealbot.game.cards;

import java.util.Arrays;

public class WildcardPropertyCard extends PropertyCard {
    private final int[] groups;

    public WildcardPropertyCard(int currencyValue, String title, int[] groups) {
        super(currencyValue, title, -1);
        this.groups = groups;
    }

    public int[] getGroups() {
        return groups;
    }

    @Override
    public String toString() {
        return "WildcardPropertyCard{" +
                "groups=" + Arrays.toString(groups) +
                "}";
    }
}
