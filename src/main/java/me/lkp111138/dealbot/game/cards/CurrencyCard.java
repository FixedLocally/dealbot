package me.lkp111138.dealbot.game.cards;

import me.lkp111138.dealbot.game.GamePlayer;

public class CurrencyCard implements Card {
    private final int currencyValue;
    private final String title;
    private final int group;

    public CurrencyCard(int currencyValue, String title, int group) {
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
        return true;
    }

    @Override
    public boolean isAction() {
        return false;
    }

    @Override
    public boolean isProperty() {
        return false;
    }

    @Override
    public String getCardTitle() {
        return title;
    }

    @Override
    public void execute(GamePlayer player, Object[] args) {
        player.addCurrency(this);
    }

    public int getGroup() {
        return group;
    }
}
