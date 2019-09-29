package me.lkp111138.dealbot.game;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.Main;
import me.lkp111138.dealbot.game.cards.*;
import me.lkp111138.dealbot.game.cards.actions.*;
import me.lkp111138.dealbot.misc.EmptyCallback;
import me.lkp111138.dealbot.translation.Translation;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Game {
    private final GroupInfo groupInfo;
    private int turnWait;
    private int currentTurn = 0;
    private long startTime;
    private long gid;
    private boolean started = false;
    private List<User> players = new ArrayList<>();
    private List<GamePlayer> gamePlayers = new ArrayList<>();
    private ScheduledFuture future;
    private String lang;
    private Translation translation;
    private int id;
    private List<Card> mainDeck = new ArrayList<>();
    private List<Card> usedDeck = new ArrayList<>();
    private Set<Integer> usedNonces = new HashSet<>();
    private int nextNonce = 0;
    private long turnStartTime;
    private long turnTime;
    private boolean turnPaused = false;
    private String broadcastMessageCache = "";
    private ScheduledFuture sendMessageFuture;

    private boolean ended = false;

    private int paymentConfirmationCount;
    private Set<Integer> paidPlayers = new HashSet<>();

    private static Map<Long, Game> games = new HashMap<>();
    public static boolean maintMode = false; // true=disallow starting games
    private static TelegramBot bot;
    private static ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(2);
    private static Map<Integer, Game> uidGames = new HashMap<>();
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private static int[] remindSeconds = new int[]{15, 30, 60, 90, 120, 180};
    private Card currentCard = null;

    public static void init(TelegramBot _bot) {
        bot = _bot;
    }

    public static Game byGroup(long gid) {
        return games.get(gid);
    }

    public Game(Message msg, int wait, GroupInfo groupInfo) throws ConcurrentGameException {
        // LOGIC :EYES:
        long gid = msg.chat().id();
        if (games.containsKey(gid)) {
            throw new ConcurrentGameException(this);
        }
        this.gid = gid;
        this.turnWait = groupInfo.waitTime;
        this.groupInfo = groupInfo;
        this.lang = DealBot.lang(gid);
        this.translation = Translation.get(this.lang);
        if (maintMode) {
            // disallow starting games
            this.execute(new SendMessage(gid, this.translation.MAINT_MODE_NOTICE()).parseMode(ParseMode.HTML), new EmptyCallback<>());
            return;
        }
        // create game
        try (Connection conn = Main.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO games (gid) values (?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, gid);
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    id = generatedKeys.getInt(1);
                }
                else {
                    throw new SQLException("Creating game failed, no ID obtained.");
                }
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            this.execute(new SendMessage(msg.chat().id(), this.translation.ERROR() + e.getMessage()).replyToMessageId(msg.messageId()));
        }
        // notify queued users
        try (Connection conn = Main.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT tgid FROM next_game where gid=?");
            stmt.setLong(1, gid);
            ResultSet rs = stmt.executeQuery();
            String startMsg = this.translation.GAME_STARTING_IN(msg.chat().title());
            while (rs.next()) {
                SendMessage send = new SendMessage(rs.getInt(1), startMsg);
                this.execute(send);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            this.execute(new SendMessage(msg.chat().id(), this.translation.ERROR() + e.getMessage()).replyToMessageId(msg.messageId()));
        }
        this.execute(new SendMessage(gid, String.format(this.translation.GAME_START_ANNOUNCEMENT(), msg.from().id(), msg.from().firstName(), wait, id)).parseMode(ParseMode.HTML), new EmptyCallback<>());
        games.put(gid, this);
        addPlayer(msg);
        startTime = System.currentTimeMillis() + wait * 1000;
        int i;
        i = 0;
        while (wait > remindSeconds[i]) {
            ++i;
        }
//        this.log("scheduled remind task");
        schedule(this::remind, (wait - remindSeconds[--i]) * 1000);
        this.logf("Game created in %s [%d]", msg.chat().title(), msg.chat().id());
    }

    public static Game byUser(int tgid) {
        return uidGames.get(tgid);
    }

    public static RunInfo runInfo() {
        int total = games.size();
        int players = uidGames.size();
        int running = 0;
        for (Game game : games.values()) {
            if (game.started) {
                ++running;
            }
        }
        return new RunInfo(running, total, players, games);
    }

    public void addPlayer(Message msg) {
        // if a player is in a dead game, remove them
        User from = msg.from();
        Game g = uidGames.get(from.id());
        if (g != null && g.ended) {
            uidGames.remove(from.id());
        }
        // add to player list
        if (!started && !players.contains(from) && !uidGames.containsKey(from.id()) && playerCount() < 5) {
            // notify player
            // .replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{}
            SendMessage send = new SendMessage(msg.from().id(), String.format(this.translation.JOIN_SUCCESS(), msg.chat().title().replace("*", "\\*"), this.id)).parseMode(ParseMode.Markdown);
            if (msg.chat().username() != null) {
                send.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{new InlineKeyboardButton(this.translation.BACK_TO() + msg.chat().title()).url("https://t.me/" + msg.chat().username())}));
            }
            this.execute(send, new Callback<SendMessage, SendResponse>() {
                @Override
                public void onResponse(SendMessage request, SendResponse response) {
                    if (response.isOk()) {
                        // notify group
                        // we only actually add the player to the group if we can deliver the pm
                        if (playerCount() >= 5 || players.contains(msg.from())) {
                            // oops, you're the 6th player bye
                            return;
                        }
                        players.add(msg.from());
                        uidGames.put(msg.from().id(), Game.this);
                        int count = playerCount();
                        Game.this.execute(new SendMessage(msg.chat().id(), String.format(Game.this.translation.JOINED_ANNOUNCEMENT(), msg.from().id(), msg.from().firstName(), count)).parseMode(ParseMode.HTML), new EmptyCallback<>());
//                        Game.this.logf("%d / 4 players joined", count);
                        if (count == 5) {
                            // game full
                            start();
                        }
                    } else {
                        // for some reason we cant deliver the msg to the user, so we ask them to start me in the group
                        Game.this.execute(new SendMessage(msg.chat().id(), Game.this.translation.START_ME_FIRST())
                                .replyToMessageId(msg.messageId())
                                .replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{new InlineKeyboardButton(Game.this.translation.START_ME()).url("https://t.me/" + Main.getConfig("bot.username"))})));
                    }
                }

                @Override
                public void onFailure(SendMessage request, IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public boolean removePlayer(int tgid) {
        // if not in game then do nothing
        // otherwise remove them
        if (!started && players.removeIf(user -> user.id() == tgid)) {
            return uidGames.remove(tgid) != null;
        }
        return false;
    }

    public List<User> getPlayers() {
        return players;
    }

    public List<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public int playerCount() {
        return players.size();
    }

    public boolean started() {
        return started;
    }

    public int nextNonce() {
        return ++nextNonce;
    }

    public void extend() {
        if (started) {
            return;
        }
        cancelFuture();
        startTime += 30000;
        startTime = Math.min(startTime, System.currentTimeMillis() + 180000);
        long seconds = (startTime - System.currentTimeMillis() + 999) / 1000;
        this.execute(new SendMessage(gid, String.format(this.translation.EXTENDED_ANNOUNCEMENT(), seconds)));
        int i;
        i = 0;
        while (i < 6 && remindSeconds[i] < seconds) {
            ++i;
        }
//        this.log("scheduled remind task");
        schedule(this::remind, (seconds - remindSeconds[--i]) * 1000);
    }

    /**
     * Starts the game
     */
    private void start() {
        // tell them the game is starting
        SendMessage send = new SendMessage(gid, translation.GAME_STARTING_ANNOUNCEMENT());
        started = true;
        this.execute(send);
        //*
        // construct mainDeck
        // properties
        mainDeck.add(new PropertyCard(1, translation.PROPERTY_NAME(0), 0, translation));
        mainDeck.add(new PropertyCard(1, translation.PROPERTY_NAME(1), 0, translation));

        mainDeck.add(new PropertyCard(1, translation.PROPERTY_NAME(2), 1, translation));
        mainDeck.add(new PropertyCard(1, translation.PROPERTY_NAME(3), 1, translation));
        mainDeck.add(new PropertyCard(1, translation.PROPERTY_NAME(4), 1, translation));

        mainDeck.add(new PropertyCard(2, translation.PROPERTY_NAME(5), 2, translation));
        mainDeck.add(new PropertyCard(2, translation.PROPERTY_NAME(6), 2, translation));
        mainDeck.add(new PropertyCard(2, translation.PROPERTY_NAME(7), 2, translation));

        mainDeck.add(new PropertyCard(2, translation.PROPERTY_NAME(8), 3, translation));
        mainDeck.add(new PropertyCard(2, translation.PROPERTY_NAME(9), 3, translation));
        mainDeck.add(new PropertyCard(2, translation.PROPERTY_NAME(10), 3, translation));

        mainDeck.add(new PropertyCard(3, translation.PROPERTY_NAME(11), 4, translation));
        mainDeck.add(new PropertyCard(3, translation.PROPERTY_NAME(12), 4, translation));
        mainDeck.add(new PropertyCard(3, translation.PROPERTY_NAME(13), 4, translation));

        mainDeck.add(new PropertyCard(3, translation.PROPERTY_NAME(14), 5, translation));
        mainDeck.add(new PropertyCard(3, translation.PROPERTY_NAME(15), 5, translation));
        mainDeck.add(new PropertyCard(3, translation.PROPERTY_NAME(16), 5, translation));

        mainDeck.add(new PropertyCard(4, translation.PROPERTY_NAME(17), 6, translation));
        mainDeck.add(new PropertyCard(4, translation.PROPERTY_NAME(18), 6, translation));
        mainDeck.add(new PropertyCard(4, translation.PROPERTY_NAME(19), 6, translation));

        mainDeck.add(new PropertyCard(4, translation.PROPERTY_NAME(20), 7, translation));
        mainDeck.add(new PropertyCard(4, translation.PROPERTY_NAME(21), 7, translation));

        mainDeck.add(new PropertyCard(2, translation.PROPERTY_NAME(22), 8, translation));
        mainDeck.add(new PropertyCard(2, translation.PROPERTY_NAME(23), 8, translation));
        mainDeck.add(new PropertyCard(2, translation.PROPERTY_NAME(24), 8, translation));
        mainDeck.add(new PropertyCard(2, translation.PROPERTY_NAME(25), 8, translation));

        mainDeck.add(new PropertyCard(2, translation.PROPERTY_NAME(26), 9, translation));
        mainDeck.add(new PropertyCard(2, translation.PROPERTY_NAME(27), 9, translation));

        // wildcard properties
        mainDeck.add(new WildcardPropertyCard(3, translation.WILD_CARD(4, 5), new int[]{4, 5}, translation));
        mainDeck.add(new WildcardPropertyCard(3, translation.WILD_CARD(4, 5), new int[]{4, 5}, translation));
        mainDeck.add(new WildcardPropertyCard(4, translation.WILD_CARD(6, 7), new int[]{6, 7}, translation));
        mainDeck.add(new WildcardPropertyCard(1, translation.WILD_CARD(0, 1), new int[]{0, 1}, translation));
        mainDeck.add(new WildcardPropertyCard(2, translation.WILD_CARD(2, 3), new int[]{2, 3}, translation));
        mainDeck.add(new WildcardPropertyCard(2, translation.WILD_CARD(2, 3), new int[]{2, 3}, translation));
        mainDeck.add(new WildcardPropertyCard(4, translation.WILD_CARD(6, 8), new int[]{6, 8}, translation));
        mainDeck.add(new WildcardPropertyCard(4, translation.WILD_CARD(1, 8), new int[]{1, 8}, translation));
        mainDeck.add(new WildcardPropertyCard(2, translation.WILD_CARD(8, 9), new int[]{8, 9}, translation));
        mainDeck.add(new WildcardPropertyCard(0, translation.WILD_CARD(), new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, translation));
        mainDeck.add(new WildcardPropertyCard(0, translation.WILD_CARD(), new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, translation));

        // action mainDeck
        for (int i = 0; i < 3; i++) {
            mainDeck.add(new ItsMyBirthdayActionCard(translation));
        }
        for (int i = 0; i < 3; i++) {
            mainDeck.add(new DebtCollectorCard(translation));
        }
        for (int i = 0; i < 10; i++) {
            mainDeck.add(new GoPassActionCard(translation));
        }
        for (int i = 0; i < 2; i++) {
            mainDeck.add(new DoubleRentActionCard(translation));
        }
        for (int i = 0; i < 3; i++) {
            mainDeck.add(new JustSayNoCard(translation));
        }
        for (int i = 0; i < 2; i++) {
            mainDeck.add(new DealBreakerActionCard(translation));
        }
        for (int i = 0; i < 3; i++) {
            mainDeck.add(new SlyDealActionCard(translation));
        }
        for (int i = 0; i < 3; i++) {
            mainDeck.add(new HouseActionCard(translation));
        }
        for (int i = 0; i < 2; i++) {
            mainDeck.add(new HotelActionCard(translation));
        }
        for (int i = 0; i < 3; i++) {
            mainDeck.add(new ForcedDealActionCard(translation));
        }

        // rent mainDeck
        mainDeck.add(new RentActionCard(new int[]{0, 1}, translation));
        mainDeck.add(new RentActionCard(new int[]{0, 1}, translation));
        mainDeck.add(new RentActionCard(new int[]{2, 3}, translation));
        mainDeck.add(new RentActionCard(new int[]{2, 3}, translation));
        mainDeck.add(new RentActionCard(new int[]{4, 5}, translation));
        mainDeck.add(new RentActionCard(new int[]{4, 5}, translation));
        mainDeck.add(new RentActionCard(new int[]{6, 7}, translation));
        mainDeck.add(new RentActionCard(new int[]{6, 7}, translation));
        mainDeck.add(new RentActionCard(new int[]{8, 9}, translation));
        mainDeck.add(new RentActionCard(new int[]{8, 9}, translation));
        for (int i = 0; i < 3; i++) {
            mainDeck.add(new RentActionCard(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, translation));
        }

        // currency mainDeck
        for (int i = 0; i < 6; i++) {
            mainDeck.add(new CurrencyCard(1, "$1M", translation));
        }
        for (int i = 0; i < 5; i++) {
            mainDeck.add(new CurrencyCard(2, "$2M", translation));
        }
        for (int i = 0; i < 3; i++) {
            mainDeck.add(new CurrencyCard(3, "$3M", translation));
        }
        for (int i = 0; i < 3; i++) {
            mainDeck.add(new CurrencyCard(4, "$4M", translation));
        }
        mainDeck.add(new CurrencyCard(5, "$5M", translation));
        mainDeck.add(new CurrencyCard(5, "$5M", translation));
        mainDeck.add(new CurrencyCard(10, "$10M", translation));
        //*/

        /*
        for (int i = 0; i < 18; i++) {
            mainDeck.add(new PropertyCard(4, translation.PROPERTY_NAME(20), 7, translation));
            mainDeck.add(new PropertyCard(4, translation.PROPERTY_NAME(17), 6, translation));
        }
        for (int i = 0; i < 40; i++) {
            mainDeck.add(new RentActionCard(new int[]{6, 7}, translation));
        }
        for (int i = 0; i < 30; i++) {
            mainDeck.add(new JustSayNoCard(translation));
        }
        //*/
        
        Collections.shuffle(mainDeck);

        // construct players
        for (User player : players) {
            gamePlayers.add(new GamePlayer(this, player.id(), player));
        }

        // distribute mainDeck
        for (GamePlayer gamePlayer : gamePlayers) {
            for (int i = 0; i < 5; i++) {
                gamePlayer.addHand(mainDeck.remove(0));
            }
            gamePlayer.sendState();
        }
        Collections.shuffle(gamePlayers);

        startTime = System.currentTimeMillis();
        startTurn();
    }

    void pauseTurn() {
        if (turnPaused) {
            return;
        }
        turnPaused = true;
        cancelFuture();
        long now = System.currentTimeMillis();
        turnTime += now - turnStartTime;
        logf("pausing turn, turnTime=%s", turnTime);
    }

    public void resumeTurn() {
        cancelFuture();
        turnPaused = false;
        schedule(gamePlayers.get(currentTurn)::endTurnTimeout, turnWait * 1000 - turnTime);
        gamePlayers.get(currentTurn).promptForCard();
        turnStartTime = System.currentTimeMillis();
    }

    long getDelay() {
        if (future != null) {
            return future.getDelay(TimeUnit.MILLISECONDS);
        }
        return -1;
    }

    private void kill(boolean isError) {
        this.log("Game ended");
        if (started) {
            // the game is forcibly killed, kill aht buttons of the current player too
            if (isError) {
                this.execute(new EditMessageText(players.get(currentTurn).id(), gamePlayers.get(currentTurn).getMessageId(), this.translation.GAME_ENDED_ERROR()));
            } else {
                this.execute(new EditMessageText(players.get(currentTurn).id(), gamePlayers.get(currentTurn).getMessageId(), this.translation.GAME_ENDED()));
            }
        }
        cancelFuture();
        // remove this game instance
        this.execute(new SendMessage(gid, this.translation.GAME_ENDED_ANNOUNCEMENT()), new EmptyCallback<>());
        ended = true;
        // save the stats
        float gameMinutes = (System.currentTimeMillis() - startTime) / (float) 60000;
        if (!isError) {
            try {
                Connection conn = Main.getConnection();
                for (int i = 0; i < gamePlayers.size(); i++) {
                    GamePlayer gamePlayer = gamePlayers.get(i);
                    int propertiesPlayed = gamePlayer.getPropertiesPlayed();
                    int currencyCollected = gamePlayer.getCurrencyCollected();
                    int actionUsed = gamePlayer.getCardsPlayed();
                    int rentCollected = gamePlayer.getRentCollected();
                    int won = i == currentTurn ? 1 : 0;
                    PreparedStatement stmt = conn.prepareStatement("update tg_users set game_minutes=game_minutes+?, game_count=game_count+1, won_count=won_count+?, cards_played=cards_played+?, currency_collected=currency_collected+?, properties_collected=properties_collected+?, rent_collected=rent_collected+? where tgid=?");
                    stmt.setFloat(1, gameMinutes);
                    stmt.setInt(2, won);
                    stmt.setInt(3, actionUsed);
                    stmt.setInt(4, currencyCollected);
                    stmt.setInt(5, propertiesPlayed);
                    stmt.setInt(6, rentCollected);
                    stmt.setInt(7, gamePlayer.getTgid());
                    stmt.executeUpdate();
                    stmt.close();
                    stmt = conn.prepareStatement("select game_count, won_count from tg_users where tgid=?");
                    stmt.setInt(1, gamePlayer.getTgid());
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        int wins = rs.getInt(2);
                        int games = rs.getInt(1);
                        if (wins == 1) {
                            DealBot.triggerAchievement(gamePlayer.getTgid(), DealBot.Achievement.WINNER);
                        }
                        if (wins == 10) {
                            DealBot.triggerAchievement(gamePlayer.getTgid(), DealBot.Achievement.ADEPTED);
                        }
                        if (wins == 50) {
                            DealBot.triggerAchievement(gamePlayer.getTgid(), DealBot.Achievement.MASTER);
                        }
                        if (games == 1) {
                            DealBot.triggerAchievement(gamePlayer.getTgid(), DealBot.Achievement.GETTING_STARTED);
                        }
                        if (games == 10) {
                            DealBot.triggerAchievement(gamePlayer.getTgid(), DealBot.Achievement.FAMILIAR);
                        }
                        if (games == 50) {
                            DealBot.triggerAchievement(gamePlayer.getTgid(), DealBot.Achievement.ADDICTED);
                        }
                        if (players.stream().mapToInt(User::id).anyMatch(x -> x == Main.BOT_OWNER)) {
                            DealBot.triggerAchievement(gamePlayer.getTgid(), DealBot.Achievement.PLAY_WITH_MINT);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        games.remove(gid);
        for (int i = 0; i < playerCount(); i++) {
            uidGames.remove(players.get(i).id());
        }
    }

    public void kill() {
        kill(false);
    }

    public void tryStart() {
        if (started) {
            return;
        }
        // see if theres enough players to start the game
        if (playerCount() > 1) {
            cancelFuture();
            start();
        } else {
            kill(false);
        }
    }

    void addToMainDeck(Card card) {
        mainDeck.add(card);
    }

    void addToUsedDeck(Card card) {
        usedDeck.add(card);
    }

    void eliminate() {
        GamePlayer removed = gamePlayers.remove(currentTurn);
        players.removeIf(user -> user.id() == removed.getTgid());
        currentTurn = (currentTurn + gamePlayers.size() - 1) % gamePlayers.size();
        execute(new SendMessage(gid, translation.SB_IS_ELIMINATED(removed.getName())));
        logf("%s has been afk for 3 turns, eliminating!", removed.getTgid());
        try (Connection conn = Main.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("update tg_users set game_count=game_count+1 where tgid=?");
            stmt.setInt(1, removed.getTgid());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 0-9: normal groups
    // 10: birthday
    // 11: debt
    public void collectRentFromAll(int value, int group) {
        pauseTurn();
        paymentConfirmationCount = 0;
        paidPlayers.clear();
        GamePlayer gamePlayer = gamePlayers.get(currentTurn);
        if (value <= 0) {
            // nothing to collect
            resumeTurn();
            return;
        }
        for (GamePlayer player : gamePlayers) {
            if (player == gamePlayer) {
                // dont collect from yourself
                continue;
            }
            player.collectRent(value, group, gamePlayer);
        }
        DealBot.triggerAchievement(gamePlayer.getTgid(), DealBot.Achievement.RENT_COLLECTOR);
        if (value >= 20) {
            DealBot.triggerAchievement(gamePlayer.getTgid(), DealBot.Achievement.THANK_YOU);
        }
    }

    public void collectRentFromOne(int value, int group, int order) {
        pauseTurn();
        paidPlayers.clear();
        paymentConfirmationCount = gamePlayers.size() - 2; // shush
        GamePlayer gamePlayer = gamePlayers.get(currentTurn);
        if (value <= 0) {
            // nothing to collect
            resumeTurn();
            return;
        }
        gamePlayers.get(order).collectRent(value, group, gamePlayer);
        DealBot.triggerAchievement(gamePlayer.getTgid(), DealBot.Achievement.RENT_COLLECTOR);
        if (value >= 20) {
            DealBot.triggerAchievement(gamePlayer.getTgid(), DealBot.Achievement.THANK_YOU);
        }
    }

    private void remind() {
//        this.log("firing remind task");
        long seconds = (startTime - System.currentTimeMillis()) / 1000;
        this.execute(new SendMessage(gid, String.format(this.translation.JOIN_PROMPT(), Math.round(seconds / 15.0) * 15)));
        int i;
        i = 0;
        while (remindSeconds[i] < seconds) {
            ++i;
        }
        if (i > 0) {
//            this.log("scheduled remind task");
            schedule(this::remind, startTime - System.currentTimeMillis() - remindSeconds[--i] * 1000);
        } else {
//            this.log(String.format("scheduled kill task after %d seconds", seconds));
            schedule(this::tryStart, startTime - System.currentTimeMillis());
        }
    }

    private void startTurn() {
        currentCard = null;
        turnStartTime = System.currentTimeMillis();
        turnTime = 0;
        turnPaused = false;
        // tell the current player the current situation
        GamePlayer player = gamePlayers.get(currentTurn);
        String currentState = getGlobalState();
        player.sendGlobalState(currentState);
        execute(new SendMessage(gid, currentState).parseMode(ParseMode.HTML));
        player.addHand(draw());
        player.addHand(draw());
        // draw 5 if starting with 0
        if (player.handCount() == 2) {
            player.addHand(draw());
            player.addHand(draw());
            player.addHand(draw());
        }
        cancelFuture();
        player.startTurn();
    }

    public Card draw() {
        if (mainDeck.isEmpty()) {
            // the deck is exhausted, shuffle the used deck
            Collections.shuffle(usedDeck);
            mainDeck.addAll(usedDeck);
            usedDeck.clear();
        }
        return mainDeck.remove(0);
    }

    private String getGlobalState() {
        StringBuilder currentState = new StringBuilder(translation.CURRENT_STATE()).append("\n");
        for (int i = 0; i < gamePlayers.size(); i++) {
            int index = (i + currentTurn) % gamePlayers.size();
            GamePlayer player = gamePlayers.get(index);
            currentState.append(i + 1).append(". ").append(player.getName()).append("\n");
            currentState.append(translation.CARDS_IN_HAND()).append(player.handCount()).append("\n");
            currentState.append(translation.CARDS_IN_CURRENCY_DECK()).append(player.currencyCount()).append("\n");
            Map<Integer, List<Card>> propertyDecks = player.getPropertyDecks();
            List<Integer> groups = new ArrayList<>(propertyDecks.keySet());
            groups.sort((o1, o2) -> {
                List<Card> props1 = propertyDecks.get(o1);
                List<Card> props2 = propertyDecks.get(o2);
                int count1 = PropertyCard.realCount(props1);
                int total1 = PropertyCard.propertySetCounts[o1];
                int count2 = PropertyCard.realCount(props2);
                int total2 = PropertyCard.propertySetCounts[o2];
                double ratio1 = count1 / (double) total1;
                double ratio2 = count2 / (double) total2;
                return Double.compare(ratio2, ratio1);
            });
            int sets = 0;
            for (Integer group : groups) {
                List<Card> props = propertyDecks.get(group);
                int count = PropertyCard.realCount(props);
                int total = PropertyCard.propertySetCounts[group];
                if (count >= total) {
                    sets++;
                }
            }
            currentState.append(translation.PROPERTIES()).append(translation.NO_OF_FULL_SETS(sets)).append(":\n");
            for (Integer group : groups) {
                List<Card> props = propertyDecks.get(group);
                if (props.isEmpty()) {
                    continue;
                }
                int count = PropertyCard.realCount(props);
                int total = PropertyCard.propertySetCounts[group];
                if (count >= total) {
                    currentState.append("<b>");
                }
                currentState.append(translation.PROPERTY_GROUP(group)).append(" (").append(count).append("/")
                        .append(total).append("): ");
                for (Card prop : props) {
                    currentState.append(prop.getCardTitle()).append(", ");
                }
                currentState.setLength(currentState.length() - 2);
                if (count >= total) {
                    currentState.append("</b>");
                }
                currentState.append("\n");
            }
            currentState.append("\n");
        }
        return currentState.toString().trim();
    }

    void nextTurn() {
        // we wait a bit to wait for previous cards to finish processing
        executor.schedule(() -> {
            // before we start, we check if the previous player had won
            GamePlayer player = gamePlayers.get(currentTurn);
            if (player.checkWinCondition() || gamePlayers.size() == 1) {
                // won
                String msg;
                if (gamePlayers.size() > 1) {
                    msg = translation.WON_ANNOUNCEMENT(player.getTgid(), player.getName());
                    execute(new SendMessage(gid, getGlobalState()).parseMode(ParseMode.HTML));
                } else {
                    msg = translation.LONE_WIN(player.getTgid(), player.getName());
                }
                this.execute(new SendMessage(gid, msg).parseMode(ParseMode.HTML));
                this.kill(false);
                return;
            }
            currentTurn = (currentTurn + 1) % gamePlayers.size();
            startTurn();
        }, 500, TimeUnit.MILLISECONDS);
    }

    void cancelFuture() {
        if (future != null && !future.isDone() && !future.isCancelled()) {
//            this.log("cancelled task");
            logf("cancelFuture() called from %s", Thread.currentThread().getStackTrace()[2]);
            logf("                           %s", Thread.currentThread().getStackTrace()[3]);
            logf("                           %s", Thread.currentThread().getStackTrace()[4]);
            future.cancel(true);
            future = null;
        }
    }

    void schedule(Runnable runnable, long l) {
        cancelFuture();
        logf("schedule() called from %s %s", Thread.currentThread().getStackTrace()[2], l);
        logf("                       %s", Thread.currentThread().getStackTrace()[3]);
        logf("                       %s", Thread.currentThread().getStackTrace()[4]);
        future = executor.schedule(runnable, l, TimeUnit.MILLISECONDS);
    }

    public String getLang() {
        return lang;
    }

    int getTurnWait() {
        return turnWait;
    }

    int getPaymentWait() {
        return groupInfo.payTime;
    }

    int getObjectionWait() {
        return groupInfo.sayNoTime;
    }

    void log(Object o) {
        String date = sdf.format(new Date());
        System.out.printf("[%s][Game %d] %s\n", date, id, o);
    }

    void logf(String format, Object... objs) {
        String date = sdf.format(new Date());
        Object[] _objs = new Object[objs.length + 2];
        _objs[1] = id;
        _objs[0] = date;
        System.arraycopy(objs, 0, _objs, 2, objs.length);
        System.out.printf("[%s][Game %d] " + format + "\n", _objs);
    }

    public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(T request) {
        this.execute(request, null);
    }

    public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(T request, Callback<T, R> callback) {
        this.execute(request, callback, 0);
    }

    public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(T request, Callback<T, R> callback, int failCount) {
        Map<String, Object> params = request.getParameters();
        boolean willThrottle = request instanceof SendMessage && params.get("reply_markup") == null && params.get("chat_id").toString().equals(String.valueOf(gid));
        if (request instanceof SendMessage) {
            ((SendMessage) request).parseMode(ParseMode.HTML);
        }
        Runnable execute = () -> {
            if (willThrottle) {
                broadcastMessageCache = "";
                sendMessageFuture = null;
            }
            bot.execute(request, new Callback<T, R>() {
                @Override
                public void onResponse(T request, R response) {
                    if (callback != null) {
                        callback.onResponse(request, response);
                    }
                }

                @Override
                public void onFailure(T request, IOException e) {
                    if (callback != null) {
                        callback.onFailure(request, e);
                    }
                    Game.this.logf("HTTP Error: %s %s\n", e.getClass().toString(), e.getMessage());
                    e.printStackTrace();
                    if (failCount < 5) { // linear backoff, max 5 retries
                        new Thread(() -> {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ignored) {
                            }
                            Game.this.execute(request, callback, failCount + 1);
                        }).start();
                    } else {
                        Game.this.kill(true);
                    }
                }
            });
        };
        if (willThrottle) {
            // is broadcast and not keyboard
            if (sendMessageFuture != null) {
                sendMessageFuture.cancel(true);
                sendMessageFuture = null;
            }
            broadcastMessageCache += "\n\n" + params.get("text");
            params.put("text", broadcastMessageCache);
            sendMessageFuture = executor.schedule(execute, 1000, TimeUnit.MILLISECONDS);
        } else {
            execute.run();
        }
    }

    public boolean callback(CallbackQuery query) {
        String payload = query.data();
        String[] _args = payload.split(":");
        int nonce;
        try {
            nonce = Integer.parseInt(_args[0]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (usedNonces.contains(nonce)) {
            logf("query %s ignored, repeated nonce", payload);
            return true;
        }
        usedNonces.add(nonce);
        String[] args = new String[_args.length - 1];
        System.arraycopy(_args, 1, args, 0, args.length);
        if (gamePlayers.get(currentTurn).getTgid() != query.from().id() && !args[0].startsWith("pay_")
                && !args[0].equals("say_no")) {
            bot.execute(new AnswerCallbackQuery(query.id()));
            bot.execute(new EditMessageReplyMarkup(query.message().chat().id(), query.message().messageId()));
            return true;
        }
        GamePlayer player = gamePlayers.get(currentTurn);
        switch (args[0]) {
            case "play_card":
                this.currentCard = player.handCardAt(Integer.parseInt(args[1]));
                player.removeHand(this.currentCard);
                if (this.currentCard instanceof ActionCard) {
                    // only action cards should go to the used deck
                    usedDeck.add(this.currentCard);
                }
                player.play(this.currentCard);
                logf("query from %s: %s, card=%s\n", query.from().id(), query.data(), this.currentCard);
                return true;
            case "end_turn":
                player.endTurnVoluntary();
                logf("query from %s: %s\n", query.from().id(), query.data());
                return true;
            case "card_arg":
                String[] subarray = new String[args.length - 1];
                System.arraycopy(args, 1, subarray, 0, args.length - 1);
                if (currentCard instanceof ActionCard) {
                    ((ActionCard) currentCard).use(gamePlayers.get(currentTurn), subarray);
                } else {
                    currentCard.execute(gamePlayers.get(currentTurn), subarray);
                }
                logf("query from %s: %s, card=%s\n", query.from().id(), query.data(), this.currentCard);
                return true;
            case "use_as":
                if (args[1].equals("money")) {
                    player.addCurrency(this.currentCard);
                    player.playedCurrency(this.currentCard.currencyValue());
                    SendMessage send = new SendMessage(player.getTgid(), translation.YOU_DEPOSITED(this.currentCard.getCardTitle()));
                    player.getGame().execute(send);
                    send = new SendMessage(gid, translation.SOMEONE_DEPOSITED(player.getName(), this.currentCard.getCardTitle()));
                    player.getGame().execute(send);
                    player.promptForCard();
                } else {
                    ((ActionCard) this.currentCard).use(player, new String[0]);
                }
                logf("query from %s: %s, card=%s\n", query.from().id(), query.data(), this.currentCard);
                return true;
            case "use_cancel":
                player.addHand(this.currentCard);
                usedDeck.remove(this.currentCard);
                this.currentCard = null;
                player.addMove();
                player.promptForCard();
                logf("query from %s: %s\n", query.from().id(), query.data());
                return true;
            case "dispose_card":
                Card disposed = player.handCardAt(Integer.parseInt(args[1]));
                player.removeHand(disposed);
                addToMainDeck(disposed);
                player.endTurnVoluntary();
                execute(new SendMessage(gid, translation.SB_DISPOSED(player.getName(), disposed.getCardTitle())));
                execute(new SendMessage(player.getTgid(), translation.YOU_DISPOSED(disposed.getCardTitle())));
                logf("query from %s: %s, card=%s\n", query.from().id(), query.data(), this.currentCard);
                return true;
            case "pay_choose":
            case "pay_confirm":
            case "pay_reject":
                int tgid = query.from().id();
                for (GamePlayer gamePlayer : gamePlayers) {
                    if (gamePlayer.getTgid() == tgid) {
                        gamePlayer.payCallback(args, query.id());
                        break;
                    }
                }
                logf("query from %s: %s\n", query.from().id(), query.data());
                return true;
            case "wildcard_menu":
                tgid = query.from().id();
                for (GamePlayer gamePlayer : gamePlayers) {
                    if (gamePlayer.getTgid() == tgid) {
                        gamePlayer.wildcardMenuCallback(args, query.id());
                        break;
                    }
                }
                logf("query from %s: %s\n", query.from().id(), query.data());
                return true;
            case "say_no":
                tgid = query.from().id();
                for (GamePlayer gamePlayer : gamePlayers) {
                    if (gamePlayer.getTgid() == tgid) {
                        gamePlayer.sayNoCallback(args, query.id(), query.message().messageId());
                        break;
                    }
                }
                logf("query from %s: %s\n", query.from().id(), query.data());
                return true;
        }
        return false;
    }

    public void removeFromUsed(Card card) {
        usedDeck.remove(card);
    }

    boolean confirmPayment(List<Card> payment, int tgid) {
        if (paidPlayers.contains(tgid)) {
            logf("confirm payment from %s but duplicated", tgid);
            return false;
        }
        logf("confirm payment from %s", tgid);
        String name = "";
        for (User user : players) {
            if (user.id() == tgid) {
                name = user.firstName();
                break;
            }
        }
        ++paymentConfirmationCount;
        logf("confirmed payments: %s / %s", paymentConfirmationCount, gamePlayers.size() - 1);
        paidPlayers.add(tgid);
        int id = gamePlayers.get(currentTurn).getTgid();
        if (payment != null) {
            String paymentStr = (payment.stream().map(x -> x.isCurrency() ? ("$ " + x.currencyValue() + "M") : (x.getCardTitle())).collect(Collectors.joining(", ")));
            for (Card card : payment) {
                if (card instanceof PropertyCard) {
                    PropertyCard pcard = (PropertyCard) card;
                    gamePlayers.get(currentTurn).addProperty(pcard, pcard.getGroup());
                } else {
                    gamePlayers.get(currentTurn).addCurrency(card);
                }
            }
            gamePlayers.get(currentTurn).rentCollected(payment.stream().mapToInt(Card::currencyValue).sum());
            SendMessage send = new SendMessage(id, translation.SB_PAID_YOU(name, paymentStr));
            execute(send);
            send = new SendMessage(gid, translation.SB_PAID_SB(name, gamePlayers.get(currentTurn).getName(), paymentStr));
            execute(send);
        } else {
            SendMessage send = new SendMessage(id, translation.VICTIM_SAID_NO(name));
            execute(send);
        }
        if (paymentConfirmationCount == gamePlayers.size() - 1) {
            // everyone has paid
            resumeTurn();
            // restart end turn timer
        }
        return true;
    }

    public Translation getTranslation() {
        return translation;
    }

    public long getGid() {
        return gid;
    }

    public static class RunInfo {
        public final int runningCount;
        public final int gameCount;
        public final int playerCount;
        public final Map<Long, Game> games;

        RunInfo(int runningCount, int gameCount, int playerCount, Map<Long, Game> games) {
            this.runningCount = runningCount;
            this.gameCount = gameCount;
            this.playerCount = playerCount;
            this.games = games;
        }
    }
}
