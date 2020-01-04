package me.lkp111138.dealbot.game;

import com.pengrad.telegrambot.model.User;
import me.lkp111138.dealbot.game.card.Card;
import me.lkp111138.dealbot.game.card.state.CardStateInPlayerHand;
import me.lkp111138.dealbot.game.card.state.CardStateInPlayerProperty;

import java.util.*;

/**
 * Represents a player in the game
 */
public class Player {
    private Game game;
    private User user;

    // decks
    private List<Card> currencyDeck = new ArrayList<>();
    private Map<Integer, List<Card>> propertyDecks = new HashMap<>();
    private List<Card> handDeck = new ArrayList<>();

    public Player(Game game, User user) {
        this.game = game;
        this.user = user;
    }

    public Map<Integer, Integer> getPropertyCounts() {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            counts.put(i, 0);
        }
        for (Card card : game.getCards()) {
            if (card.getState() instanceof CardStateInPlayerProperty) {
                CardStateInPlayerProperty state = (CardStateInPlayerProperty) card.getState();
                if (!state.getPlayer().equals(this)) {
                    continue;
                }
                int colour = state.getColour();
                counts.put(colour, counts.get(colour) + 1);
            }
        }
        return counts;
    }

    public int getHandCount() {
        int count = 0;
        for (Card card : game.getCards()) {
            if (card.getState() instanceof CardStateInPlayerHand) {
                if (((CardStateInPlayerHand) card.getState()).getPlayer().equals(this)) {
                    ++count;
                }
            }
        }
        return count;
    }

    public Game getGame() {
        return game;
    }

    public int getUserId() {
        return user.id();
    }

    public String getName() {
        return user.firstName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return game.equals(player.game) &&
                user.equals(player.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(game, user);
    }
}
