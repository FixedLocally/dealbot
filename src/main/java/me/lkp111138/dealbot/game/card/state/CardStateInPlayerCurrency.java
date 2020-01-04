package me.lkp111138.dealbot.game.card.state;

import me.lkp111138.dealbot.game.Player;

public class CardStateInPlayerCurrency implements CardState {
    private Player player;

    public CardStateInPlayerCurrency(Player player) {
        this.player = player;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CardStateInPlayerCurrency) {
            return ((CardStateInPlayerCurrency) obj).player.equals(player);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return player.hashCode() + 342978177;
    }

    @Override
    public boolean isOwnedBy(Player player) {
        return player.equals(this.player);
    }
}
