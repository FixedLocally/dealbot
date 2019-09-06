package me.lkp111138.dealbot.game.cards;

import me.lkp111138.dealbot.game.GamePlayer;

public class PropertyCard implements Card {
    protected final int currencyValue;
    protected final String title;
    protected final int group;

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
    }

    public int getGroup() {
        return group;
    }
}
