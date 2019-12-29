package me.lkp111138.dealbot.game;

import me.lkp111138.dealbot.game.card.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a player in the game
 */
public class Player {
    private Game game;
    private long userId;

    // decks
    private List<Card> currencyDeck = new ArrayList<>();
    private Map<Integer, List<Card>> propertyDecks = new HashMap<>();
    private List<Card> handDeck = new ArrayList<>();

    public Player(Game game, long userId) {
        this.game = game;
        this.userId = userId;
    }
}
