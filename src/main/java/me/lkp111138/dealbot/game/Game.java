package me.lkp111138.dealbot.game;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.EmptyCallback;
import me.lkp111138.dealbot.Main;
import me.lkp111138.dealbot.game.card.*;
import me.lkp111138.dealbot.game.card.action.GoPassActionCard;
import me.lkp111138.dealbot.game.card.state.*;
import me.lkp111138.dealbot.game.exception.ConcurrentGameException;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Represents a Monopoly Deal game
 */
public class Game {
    private static Map<Long, Game> gidGames = new HashMap<>();
    private static Map<Integer, Game> uidGames = new HashMap<>();

    // operational fields
    private int id;
    private long gid;
    private DealBot bot;

    // game params
    private int playTime;
    private int objectionTime;
    private int payTime;
    private String lang;
    private boolean protestMode;

    // game status
    private long startTime;
    private boolean started = false;
    private List<Card> cards = new ArrayList<>();
    private Map<Integer, Card> idCards = new HashMap<>();
    private List<Player> players = new ArrayList<>();
    private int currentPlayerIndex = -1;
    private Player currentPlayer;
    private int deckPointer = 0;

    // current turn status
    private int actionCount = -1;
    private long turnRestartTime;
    private int turnElapsedTime;
    private int messageId;
    private Card currentCard;
    private boolean paused = false;
    private CardArgumentRequest activeRequest;

    // payment request status
    private int confirmedCount = 0;
    private int requiredPaymentCount = 0;
    private Set<Integer> confirmedPlayers = new HashSet<>();
    private int paymentAmount = 0;
    private String paymentRequestMessage;
    private Map<Integer, Set<Integer>> paymentCards = new HashMap<>();

    // broadcast fields
    private ScheduledExecutorService broadcastExecutor = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture broadcastFuture;
    private String broadcastMessage = "";

    // scheduler fields
    private ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture scheduledFuture;

    /**
     * Constructs the game in preparation to start it
     * @see #startGame()
     * @param bot The DealBot instance
     * @param gid The group ID that attempted to start the game
     * @param message The message that triggered the creation
     * @throws ConcurrentGameException when a game for the group already exists
     * @throws SQLException when the database connection returned an error
     */
    public Game(DealBot bot, long gid, Message message) throws ConcurrentGameException, SQLException {
        // Check for duplicated games
        if (gidGames.containsKey(gid)) {
            throw new ConcurrentGameException(gidGames.get(gid).getId());
        }
        Connection conn = Main.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO games (gid) values (?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, gid);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    this.id = keys.getInt(1);
                    this.gid = gid;
                    this.bot = bot;
                }
            }
        }
        try (PreparedStatement stmt = conn.prepareStatement("SELECT turn_wait_time, say_no_time, pay_time, lang, protest_mode from `groups` WHERE gid=?")) {
            stmt.setLong(1, gid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                playTime = rs.getInt(1);
                objectionTime = rs.getInt(2);
                payTime = rs.getInt(3);
                lang = rs.getString(4);
                protestMode = rs.getBoolean(5);
            } else {
                playTime = 90;
                objectionTime = 15;
                payTime = 30;
                lang = "en";
                protestMode = true;
            }
        }
        gidGames.put(gid, this);
        startTime = System.currentTimeMillis() + 18000;
        schedule(this::joinReminder, 3000);
        broadcast(bot.translate(this.lang, "game.created", message.from().id(), message.from().firstName(), 18, this.id), true);
        conn.close();
    }

    /**
     * Queries the game running in a group by ID
     * @param gid the group ID to query
     * @return The running game if any or null
     */
    public static Game byGroup(long gid) {
        return gidGames.get(gid);
    }

    /**
     * Queries a game running by user ID
     * @param uid the group ID to query
     * @return The running game if any or null
     */
    public static Game byUser(int uid) {
        return uidGames.get(uid);
    }

    private void joinReminder() {
        long millis = startTime - System.currentTimeMillis();
        int secs = (int) ((millis + 500) / 1000);
        System.out.println(millis);
        switch (secs) {
            case 30:
                schedule(this::joinReminder, (millis - 1000) % 15000 + 1000);
                break;
            case 15:
                schedule(this::startGame, millis);
                break;
            default:
                schedule(this::joinReminder, (millis - 1000) % 30000 + 1000);
        }
        broadcast(bot.translate(this.lang, "game.join_reminder", secs), true);
    }

    /**
     * Adds a player to the game by their request
     * @param message The message that requested the join
     */
    public void addPlayer(Message message) {
        if (started || players.stream().anyMatch(p -> p.getUserId() == message.from().id())) {
            // No joining if the player already joined or if is started
            return;
        }
        if (uidGames.containsKey(message.from().id())) {
            // No joining if the user is already in game
            return;
        }
        SendMessage send = new SendMessage(message.from().id(), bot.translate(message.from().id(), "game.joined_successfully", message.chat().title(), id));
        bot.execute(send, new Callback<SendMessage, SendResponse>() {
            @Override
            public void onResponse(SendMessage request, SendResponse response) {
                if (response.isOk()) {
                    players.add(new Player(Game.this, message.from()));
                    uidGames.put(message.from().id(), Game.this);
                    broadcast(bot.translate(lang, "game.joined_announcement", message.from().id(), message.from().firstName(), players.size()));
                } else {
                    requestStart();
                }
            }

            @Override
            public void onFailure(SendMessage request, IOException e) {
            }

            private void requestStart() {
                // ask the player to start the bot
                InlineKeyboardButton button = new InlineKeyboardButton(bot.translate(lang, "misc.start_me"));
                button.url(String.format("https://t.me/%s?start=join_%s", Main.getConfig("bot.username"), gid));
                InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(new InlineKeyboardButton[]{button});
                broadcast(bot.translate(lang, "misc.please_start_me"), keyboard, true);
            }
        });
    }

    public void removePlayer(Message message) {
        if (started || players.stream().noneMatch(p -> p.getUserId() == message.from().id())) {
            // disallow fleeing if game has started or player didn't join
            return;
        }
        for (Iterator<Player> iterator = players.iterator(); iterator.hasNext(); ) {
            Player player = iterator.next();
            if (player.getUserId() == message.from().id()) {
                iterator.remove();
                uidGames.remove(player.getUserId());
                break;
            }
        }
        broadcast(bot.translate(lang, "game.player_fled", message.from().id(), message.from().firstName(), players.size()));
    }

    /**
     * Starts the game
     */
    public void startGame() {
        started = true;
        broadcast(bot.translate(lang, "game.starting"), false);
        generateCards();
        Collections.shuffle(players);
        // 5 cards per player
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            for (int j = 0; j < 5; j++) {
                cards.get(i * 5 + j).setState(new CardStateInPlayerHand(player));
            }
        }
        deckPointer += 5 * players.size();
        startTurn();
    }

    private void generateCards() {
        // currency cards
        for (int i = 0; i < 6; i++) {
            cards.add(new CurrencyCard(1 + i, 1));
        }
        for (int i = 0; i < 5; i++) {
            cards.add(new CurrencyCard(7 + i, 2));
        }
        for (int i = 0; i < 3; i++) {
            cards.add(new CurrencyCard(12 + i, 3));
        }
        for (int i = 0; i < 3; i++) {
            cards.add(new CurrencyCard(15 + i, 4));
        }
        cards.add(new CurrencyCard(18, 5));
        cards.add(new CurrencyCard(19, 5));
        cards.add(new CurrencyCard(20, 10));

        // named property cards
        cards.add(new NamedPropertyCard(21, 0, 0, 1));
        cards.add(new NamedPropertyCard(22, 1, 0, 1));

        cards.add(new NamedPropertyCard(23, 2, 1, 1));
        cards.add(new NamedPropertyCard(24, 3, 1, 1));
        cards.add(new NamedPropertyCard(25, 4, 1, 1));

        cards.add(new NamedPropertyCard(26, 5, 2, 2));
        cards.add(new NamedPropertyCard(27, 6, 2, 2));
        cards.add(new NamedPropertyCard(28, 7, 2, 2));

        cards.add(new NamedPropertyCard(29, 8, 3, 2));
        cards.add(new NamedPropertyCard(30, 9, 3, 2));
        cards.add(new NamedPropertyCard(31, 10, 3, 2));

        cards.add(new NamedPropertyCard(32, 11, 4, 3));
        cards.add(new NamedPropertyCard(33, 12, 4, 3));
        cards.add(new NamedPropertyCard(34, 13, 4, 3));

        cards.add(new NamedPropertyCard(35, 14, 5, 3));
        cards.add(new NamedPropertyCard(36, 15, 5, 3));
        cards.add(new NamedPropertyCard(37, 16, 5, 3));

        cards.add(new NamedPropertyCard(38, 17, 6, 4));
        cards.add(new NamedPropertyCard(39, 18, 6, 4));
        cards.add(new NamedPropertyCard(40, 19, 6, 4));

        cards.add(new NamedPropertyCard(41, 20, 7, 4));
        cards.add(new NamedPropertyCard(42, 21, 7, 4));

        cards.add(new NamedPropertyCard(43, 22, 8, 2));
        cards.add(new NamedPropertyCard(44, 23, 8, 2));
        cards.add(new NamedPropertyCard(45, 24, 8, 2));
        cards.add(new NamedPropertyCard(46, 25, 8, 2));

        cards.add(new NamedPropertyCard(47, 26, 9, 2));
        cards.add(new NamedPropertyCard(48, 27, 9, 2));

        // two colour wildcard properties
        cards.add(new TwoColourWildcardPropertyCard(49, 4, 5, 3));
        cards.add(new TwoColourWildcardPropertyCard(50, 4, 5, 3));
        cards.add(new TwoColourWildcardPropertyCard(51, 6, 7, 4));
        cards.add(new TwoColourWildcardPropertyCard(52, 0, 1, 1));
        cards.add(new TwoColourWildcardPropertyCard(53, 2, 3, 2));
        cards.add(new TwoColourWildcardPropertyCard(54, 2, 3, 2));
        cards.add(new TwoColourWildcardPropertyCard(55, 6, 8, 4));
        cards.add(new TwoColourWildcardPropertyCard(56, 1, 8, 4));
        cards.add(new TwoColourWildcardPropertyCard(57, 8, 9, 2));
        cards.add(new TwoColourWildcardPropertyCard(58, 8, 9, 2));

        cards.add(new RainbowWildcardPropertyCard(59));
        cards.add(new RainbowWildcardPropertyCard(60));

        // TODO action cards
        for (int i = 0; i < 10; i++) {
            cards.add(new GoPassActionCard(61 + i));
        }
        cards.add(new JustSayNoCard(71));
        cards.add(new JustSayNoCard(72));

        Collections.shuffle(cards);

        for (Card card : cards) {
            idCards.put(card.getId(), card);
        }
    }

    public void extend(int secs) {
        if (isStarted()) {
            // What are we extending is the game is started?
            return;
        }
        cancelFuture();
        startTime += 1000 * secs;
        long millis = startTime - System.currentTimeMillis();
        if (millis > 30000) {
            schedule(this::joinReminder, (millis - 1000) % 30000 + 1000);
        } else if (millis > 15000) {
            schedule(this::joinReminder, (millis - 1000) % 15000 + 1000);
        } else {
            schedule(this::startGame, millis);
        }
        broadcast(bot.translate(this.lang, "game.join_extended", secs, millis / 1000));
    }

    /**
     * Starts a new turn
     */
    private void startTurn() {
        // reset turn params
        actionCount = 0;
        ++currentPlayerIndex;
        currentPlayerIndex %= players.size();
        messageId = 0;
        currentPlayer = players.get(currentPlayerIndex);
        turnRestartTime = System.currentTimeMillis();
        paused = false;
        activeRequest = null;

        // draw cards
        int cardsToDraw = 2;
        if (currentPlayer.getHandCount() == 0) {
            cardsToDraw = 5;
        }
        StringBuilder msg = new StringBuilder(bot.translate(currentPlayer.getUserId(), "game.have_drawn"));
        for (int i = 0; i < cardsToDraw; i++) {
            Card drawn = draw();
            if (drawn == null) {
                // we just can't draw a card anymore
                // TODO state the fact
                System.out.println("deck empty!");
                break;
            } else {
                msg.append("\n- ").append(bot.translate(currentPlayer.getUserId(), drawn.getNameKey()));
            }
        }
        if (msg.indexOf("\n") > 0) {
            bot.execute(new SendMessage(currentPlayer.getUserId(), msg.toString()));
        }

        promptForCard(currentPlayer);
        schedule(this::endTurn, playTime * 1000);
    }

    private void promptForCard(Player player) {
        if (actionCount >= 3) {
            // end turn
            endTurn();
            return;
        }
        currentCard = null;
        // prompt to play cards
        int millis = (int) (playTime * 1000 - turnElapsedTime - (System.currentTimeMillis() - turnRestartTime));
        String msg = bot.translate(player.getUserId(), "game.play_prompt", 3 - actionCount, millis / 1000);
        InlineKeyboardButton[][] buttons = cards.stream()
                .filter(c -> c.getState().equals(new CardStateInPlayerHand(player)))
                .map(card -> new InlineKeyboardButton[]{
                        new InlineKeyboardButton(bot.translate(player.getUserId(), card.getNameKey()))
                                .callbackData("card:" + card.getId())
                })
                .toArray(i -> new InlineKeyboardButton[i + 1][1]);
        buttons[buttons.length - 1][0] = new InlineKeyboardButton(bot.translate(currentPlayer.getUserId(), "game.end_turn"))
                .callbackData("end");
        if (messageId == 0) {
            bot.execute(new SendMessage(player.getUserId(), msg).replyMarkup(new InlineKeyboardMarkup(buttons)), new Callback<SendMessage, SendResponse>() {
                @Override
                public void onResponse(SendMessage request, SendResponse response) {
                    if (response.isOk()) {
                        messageId = response.message().messageId();
                    }
                }

                @Override
                public void onFailure(SendMessage request, IOException e) {

                }
            });
        } else {
            bot.execute(new EditMessageText(player.getUserId(), messageId, msg).replyMarkup(new InlineKeyboardMarkup(buttons)));
        }
    }

    private void endTurn() {
        // tell them their turn has ended
        System.out.println(bot.execute(new EditMessageText(currentPlayer.getUserId(), messageId, bot.translate(currentPlayer.getUserId(), "game.turn_ended"))));

        startTurn();
    }

    public boolean callback(CallbackQuery query) {
        int from = query.from().id();
        String[] payload = query.data().split(":");
        System.out.println(query.data());
        switch (payload[0]) {
            case "card":
                // attempt to play a card
                if (from != currentPlayer.getUserId()) {
                    // impersonation?
                    return true;
                }
                ++actionCount;
                Card card = idCards.get(Integer.parseInt(payload[1]));
                if (card.getState().equals(new CardStateInPlayerHand(currentPlayer))) {
                    String[] args = new String[payload.length - 2];
                    System.arraycopy(payload, 2, args, 0, args.length);
                    currentCard = card;
                    CardArgumentRequest req = card.execute(bot, currentPlayer, args);
                    processCardArgReq(req);
                }
                System.out.println(card.getState());
                return true;
            case "arg":
                // specify args for a card
                if (from != currentPlayer.getUserId()) {
                    // impersonation?
                    return true;
                }
                if (currentCard != null) {
                    String[] args = new String[payload.length - 1];
                    System.arraycopy(payload, 1, args, 0, args.length);
                    CardArgumentRequest req = currentCard.execute(bot, currentPlayer, args);
                    processCardArgReq(req);
                }
                return true;
            case "end":
                // end turn
                if (from != currentPlayer.getUserId()) {
                    // impersonation?
                    return true;
                }
                endTurn();
                return true;
            case "cancel":
                // cancel and return to main menu
                if (from != currentPlayer.getUserId()) {
                    // impersonation?
                    return true;
                }
                --actionCount;
                promptForCard(currentPlayer);
                return true;
            case "obj":
            case "payobj":
                // normal objection
                boolean objected = from != currentPlayer.getUserId();
                switch (payload[1]) {
                    case "yes":
                        // find the player
                        Player player = null;
                        for (Player player1 : players) {
                            if (player1.getUserId() == from) {
                                player = player1;
                                break;
                            }
                        }
                        if (player == null) {
                            return true;
                        }
                        CardState inHand = new CardStateInPlayerHand(player);
                        List<Card> objections = cards.stream().filter(c -> c.getState().equals(inHand))
                                .filter(c -> c instanceof JustSayNoCard).collect(Collectors.toList());
                        if (objections.size() > 0) {
                            objected = !objected;
                            objections.get(0).setState(new CardStateInUsedDeck());
                            int objector = Integer.parseInt(payload[2]);
                            if (objected) {
                                promptForObjection("obj", currentPlayer, bot.translate(currentPlayer.getUserId(), "game.was_objected", players.get(objector).getName()));
                            } else {
                                promptForObjection("obj", activeRequest.getTarget(), bot.translate(activeRequest.getTarget().getUserId(), activeRequest.getMessage()));
                            }
                            break;
                        }
                        // intentional fallthrough
                    case "no":
                        objected = !objected;
                        if (payload[0].equals("obj")) {
                            if (!objected) {
                                activeRequest.getObjectionable().run();
                            }
                            resumeTurn();
                        } else {
                            if (!objected) {
                                collectPayment(players.indexOf(new Player(this, query.from())));
                            } else {
                                incrementConfirmPayments();
                            }
                        }
                        break;
                }
                return true;
            case "pay":
                // todo
                if (paymentAmount <= 0) {
                    return true;
                }
                int index = -1;
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getUserId() == from) {
                        index = i;
                        break;
                    }
                }
                if (payload[1].equals("confirm")) {
                    // confirm payment
                    confirmPayment(index);
                } else {
                    int id = Integer.parseInt(payload[1]);
                    Card selected = cards.get(id);
                    if (selected.getState().isOwnedBy(players.get(index)) && !(selected.getState() instanceof CardStateInPlayerHand)) {
                        Set<Integer> selectedCards = paymentCards.getOrDefault(index, new HashSet<>());
                        if (!selectedCards.add(id)) {
                            selectedCards.remove(id);
                        } else {
                            paymentCards.put(index, selectedCards);
                            updatePaymentButtons(index, query.message().messageId());
                        }
                    }
                }
                return true;
        }
        return false;
    }

    /**
     * Makes the current player draw a card
     * @return the card drawn if the player successfully drew a card, null otherwise
     */
    public Card draw() {
        if (deckPointer == cards.size()) {
            if (reshuffle() == 0) {
                return null;
            }
        }
        Card drawn = cards.get(deckPointer++);
        drawn.setState(new CardStateInPlayerHand(players.get(currentPlayerIndex)));
        return drawn;
    }

    private void processCardArgReq(CardArgumentRequest req) {
        if (req == null) {
            promptForCard(currentPlayer);
        } else {
            switch (req.getType()) {
                case UPDATE:
                    bot.execute(new EditMessageText(currentPlayer.getUserId(), messageId, req.getMessage()).replyMarkup(new InlineKeyboardMarkup(addCancelButton(req.getKeyboard()))));
                    break;
                case OBJECTION:
                    pauseTurn();
                    activeRequest = req;
                    // ask if target wants to object
                    promptForObjection("obj", activeRequest.getTarget(), activeRequest.getMessage());
                    break;

            }
        }
    }

    /**
     * Prompt a player for objection
     * @param prefix The prefix for the query
     * @param target the target player to prompt
     */
    private void promptForObjection(String prefix, Player target, String message) {
        int objectionCount = (int) cards.stream().filter(card -> card instanceof JustSayNoCard).filter(card -> card.getState().equals(new CardStateInPlayerHand(target))).count();
        InlineKeyboardButton[][] buttons;
        if (objectionCount > 0) {
            buttons = new InlineKeyboardButton[2][1];
            buttons[0][0] = new InlineKeyboardButton(bot.translate(target.getUserId(), "game.use_objection", objectionCount)).callbackData(prefix + ":yes:" + players.indexOf(target));
        } else {
            buttons = new InlineKeyboardButton[1][1];
        }
        buttons[buttons.length - 1][0] = new InlineKeyboardButton(bot.translate(target.getUserId(), "misc.nope")).callbackData(prefix + ":no:");
        bot.execute(new SendMessage(target.getUserId(), bot.translate(target.getUserId(), "%s {{game.say_no_prompt}}", message)).replyMarkup(new InlineKeyboardMarkup(buttons)));
    }

    /**
     * Recycle all used cards and resets the deck pointer accordingly.
     * @return the number of cards recycled
     */
    private int reshuffle() {
        List<Card> used = cards.stream().filter(c -> c.getState() instanceof CardStateInUsedDeck).collect(Collectors.toList());
        cards.removeAll(used);
        Collections.shuffle(used);
        used.forEach(c -> c.setState(new CardStateInMainDeck()));
        cards.addAll(used);
        deckPointer -= used.size();
        return used.size();
    }

    /**
     * Request a player or all players to pay a fee
     * @param playerIndex the index of the player in the game, -1 for all
     * @param amount the amount to collect
     */
    public void requestPayment(int playerIndex, int amount, int group) {
        confirmedCount = 0;
        confirmedPlayers.clear();
        paymentAmount = amount;
        if (playerIndex == -1) {
            requiredPaymentCount = players.size() - 1;
            // all
            for (int i = 0; i < players.size(); i++) {
                if (i == currentPlayerIndex) {
                    continue;
                }
                Player target = players.get(i);
                if (group < 10) {
                    paymentRequestMessage = bot.translate(target.getUserId(), "game.payment_requested", currentPlayer.getName(), amount, bot.translate(target.getUserId(), "game.property.colour.long." + group));
                    promptForObjection("payobj", target, paymentRequestMessage);
                }
            }
            return;
        }
        requiredPaymentCount = 1;
        Player target = players.get(playerIndex);
        if (group < 10) {
            paymentRequestMessage = bot.translate(target.getUserId(), "game.payment_requested", currentPlayer.getName(), amount, bot.translate(target.getUserId(), "game.property.colour.long." + group));
            promptForObjection("payobj", target, paymentRequestMessage);
        }
    }

    /**
     * Collects a fee from a player
     * @param playerIndex the index of the player in the game
     */
    private void collectPayment(int playerIndex) {
        updatePaymentButtons(playerIndex, 0);
    }

    private void updatePaymentButtons(int playerIndex, int messageId) {
        Player target = players.get(playerIndex);
        List<Card> paymentOptions = cards.stream().filter(c -> c.getState().equals(new CardStateInPlayerCurrency(target))).collect(Collectors.toList());
        cards.stream().filter(c -> {
            if (c.getState() instanceof CardStateInPlayerProperty) {
                return ((CardStateInPlayerProperty) c.getState()).getPlayer().equals(target);
            }
            return false;
        }).forEach(paymentOptions::add);
        InlineKeyboardButton[][] buttons = paymentOptions.stream().map(card -> new InlineKeyboardButton[]{
                new InlineKeyboardButton((paymentCards.getOrDefault(playerIndex, new HashSet<>()).contains(card.getId()) ? "✅ " : "") + bot.translate(target.getUserId(), card instanceof CurrencyCard ? card.getNameKey() : "[$ %sM] {{" + card.getNameKey() + "}}"))
                        .callbackData("pay:" + card.getId())
        }).toArray(i -> new InlineKeyboardButton[i + 1][1]);
        buttons[buttons.length - 1][0] = new InlineKeyboardButton(bot.translate(target.getUserId(), "game.pay", 0)).callbackData("pay:confirm");
        if (messageId == 0) {
            bot.execute(new SendMessage(target.getUserId(), paymentRequestMessage).replyMarkup(new InlineKeyboardMarkup(buttons)));
        } else {
            bot.execute(new EditMessageReplyMarkup(target.getUserId(), messageId).replyMarkup(new InlineKeyboardMarkup(buttons)));
        }
    }

    /**
     * Confirms a payment from a player
     * @param playerIndex the position of the player to confirm
     */
    private void confirmPayment(int playerIndex) {
        List<Card> cards = paymentCards.getOrDefault(playerIndex, new HashSet<>()).stream().map(this.cards::get).collect(Collectors.toList());
        // check ownership
        if (cards.stream().anyMatch(card -> !card.getState().isOwnedBy(players.get(playerIndex)))) {
            return;
        }
        // check value
        int total = cards.stream().mapToInt(Card::getCurrencyValue).sum();
        if (total < paymentAmount) {
            return;
        }
        // change ownership
        for (Card card : cards) {
            CardState state = card.getState();
            if (state instanceof CardStateInPlayerProperty) {
                card.setState(new CardStateInPlayerProperty(currentPlayer, ((CardStateInPlayerProperty) state).getColour()));
            }
            if (state instanceof CardStateInPlayerCurrency) {
                card.setState(new CardStateInPlayerCurrency(currentPlayer));
            }
        }
        incrementConfirmPayments();
    }
    
    private void incrementConfirmPayments() {
        ++confirmedCount;
        if (confirmedCount == requiredPaymentCount) {
            resumeTurn();
        }
    }

    private void pauseTurn() {
        if (paused) {
            return;
        }
        cancelFuture();
        turnElapsedTime += (int) (System.currentTimeMillis() - turnRestartTime);
        paused = true;
    }

    private void resumeTurn() {
        if (!paused) {
            return;
        }
        cancelFuture();
        schedule(this::endTurn, playTime * 1000 - turnElapsedTime);
        paused = false;
    }

    /**
     * Stops the game prematurely.
     */
    public void kill() {
        // TODO
    }

    private void broadcast(String message) {
        broadcast(message, null, false);
    }

    private void broadcast(String message, boolean flush) {
        broadcast(message, null, flush);
    }

    /**
     * Send a message about this game to the group.
     * @param message The message text to send.
     * @param keyboard The reply markup to attach, can be null.
     * @param flush If true, sends immediately without throttling
     */
    private void broadcast(String message, Keyboard keyboard, boolean flush) {
        if (broadcastFuture != null) {
            broadcastFuture.cancel(true);
        }
        if (keyboard != null || flush) {
            // send immediately along with the other messages no matter what
            System.out.println("flushing");
            SendMessage send = new SendMessage(this.gid, broadcastMessage + message)
                    .parseMode(ParseMode.HTML);
            if (keyboard != null) {
                send.replyMarkup(keyboard);
            }
            broadcastMessage = "";
            System.out.println("flush sending " + broadcastMessage + message);
            bot.execute(send, new EmptyCallback<>());
            System.out.println("flush sent");
        } else {
            // wait a second
            broadcastMessage += message + "\n\n";
            broadcastFuture = broadcastExecutor.schedule(() -> {
                SendMessage send = new SendMessage(this.gid, broadcastMessage);
                send.parseMode(ParseMode.HTML);
                broadcastMessage = "";
                bot.execute(send, new EmptyCallback<>());
            }, 1000, TimeUnit.MILLISECONDS);
        }
    }

    private InlineKeyboardButton[][] addCancelButton(InlineKeyboardButton[][] keyboard) {
        InlineKeyboardButton[][] buttons = new InlineKeyboardButton[keyboard.length + 1][1];
        System.arraycopy(keyboard, 0, buttons, 0, keyboard.length);
        buttons[keyboard.length][0] = new InlineKeyboardButton(bot.translate(currentPlayer.getUserId(), "misc.cancel")).callbackData("cancel");
        return buttons;
    }

    /**
     * Schedule a task to be ran in the future for this game
     * @param task The task to run
     * @param time The number of milliseconds to wait to run the task
     */
    private void schedule(Runnable task, long time) {
        System.out.println(time);
        cancelFuture();
        scheduledFuture = scheduledExecutor.schedule(task, time, TimeUnit.MILLISECONDS);
    }

    private void cancelFuture() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
    }

    public int getId() {
        return id;
    }

    public boolean isStarted() {
        return started;
    }

    List<Card> getCards() {
        return cards;
    }
}
