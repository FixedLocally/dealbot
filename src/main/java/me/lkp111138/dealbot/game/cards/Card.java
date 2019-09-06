package me.lkp111138.dealbot.game.cards;

import me.lkp111138.dealbot.game.GamePlayer;

public interface Card {
    /**
     * @return true is objectable, false otherwise
     */
    boolean isObjectable();

    /**
     * The value of the card if used as currency
     * @return the currency value
     */
    int currencyValue();

    /**
     * @return true if the card is a currency card, false otherwise
     */
    boolean isCurrency();

    /**
     * @return true if the card is an action card, false otherwise
     */
    boolean isAction();

    /**
     * @return true if the card is a property card, false otherwise
     */
    boolean isProperty();

    /**
     * @return the title of the card
     */
    String getCardTitle();

    /**
     * Executes the card logic.
     * @param player the player that played the card
     * @param args the arguments passed to the card, such as target card or player
     */
    void execute(GamePlayer player, Object[] args);
}
