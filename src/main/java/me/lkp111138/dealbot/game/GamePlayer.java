package me.lkp111138.dealbot.game;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import me.lkp111138.dealbot.game.cards.ActionCard;
import me.lkp111138.dealbot.game.cards.Card;
import me.lkp111138.dealbot.game.cards.PropertyCard;
import me.lkp111138.dealbot.game.cards.WildcardPropertyCard;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GamePlayer {
    // player info
    private final int tgid;
    private final long gid;
    private final Game game;
    private final User user;

    private int actionCount;
    private int messageId;
    private int stateMessageId;
    private int globalStateMessageId;
    private int disposeMessageId;

    // decks
    private final List<Card> hand = new ArrayList<>();
    private final List<Card> currencyDeck = new ArrayList<>();
    private final Map<Integer, List<Card>> propertyDecks = new HashMap<>();

    public GamePlayer(Game game, int tgid, long gid, User user) {
        this.tgid = tgid;
        this.gid = gid;
        this.game = game;
        this.user = user;
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
        // tell the player that its their turn now
        SendMessage send = new SendMessage(game.getGid(),
                String.format(game.getTranslation().YOUR_TURN_ANNOUNCEMENT(), tgid, getName(), game.getTurnWait()));
        send.parseMode(ParseMode.HTML).disableWebPagePreview(true);
        game.execute(send);
        promptForCard();
    }

    public void promptForCard() {
        sendState();
        if (actionCount < 3) {
            game.schedule(this::endTurn, game.getTurnWait() * 1000);
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
            endTurn();
        }
    }

    public int getTgid() {
        return tgid;
    }

    public Game getGame() {
        return game;
    }

    public int getMessageId() {
        return messageId;
    }

    public List<Card> getCurrencyDeck() {
        return currencyDeck;
    }

    public Map<Integer, List<Card>> getPropertyDecks() {
        return propertyDecks;
    }

    public Card handCardAt(int i) {
        return hand.get(i);
    }

    public void play(Card card) {
        if (card instanceof PropertyCard) {
            card.execute(this, new String[0]);
        } else {
            card.execute(this, new String[0]);
        }
    }

    public String getMyState() {
        StringBuilder state = new StringBuilder("Cards in hand: ");
        state.append(handCount()).append("\n");
        int total = currencyDeck.stream().mapToInt(Card::currencyValue).sum();
        state.append("Currency deck (").append(currencyDeck.size()).append(" / $ ").append(total).append("M): ");
        for (Card card : currencyDeck) {
            if (card instanceof ActionCard) {
                state.append(card.getCardTitle()).append(", ");
            } else {
                state.append("$ ").append(card.currencyValue()).append("M, ");
            }
        }
        state.setLength(state.length() - 2);

        state.append("\nProperties:\n");
        for (Integer group : propertyDecks.keySet()) {
            List<Card> props = propertyDecks.get(group);
            if (props.isEmpty()) {
                continue;
            }
            state.append("Group ").append(group).append(props.size()).append("/")
                    .append(PropertyCard.propertySetCounts[group]).append("\n").append(" ($ ")
                    .append(PropertyCard.getRent(group, props.size()))
                    .append("M) ");
            for (Card prop : props) {
                state.append("- ").append(prop.getCardTitle()).append("\n");
            }
            state.append("\n");
        }
        return state.toString();
    }

    public void sendState() {
        if (stateMessageId == 0) {
            SendMessage send = new SendMessage(tgid, getMyState());
            game.execute(send, new Callback<SendMessage, SendResponse>() {
                @Override
                public void onResponse(SendMessage request, SendResponse response) {
                    stateMessageId = response.message().messageId();
                }

                @Override
                public void onFailure(SendMessage request, IOException e) {

                }
            });
        } else {
            EditMessageText edit = new EditMessageText(tgid, stateMessageId, getMyState());
            game.execute(edit);
        }
    }

    public void sendGlobalState(String s) {
        if (globalStateMessageId == 0) {
            SendMessage send = new SendMessage(tgid, s);
            game.execute(send, new Callback<SendMessage, SendResponse>() {
                @Override
                public void onResponse(SendMessage request, SendResponse response) {
                    globalStateMessageId = response.message().messageId();
                }

                @Override
                public void onFailure(SendMessage request, IOException e) {

                }
            });
        } else {
            EditMessageText edit = new EditMessageText(tgid, globalStateMessageId, s);
            game.execute(edit);
        }
    }

    public String getName() {
        return user.firstName();
    }

    public int handCount() {
        return hand.size();
    }

    public int currencyCount() {
        return currencyDeck.size();
    }

    public void addMove() {
        --actionCount;
    }

    public void endTurn() {
        game.execute(new DeleteMessage(tgid, messageId));
        messageId = 0;
        game.cancelFuture();
        // dispose cards?
        if (handCount() > 7) {
            // need to dispose cards
            InlineKeyboardButton[][] buttons = new InlineKeyboardButton[handCount()][1];
            for (int i = 0; i < hand.size(); i++) {
                buttons[i][0] = new InlineKeyboardButton(hand.get(i).getCardTitle()).callbackData("dispose_card:" + i);
            }
            String msg = String.format("Dispose some cards to keep you at 7 cards (%d remaining)", handCount() - 7);
            if (disposeMessageId == 0) {
                SendMessage send = new SendMessage(tgid, msg);
                send.replyMarkup(new InlineKeyboardMarkup(buttons));
                game.execute(send, new Callback<SendMessage, SendResponse>() {
                    @Override
                    public void onResponse(SendMessage request, SendResponse response) {
                        disposeMessageId = response.message().messageId();
                    }

                    @Override
                    public void onFailure(SendMessage request, IOException e) {

                    }
                });
                game.schedule(this::randomDispose, 15000);
            } else {
                EditMessageText edit = new EditMessageText(tgid, disposeMessageId, msg);
                edit.replyMarkup(new InlineKeyboardMarkup(buttons));
                game.execute(edit);
            }
        } else {
            if (disposeMessageId > 0) {
                game.execute(new DeleteMessage(tgid, disposeMessageId));
            }
            game.nextTurn();
        }
    }

    private void randomDispose() {
        Random rand = new Random();
        while (handCount() > 7) {
            Card disposed = handCardAt(rand.nextInt(handCount()));
            removeHand(disposed);
            game.addToMainDeck(disposed);
        }
        endTurn();
    }
}
