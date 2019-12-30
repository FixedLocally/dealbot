package me.lkp111138.dealbot.game;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.EmptyCallback;
import me.lkp111138.dealbot.Main;
import me.lkp111138.dealbot.game.card.*;
import me.lkp111138.dealbot.game.card.state.CardStateInMainDeck;
import me.lkp111138.dealbot.game.card.state.CardStateInPlayerHand;
import me.lkp111138.dealbot.game.card.state.CardStateInUsedDeck;
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
    private List<Player> players = new ArrayList<>();
    private int currentPlayerIndex = -1;
    private int deckPointer = 0;

    // current turn status
    private int actionCount = 0;
    private long turnRestartTime;
    private int turnElapsedTime;

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
        startTime = System.currentTimeMillis() + 120000;
        schedule(this::joinReminder, 30000);
        broadcast(bot.translate(this.lang, "game.created", message.from().id(), message.from().firstName(), 120, this.id), true);
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
        SendMessage send = new SendMessage(message.from().id(), bot.translate(message.from().id(), "game.joined_successfully", message.chat().title(), id));
        bot.execute(send, new Callback<SendMessage, SendResponse>() {
            @Override
            public void onResponse(SendMessage request, SendResponse response) {
                if (response.isOk()) {
                    players.add(new Player(Game.this, message.from().id()));
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
        Collections.shuffle(cards);
    }

    public void extend(int secs) {
        if (isStarted()) {
            // What are extending is the game is started?
            return;
        }
        cancelFuture();
        startTime += 1000 * secs;
        long millis = startTime - System.currentTimeMillis();
        if (millis > 30000) {
            schedule(this::joinReminder, (millis - 1000) % 30000 + 1000);
        } else if (millis > 15) {
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
        actionCount = 0;
        ++currentPlayerIndex;
        Player player = players.get(currentPlayerIndex);

        // draw cards
        int cardsToDraw = 2;
        if (player.getHandCount() == 0) {
            cardsToDraw = 3;
        }
        for (int i = 0; i < cardsToDraw; i++) {
            if (draw() == null) {
                // we just can't draw a card anymore
                // TODO state the fact
                break;
            }
        }

        promptForCard(player);
    }

    private void promptForCard(Player player) {
        // prompt to play cards
        int millis = playTime * 1000 - turnElapsedTime;
        String msg = bot.translate(player.getUserId(), "game.play_prompt", 3 - actionCount, millis / 1000);
        InlineKeyboardButton[][] buttons = cards.stream()
                .filter(c -> c.getState().equals(new CardStateInPlayerHand(player)))
                .map(card -> new InlineKeyboardButton[]{
                        new InlineKeyboardButton(bot.translate(player.getUserId(), card.getNameKey()))
                                .callbackData("card:" + card.getId())
                })
                .toArray(InlineKeyboardButton[][]::new);
        bot.execute(new SendMessage(player.getUserId(), msg).replyMarkup(new InlineKeyboardMarkup(buttons)));
        // TODO process callbacks
    }

    /**
     * Makes the current player draw a card
     * @return the card drawn if the player successfully drew a card, null otherwise
     */
    private Card draw() {
        if (deckPointer == cards.size()) {
            if (reshuffle() == 0) {
                return null;
            }
        }
        Card drawn = cards.get(deckPointer);
        drawn.setState(new CardStateInPlayerHand(players.get(currentPlayerIndex)));
        return drawn;
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
