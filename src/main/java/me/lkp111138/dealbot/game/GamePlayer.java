package me.lkp111138.dealbot.game;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import me.lkp111138.dealbot.game.cards.Card;
import me.lkp111138.dealbot.game.cards.PropertyCard;
import me.lkp111138.dealbot.game.cards.WildcardPropertyCard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePlayer {
    // player info
    private final int tgid;
    private final long gid;
    private final Game game;

    private int actionCount;
    private int messageId;

    // decks
    private final List<Card> hand = new ArrayList<>();
    private final List<Card> currencyDeck = new ArrayList<>();
    private final Map<Integer, List<Card>> propertyDecks = new HashMap<>();

    public GamePlayer(Game game, int tgid, long gid) {
        this.tgid = tgid;
        this.gid = gid;
        this.game = game;
    }

    public void removeHand(Card card) {
        hand.remove(card);
    }

    public void addHand(Card card) {
        hand.add(card);
    }

    public void removeCurrency(Card card) {
        currencyDeck.remove(card);
    }

    public void addCurrency(Card card) {
        if (!card.isCurrency()) {
            throw new RuntimeException("attempt to put non currency card in currency deck");
        }
        currencyDeck.add(card);
    }

    public void removeProperty(PropertyCard card, int group) {
        group = getRealCardGroup(card, group);
        propertyDecks.getOrDefault(group, new ArrayList<>()).remove(card);
    }

    public List<Card> removePropertyDeck(int group) {
        return propertyDecks.remove(group);
    }

    public void addProperty(PropertyCard card, int group) {
        group = getRealCardGroup(card, group);
        List<Card> deck = propertyDecks.getOrDefault(group, new ArrayList<>());
        deck.add(card);
        propertyDecks.put(group, deck);
    }

    private int getRealCardGroup(PropertyCard card, int group) {
        if (card instanceof WildcardPropertyCard) {
            WildcardPropertyCard wildcardPropertyCard = (WildcardPropertyCard) card;
            int[] groups = wildcardPropertyCard.getGroups();
            boolean found = false;
            for (int i : groups) {
                if (i == group) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new RuntimeException("incorrect wildcard property card in property deck");
            }
        } else {
            group = card.getGroup();
        }
        return group;
    }

    public boolean checkWinCondition() {
        // 3 full sets
        int sets = 0;
        for (Integer group : propertyDecks.keySet()) {
            if (propertyDecks.get(group).size() == PropertyCard.propertySetCounts[group]) {
                ++sets;
            }
        }
        return sets >= 3;
    }

    public void startTurn() {
        actionCount = 0;
        promptForCard();
    }

    public void promptForCard() {
        if (actionCount < 3) {
            // do prompt
            int size = hand.size() + (actionCount > 0 ? 1 : 0);
            InlineKeyboardButton[][] buttons = new InlineKeyboardButton[size][1];
            for (int i = 0; i < hand.size(); i++) {
                buttons[i][0] = new InlineKeyboardButton(hand.get(i).getCardTitle()).callbackData("play_card:" + i);
            }
            if (actionCount > 0) {
                buttons[hand.size()][0] = new InlineKeyboardButton("End turn").callbackData("end_turn");
            }
            String msg = String.format("Choose an action (%d remaining)", 3 - actionCount);
            if (messageId == 0) {
                SendMessage send = new SendMessage(tgid, msg);
                send.replyMarkup(new InlineKeyboardMarkup(buttons));
                game.execute(send, new Callback<SendMessage, SendResponse>() {
                    @Override
                    public void onResponse(SendMessage request, SendResponse response) {
                        messageId = response.message().messageId();
                    }

                    @Override
                    public void onFailure(SendMessage request, IOException e) {

                    }
                });
            } else {
                EditMessageText edit = new EditMessageText(tgid, messageId, msg);
                edit.replyMarkup(new InlineKeyboardMarkup(buttons));
                game.execute(edit);
            }
            ++actionCount;
        } else {
            // end turn
            game.execute(new DeleteMessage(tgid, messageId));
            messageId = 0;
            game.nextTurn();
        }
    }

    public int getTgid() {
        return tgid;
    }

    public Game getGame() {
        return game;
    }

    public Card handCardAt(int i) {
        return hand.get(i);
    }

    public void play(int i) {
        Card card = hand.get(i);
        if (card instanceof PropertyCard) {
            card.execute(this, new Object[]{((PropertyCard) card).getGroup()});
        } else {
            card.execute(this, new Object[0]);
        }
    }
}
