package me.lkp111138.dealbot.game.cards;

public abstract class ActionCard implements Card {
    @Override
    public final boolean isObjectable() {
        return true;
    }

    @Override
    public final boolean isCurrency() {
        return true;
    }

    @Override
    public final boolean isAction() {
        return true;
    }

    @Override
    public final boolean isProperty() {
        return false;
    }
}
