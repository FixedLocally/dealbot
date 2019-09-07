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

    public static int getRent(int group, int count) {
        if (count <= 0) {
            return 0;
        }
        if (count >= propertyRents[group].length) {
            return propertyRents[group][propertyRents[group].length - 1];
        }
        return propertyRents[group][count - 1];
    }

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
        return "[" + group + "] " + title;
    }

    @Override
    public void execute(GamePlayer player, String[] args) {
        player.addProperty(this, group);
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
