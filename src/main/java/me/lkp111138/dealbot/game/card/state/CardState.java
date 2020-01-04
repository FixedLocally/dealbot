package me.lkp111138.dealbot.game.card.state;

import me.lkp111138.dealbot.game.Player;

/**
 * Describes the card's state, eg. in player's currency deck, in used deck, etc
 */
public interface CardState {
    /**
     * Tests if the states represents the card is owned by the player
     * @param player the player to be tested
     * @return if the state represents that the card is owned by the player, false otherwise
     */
    boolean isOwnedBy(Player player);
}
