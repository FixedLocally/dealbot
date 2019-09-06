package me.lkp111138.dealbot.game.cards;

import me.lkp111138.dealbot.game.GamePlayer;

public class PropertyCard implements Card {
    protected final int currencyValue;
    protected final String title;
    protected final int group;

    // there are currently 10 groups of properties
    public static final int[] propertySetCounts = {2, 3, 3, 3, 3, 3, 3, 2, 4, 2};
    public static final int[][] propertyRents = {
            {1, 2},
            {1, 2, 3},
            {1, 2, 4},
            {1, 3, 5},
            {2, 3, 6},
            {2, 4, 6},
            {2, 4, 7},
            {3, 8},
            {1, 2, 3, 4},
            {1, 2}
    };

    public PropertyCard(int currencyValue, String title, int group) {
        this.currencyValue = currencyValue;
        this.title = title;
        this.group = group;
    }

    @Override
    public boolean isObjectable() {
        return false;
    }

    @Override
    public int currencyValue() {
        return currencyValue;
    }

    @Override
    public boolean isCurrency() {
        return false;
    }

    @Override
    public boolean isAction() {
        return false;
    }

    @Override
    public boolean isProperty() {
        return true;
    }

    @Override
    public String getCardTitle() {
        return title;
    }

    @Override
    public void execute(GamePlayer player, Object[] args) {
        player.addProperty(this, (int) args[0]);
        player.promptForCard();
    }

    public int getGroup() {
        return group;
    }

    @Override
    public String toString() {
        return "PropertyCard{" +
                "title='" + title + '\'' +
                ", group=" + group +
                '}';
    }
}
