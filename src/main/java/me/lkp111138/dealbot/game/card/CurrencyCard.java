package me.lkp111138.dealbot.game.card;

import me.lkp111138.dealbot.game.card.state.CardState;
import me.lkp111138.dealbot.game.card.state.CardStateInMainDeck;

public class CurrencyCard implements Card {
    private int value;
    private CardState state = new CardStateInMainDeck();

    public CurrencyCard(int value) {
        this.value = value;
    }

    @Override
    public String getNameKey() {
        return "card.name.currency." + value;
    }

    @Override
    public String getDescriptionKey() {
        return "card.desc.currency." + value;
    }

    @Override
    public CardState getState() {
        return state;
    }

    public void setState(CardState state) {
        this.state = state;
    }
}
