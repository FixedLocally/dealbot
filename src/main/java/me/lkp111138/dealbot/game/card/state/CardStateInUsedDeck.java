package me.lkp111138.dealbot.game.card.state;

import me.lkp111138.dealbot.game.Player;

public class CardStateInUsedDeck implements CardState {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof CardStateInUsedDeck;
    }

    @Override
    public int hashCode() {
        return 2080202354;
    }

    @Override
    public boolean isOwnedBy(Player player) {
        return false;
    }
}
