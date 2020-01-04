package me.lkp111138.dealbot.game.card.state;

import me.lkp111138.dealbot.game.Player;

public class CardStateInMainDeck implements CardState {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof CardStateInMainDeck;
    }

    @Override
    public int hashCode() {
        return 1931197961;
    }

    @Override
    public boolean isOwnedBy(Player player) {
        return false;
    }
}
