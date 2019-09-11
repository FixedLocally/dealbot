package me.lkp111138.dealbot.game;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import me.lkp111138.dealbot.game.cards.Card;
import me.lkp111138.dealbot.game.cards.JustSayNoCard;
import me.lkp111138.dealbot.game.cards.PropertyCard;
import me.lkp111138.dealbot.game.cards.WildcardPropertyCard;
import me.lkp111138.dealbot.game.cards.actions.BuildingActionCard;
import me.lkp111138.dealbot.translation.Translation;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GamePlayer {
    // player info
    private final int tgid;
    private final long gid;
    private final Game game;
    private final User user;
    private final Translation translation;

    private boolean doubleRentBuff = false;

    private int actionCount;
    private int messageId;
    private int stateMessageId;
    private int globalStateMessageId;
    private int disposeMessageId;

    private Set<Integer> paymentSelectedIndices = new HashSet<>();
    private Set<Integer> paymentSelectedPropertyIndices = new HashSet<>();
    private int paymentMessageId;
    private String paymentMessage;
    private int paymentValue;

    private Runnable savedActionIfNotObjected;
    private Runnable savedActionIfObjected;

    private ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(2);
    private ScheduledFuture future;

    // decks
    private final List<Card> hand = new ArrayList<>();
    private final List<Card> currencyDeck = new ArrayList<>();
    private final Map<Integer, List<Card>> propertyDecks = new HashMap<>();

    public GamePlayer(Game game, int tgid, long gid, User user) {
        this.tgid = tgid;
        this.gid = gid;
        this.game = game;
        this.user = user;
        this.translation = game.getTranslation();
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

    public boolean addProperty(PropertyCard card, int group) {
        group = getRealCardGroup(card, group);
        List<Card> deck = propertyDecks.getOrDefault(group, new ArrayList<>());
        if (deck.size() >= PropertyCard.propertySetCounts[group]) {
            // this deck is full
            return false;
        }
        if (!deck.contains(card)) {
            deck.add(card);
            if (card instanceof WildcardPropertyCard) {
                ((WildcardPropertyCard) card).setGroup(group);
            }
            propertyDecks.put(group, deck);
        } else {
            System.out.printf("%s already in %s", card, deck);
            Thread.dumpStack();
        }
        return true;
    }

    public void addBuilding(BuildingActionCard card, int group) {
        List<Card> deck = propertyDecks.get(group);
        deck.add(card);
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
            if (propertyDecks.get(group).size() >= PropertyCard.propertySetCounts[group]) {
                ++sets;
            }
        }
        return sets >= 3;
    }

    public boolean isDoubleRentBuff() {
        return doubleRentBuff;
    }

    public void setDoubleRentBuff(boolean buff) {
        doubleRentBuff = buff;
    }

    public void startTurn() {
        game.schedule(this::endTurn, 1000 * game.getTurnWait());
        game.execute(new DeleteMessage(tgid, stateMessageId));
        game.execute(new DeleteMessage(tgid, globalStateMessageId));
        stateMessageId = 0; // make it resend the self state message every turn
        globalStateMessageId = 0; // make it resend the self state message every turn
        actionCount = 0;
        setDoubleRentBuff(false);
        // tell the player that its their turn now
        SendMessage send = new SendMessage(game.getGid(),
                String.format(game.getTranslation().YOUR_TURN_ANNOUNCEMENT(), tgid, getName(), game.getTurnWait()));
        send.parseMode(ParseMode.HTML).disableWebPagePreview(true);
        game.execute(send);
        promptForCard();
    }

    public void promptForCard() {
        sendState();
        // before we actually prompt for a card, check for win condition
        if (checkWinCondition()) {
            endTurn(); // the game will take care of the announcement
            return;
        }
        if (actionCount < 3) {
            boolean hasWildcardsOrBuildings = propertyDecks.values().stream().anyMatch(x -> x.stream().anyMatch(xx -> xx instanceof WildcardPropertyCard || xx instanceof BuildingActionCard));
            // do prompt
            int size = hand.size() + (hasWildcardsOrBuildings ? 2 : 1);
            InlineKeyboardButton[][] buttons = new InlineKeyboardButton[size][1];
            int nonce = game.nextNonce();
            for (int i = 0; i < hand.size(); i++) {
                buttons[i][0] = new InlineKeyboardButton(hand.get(i).getCardTitle()).callbackData(nonce + ":play_card:" + i);
            }
            if (actionCount > -1) {
                buttons[hand.size()][0] = new InlineKeyboardButton(translation.PASS()).callbackData(nonce + ":end_turn");
            }
            if (hasWildcardsOrBuildings) {
                buttons[hand.size() + 1][0] = new InlineKeyboardButton(translation.MANAGE_CARD_MENU()).callbackData(nonce + ":wildcard_menu");
            }
            String msg = translation.CHOOSE_AN_ACTION(3 - actionCount);
            if (messageId == 0) {
                SendMessage send = new SendMessage(tgid, msg);
                send.replyMarkup(new InlineKeyboardMarkup(buttons));
                game.execute(send, new Callback<SendMessage, SendResponse>() {
                    @Override
                    public void onResponse(SendMessage request, SendResponse response) {
                        if (!response.isOk()) {
                            System.out.println(response.description());
                        }
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
        StringBuilder state = new StringBuilder(translation.CARDS_IN_HAND());
        state.append(handCount()).append("\n");
        for (Card card : hand) {
            state.append("- ").append(card.getCardTitle()).append("\n");
        }
        int total = currencyDeck.stream().mapToInt(Card::currencyValue).sum();
        state.append(translation.SELF_CURRENCY_DECK(currencyDeck.size(), total));
        for (Card card : currencyDeck) {
            state.append("$ ").append(card.currencyValue()).append("M, ");
        }
        state.setLength(state.length() - 2);

        state.append("\n").append(translation.PROPERTIES()).append("\n");
        for (Integer group : propertyDecks.keySet()) {
            List<Card> props = propertyDecks.get(group);
            if (props.isEmpty()) {
                continue;
            }
            state.append(translation.PROPERTY_GROUP(group)).append(" ").append(props.size()).append("/")
                    .append(PropertyCard.propertySetCounts[group]).append(" ($ ")
                    .append(PropertyCard.getRent(group, props))
                    .append("M) ").append(": ");
            for (Card prop : props) {
                state.append("- ").append(prop.getCardTitle()).append(", ");
            }
            state.setLength(state.length() - 2);
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

    public void promptSayNo(String msg, Runnable actionIfApproved, Runnable actionIfObjected) {
        int nonce = game.nextNonce();
        SendMessage send = new SendMessage(tgid, msg);
        long sayNos = hand.stream().filter(x -> x instanceof JustSayNoCard).count();
        InlineKeyboardButton[][] buttons = new InlineKeyboardButton[1 + (sayNos > 0 ? 1 : 0)][1];
        if (sayNos > 0) {
            buttons[0][0] = new InlineKeyboardButton(translation.JUST_SAY_NO_BTN(sayNos)).callbackData(nonce + ":say_no:y");
            buttons[1][0] = new InlineKeyboardButton(translation.NO()).callbackData(nonce + ":say_no:n");
        } else {
            buttons[0][0] = new InlineKeyboardButton(translation.NO()).callbackData(nonce + ":say_no:n");
        }
        send.replyMarkup(new InlineKeyboardMarkup(buttons));
        savedActionIfNotObjected = () -> {
            actionIfApproved.run();
            sendState();
        };
        savedActionIfObjected = actionIfObjected;
        game.execute(send, new Callback<SendMessage, SendResponse>() {
            @Override
            public void onResponse(SendMessage request, SendResponse response) {
                if (future != null) {
                    future.cancel(true);
                }
                future = executor.schedule(() -> {
                    sayNoCallback(new String[]{"say_no", "n"}, "", response.message().messageId());
                }, game.getObjectionWait(), TimeUnit.SECONDS);
            }

            @Override
            public void onFailure(SendMessage request, IOException e) {

            }
        });
    }

    public void sendGlobalState(String s) {
        if (globalStateMessageId == 0) {
            SendMessage send = new SendMessage(tgid, s).parseMode(ParseMode.HTML);
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

    public void collectRent(int value, int group, GamePlayer collector) {
        paymentMessage = translation.PAYMENT_COLLECTION_MESSAGE_SAY_NO(group, collector.getName(), value);
        promptSayNo(paymentMessage, () -> realCollectRent(value, group, collector), () -> {
            // tell the sender its objected
            game.log("Objection!");
//            SendMessage send = new SendMessage(tgid, "You refused to pay.");
//            game.execute(send);
            if (game.confirmPayment(null, tgid)) {
                confirmPayment();
            }
        });
    }

    private void realCollectRent(int value, int group, GamePlayer collector) {
        // we first check if the player's currency deck can cover the rent
        paymentValue = value;
        paymentSelectedIndices.clear();
        paymentSelectedPropertyIndices.clear();
        paymentMessage = translation.PAYMENT_COLLECTION_MESSAGE(group, collector.getName(), value, game.getPaymentWait());
        // the currency deck can cover this
        SendMessage send = new SendMessage(tgid, paymentMessage);
        List<InlineKeyboardButton[]> buttons = generatePaymentButtons();
        send.replyMarkup(new InlineKeyboardMarkup(buttons.toArray(new InlineKeyboardButton[0][0])));
        game.execute(send, new Callback<SendMessage, SendResponse>() {
            @Override
            public void onResponse(SendMessage request, SendResponse response) {
                paymentMessageId = response.message().messageId();
            }

            @Override
            public void onFailure(SendMessage request, IOException e) {
                e.printStackTrace();
            }
        });
        // if not paid in time, random pay
        if (future != null) {
            future.cancel(true);
        }
        game.logf("scheduled autopay task for %s", tgid);
        future = executor.schedule(() -> {
            game.logf("firing autopay task for %s", tgid);
            paymentSelectedIndices.clear();
            paymentSelectedPropertyIndices.clear();
            int paid = 0;
            for (int i = 0; i < currencyCount(); i++) {
                paid += currencyDeck.get(i).currencyValue();
                paymentSelectedIndices.add(i);
                if (paid >= paymentValue) {
                    break;
                }
            }
            int _k = 0;
            if (paid < paymentValue) {
                for (Integer grp : propertyDecks.keySet()) {
                    for (Card card : propertyDecks.get(grp)) {
                        if (card.currencyValue() > 0) {
                            paid += card.currencyValue();
                            paymentSelectedPropertyIndices.add(_k++);
                            if (paid >= paymentValue) {
                                break;
                            }
                        }
                    }
                    if (paid >= paymentValue) {
                        break;
                    }
                }
            }
            System.out.println(getPaymentCurrencyCards());
            if (game.confirmPayment(getPaymentCurrencyCards(), tgid)) {
                confirmPayment();
            }
        }, game.getPaymentWait(), TimeUnit.SECONDS);
    }

    private List<InlineKeyboardButton[]> generatePaymentButtons() {
        List<InlineKeyboardButton[]> buttons = new ArrayList<>();
        int nonce = game.nextNonce();
        int total = 0;
        for (int i = 0; i < currencyDeck.size(); i++) {
            boolean contains = paymentSelectedIndices.contains(i);
            buttons.add(new InlineKeyboardButton[]{new InlineKeyboardButton((contains ? " [x]" : "") + "$ " + currencyDeck.get(i).currencyValue() + "M").callbackData(nonce + ":pay_choose:" + i)});
            if (contains) {
                total += currencyDeck.get(i).currencyValue();
            }
        }
        int k = 0;
        for (Integer grp : propertyDecks.keySet()) {
            List<Card> get = propertyDecks.get(grp);
            for (int i = 0; i < get.size(); i++) {
                Card card = get.get(i);
                if (card.currencyValue() > 0) {
                    ++k;
                    boolean contains = paymentSelectedPropertyIndices.contains(k);
                    buttons.add(new InlineKeyboardButton[]{new InlineKeyboardButton((contains ? " [x]" : "") + card.getCardTitle() + " [$ " + card.currencyValue() + "M]").callbackData(nonce + ":pay_choose:p:" + (k))});
                    if (contains) {
                        total += card.currencyValue();
                    }
                }
            }
        }
        buttons.add(new InlineKeyboardButton[]{new InlineKeyboardButton(translation.PAY(total)).callbackData(nonce + ":pay_confirm")});
        return buttons;
    }

    public void wildcardMenuCallback(String[] args, String id) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery(id);
        int nonce = game.nextNonce();
        if (args.length == 1) {
            // root menu, select card to manage
            addMove(); // there's no real move yet
            List<InlineKeyboardButton[]> buttons = new ArrayList<>();
            for (Integer group : propertyDecks.keySet()) {
                List<Card> get = propertyDecks.get(group);
                for (int i = 0; i < get.size(); i++) {
                    Card card = get.get(i);
                    if (card instanceof WildcardPropertyCard) {
                        buttons.add(new InlineKeyboardButton[]{new InlineKeyboardButton(card.getCardTitle())
                                .callbackData(nonce + ":wildcard_menu:" + group + ":" + i)});
                    }
                }
            }
            buttons.add(new InlineKeyboardButton[]{new InlineKeyboardButton(translation.CANCEL()).callbackData(nonce + ":wildcard_menu:cancel")});
            EditMessageText edit = new EditMessageText(tgid, messageId, translation.CHOOSE_CARD_TO_MANAGE());
            edit.replyMarkup(new InlineKeyboardMarkup(buttons.toArray(new InlineKeyboardButton[0][0])));
            game.execute(edit);
        }
        if (args.length == 2) {
            // cancel
            promptForCard();
        }
        if (args.length == 3) {
            // card chosen so manage card
            int group = Integer.parseInt(args[1]);
            int index = Integer.parseInt(args[2]);
            WildcardPropertyCard card = (WildcardPropertyCard) propertyDecks.get(group).get(index);
            int[] groups = card.getGroups();
            InlineKeyboardButton[][] buttons = new InlineKeyboardButton[groups.length + 1][1];
            for (int i = 0; i < groups.length; i++) {
                int _group = groups[i];
                int count = propertyDecks.getOrDefault(_group, new ArrayList<>()).size();
                int total = PropertyCard.propertySetCounts[_group];
                buttons[i][0] = new InlineKeyboardButton(translation.PROPERTY_GROUP(_group) + " (" + count + "/" + total + ")")
                        .callbackData(nonce + ":wildcard_menu:" + group + ":" + index + ":" + _group);
            }
            buttons[groups.length][0] = new InlineKeyboardButton(translation.CANCEL()).callbackData(nonce + ":wildcard_menu:cancel");
            EditMessageText edit = new EditMessageText(tgid, messageId, translation.CHOOSE_RELOCATE());
            edit.replyMarkup(new InlineKeyboardMarkup(buttons));
            game.execute(edit);
        }
        if (args.length == 4) {
            // move and deduct action
            int group = Integer.parseInt(args[1]);
            int index = Integer.parseInt(args[2]);
            int newGroup = Integer.parseInt(args[3]);
            WildcardPropertyCard card = (WildcardPropertyCard) propertyDecks.get(group).get(index);
            if (addProperty(card, newGroup)) {
                // success, remove from old group
                propertyDecks.get(group).remove(card);
                card.setGroup(group);
                promptForCard();
            } else {
                answer.showAlert(true);
                answer.text(translation.GROUP_FULL());
                promptForCard();
            }
        }
        game.execute(answer);
    }

    public void payCallback(String[] args, String id) {
        switch (args[0]) {
            case "pay_choose":
                if (args[1].equals("p")) {
                    int index = Integer.parseInt(args[2]);
                    if (!paymentSelectedPropertyIndices.add(index)) {
                        paymentSelectedPropertyIndices.remove(index);
                    }
                } else {
                    int index = Integer.parseInt(args[1]);
                    if (!paymentSelectedIndices.add(index)) {
                        paymentSelectedIndices.remove(index);
                    }
                }
                EditMessageText edit = new EditMessageText(tgid, paymentMessageId, paymentMessage);
                List<InlineKeyboardButton[]> buttons = generatePaymentButtons();
                edit.replyMarkup(new InlineKeyboardMarkup(buttons.toArray(new InlineKeyboardButton[0][0])));
                game.execute(edit);
                break;
            case "pay_confirm":
                List<Card> payment = getPaymentCurrencyCards();
                int total = payment.stream().mapToInt(Card::currencyValue).sum();
                if (total < paymentValue && payment.size() < currencyCount()) {
                    // not enough and didnt do their best
                    AnswerCallbackQuery answer = new AnswerCallbackQuery(id);
                    answer.text(translation.PAYMENT_TOO_LOW());
                    answer.showAlert(true);
                    game.execute(answer);
                    edit = new EditMessageText(tgid, paymentMessageId, paymentMessage);
                    buttons = generatePaymentButtons();
                    edit.replyMarkup(new InlineKeyboardMarkup(buttons.toArray(new InlineKeyboardButton[0][0])));
                    game.execute(edit);
                    return;
                }
                // enough amount so confirm payment
                if (game.confirmPayment(payment, tgid)) {
                    confirmPayment();
                    if (future != null) {
                        future.cancel(true);
                    }
                }
                break;
            case "pay_reject":
                // just say no lol
                int sayNos = hand.stream().mapToInt(x -> x instanceof JustSayNoCard ? 1 : 0).sum();
                if (sayNos > 0) {
                    if (game.confirmPayment(null, tgid)) {
                        confirmPayment();
                        for (Card card : hand) {
                            if (card instanceof JustSayNoCard) {
                                removeHand(card);
                                game.addToUsedDeck(card);
                                break;
                            }
                        }
                    }
                    if (future != null) {
                        future.cancel(true);
                    }
                }
                break;
        }
    }

    public void sayNoCallback(String[] args, String id, int mid) {
        if (future != null) {
            future.cancel(true);
        }
        switch (args[1]) {
            case "y":
                // say no
                boolean removed = false;
                for (Card card : hand) {
                    if (card instanceof JustSayNoCard) {
                        hand.remove(card);
                        removed = true;
                        break;
                    }
                }
                System.out.println(removed);
                if (removed) {
                    // did say no, notify sender
                    savedActionIfObjected.run();
                    game.execute(new EditMessageText(tgid, mid, translation.SAID_NO()));
                    break;
                }
                // pass thru if not removed
            case "n":
                savedActionIfNotObjected.run();
                game.execute(new EditMessageText(tgid, mid, translation.SAID_YES()));
                break;
        }
        if (id.length() > 0) {
            game.execute(new AnswerCallbackQuery(id));
        }
    }

    public List<Card> getPaymentCurrencyCards() {
        List<Card> payment = new ArrayList<>();
        for (Integer index : paymentSelectedIndices) {
            payment.add(currencyDeck.get(index));
        }
        int k = -1;
        for (Integer grp : propertyDecks.keySet()) {
            List<Card> get = propertyDecks.get(grp);
            for (int i = 0; i < get.size(); i++) {
                Card card = get.get(i);
                if (card.currencyValue() > 0) {
                    k++;
                    if (paymentSelectedPropertyIndices.contains(k)) {
                        payment.add(card);
                    }
                }
            }
        }
        return payment;
    }

    public void confirmPayment() {
        // deduct the currencies
        List<Card> payment = getPaymentCurrencyCards();
        currencyDeck.removeAll(payment);
        propertyDecks.values().forEach(x -> x.removeAll(payment));
//        if (paymentMessageId != 0) {
//            game.execute(new DeleteMessage(tgid, paymentMessageId));
//        }
        if (payment.size() > 0) {
            String paymentStr = payment.stream().map(x -> !(x instanceof PropertyCard) ? "$ " + x.currencyValue() + "M" : x.getCardTitle()).collect(Collectors.joining(", "));
            EditMessageText edit = new EditMessageText(tgid, paymentMessageId, translation.PAYMENT_THX() + paymentStr);
            game.execute(edit);
        }
        paymentMessageId = 0;
        sendState();
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
            int nonce = game.nextNonce();
            for (int i = 0; i < hand.size(); i++) {
                buttons[i][0] = new InlineKeyboardButton(hand.get(i).getCardTitle()).callbackData(nonce + ":dispose_card:" + i);
            }
            String msg = translation.DISPOSE_CARD(handCount() - 7);
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
                disposeMessageId = 0;
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

    public int getGroupRent(int group) {
        return PropertyCard.getRent(group, propertyDecks.getOrDefault(group, new ArrayList<>()));
    }
}
