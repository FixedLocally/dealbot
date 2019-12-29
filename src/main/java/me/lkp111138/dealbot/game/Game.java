package me.lkp111138.dealbot.game;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.EmptyCallback;
import me.lkp111138.dealbot.Main;
import me.lkp111138.dealbot.game.exception.ConcurrentGameException;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    // broadcast fields
    private ScheduledExecutorService broadcastExecutor = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture broadcastFuture;
    private String broadcastMessage = "";

    // scheduler fields
    private ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture scheduledFuture;

    /**
     * Constructs the game in preparation to start it
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
                schedule(this::start, millis);
                break;
            default:
                schedule(this::joinReminder, (millis - 1000) % 30000 + 1000);
        }
        broadcast(bot.translate(this.lang, "game.join_reminder", secs), true);
    }

    /**
     * Starts the game
     */
    public void start() {
        started = true;
        broadcast("starting...", false);
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
            schedule(this::start, millis);
        }
        broadcast(bot.translate(this.lang, "game.join_extended", secs, millis / 1000));
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
}
