package me.lkp111138.dealbot.game.cards;

import me.lkp111138.dealbot.game.GamePlayer;

public class JustSayNoCard implements Card {
    @Override
    public boolean isObjectable() {
        return true;
    }

    @Override
    public int currencyValue() {
        return 4;
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
        return "Just Say No!";
    }

    @Override
    public void execute(GamePlayer player, String[] args) {
        throw new RuntimeException("just say no!");
    }

    @Override
    public String getDescription() {
        return "This card can be used to neutralize an action against you, including another Just Say No.";
    }
}
