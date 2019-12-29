package me.lkp111138.dealbot.game.card;

import me.lkp111138.dealbot.game.CardArgumentRequest;
import me.lkp111138.dealbot.game.Player;
import me.lkp111138.dealbot.game.card.state.CardState;
import me.lkp111138.dealbot.game.card.state.CardStateInMainDeck;
import me.lkp111138.dealbot.game.card.state.CardStateInPlayerCurrency;

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
        return "card.desc.currency";
    }

    @Override
    public CardState getState() {
        return state;
    }

    @Override
    public int getCurrencyValue() {
        return value;
    }

    @Override
    public CardArgumentRequest execute(Player player, String[] arg) {
        setState(new CardStateInPlayerCurrency(player));
        return null;
    }

    public void setState(CardState state) {
        this.state = state;
    }
}
