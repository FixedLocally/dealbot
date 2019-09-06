package me.lkp111138.dealbot.game;

import me.lkp111138.dealbot.game.cards.Card;

public class GamePlayer {
    // player info
    int tgid;
    int gid;

    // decks
    private Card[] hand;
    private Card[] currencyDeck;
    private Card[][] propertyDecks;
}
