package me.lkp111138.dealbot.game.card.state;

public class CardStateInUsedDeck implements CardState {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof CardStateInUsedDeck;
    }

    @Override
    public int hashCode() {
        return 2080202354;
    }
}
