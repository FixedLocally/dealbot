package me.lkp111138.dealbot.game.card.state;

public class CardStateInMainDeck implements CardState {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof CardStateInMainDeck;
    }

    @Override
    public int hashCode() {
        return 1931197961;
    }
}
