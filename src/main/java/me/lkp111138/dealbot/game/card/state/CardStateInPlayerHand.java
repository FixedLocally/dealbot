package me.lkp111138.dealbot.game.card.state;

import me.lkp111138.dealbot.game.Player;

public class CardStateInPlayerHand implements CardState {
    private Player player;

    public CardStateInPlayerHand(Player player) {
        this.player = player;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CardStateInPlayerHand) {
            return ((CardStateInPlayerHand) obj).player.equals(player);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return player.hashCode() + 1469440751;
    }
}
