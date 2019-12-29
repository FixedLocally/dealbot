package me.lkp111138.dealbot.game.card.state;

import me.lkp111138.dealbot.game.Player;

import java.util.Objects;

public class CardStateInPlayerProperty implements CardState {
    private Player player;
    private int colour;

    public CardStateInPlayerProperty(Player player, int colour) {
        this.player = player;
        this.colour = colour;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CardStateInPlayerProperty) {
            return ((CardStateInPlayerProperty) obj).player.equals(player) && ((CardStateInPlayerProperty) obj).colour == colour;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, colour) + 342978177;
    }
}
