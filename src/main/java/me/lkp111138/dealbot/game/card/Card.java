package me.lkp111138.dealbot.game.card;

import me.lkp111138.dealbot.game.card.state.CardState;

public interface Card {
    /**
     * @return The translation key for this card's name
     */
    String getNameKey();

    /**
     * @return The translation key for this card's description
     */
    String getDescriptionKey();

    /**
     * @return The card's current state
     */
    CardState getState();
}
