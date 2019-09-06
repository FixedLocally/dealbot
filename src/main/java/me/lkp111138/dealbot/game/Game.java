package me.lkp111138.dealbot.game;

import com.google.gson.JsonObject;
import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
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

public class Game {
    private final GroupInfo groupInfo;
    private int turnWait;
    private int currentTurn = 0;
    private long startTime;
    private long gid;
    private Chat group;
    private boolean started = false;
    private boolean firstRound = true;
    private List<User> players = new ArrayList<>();
    private int[] deckMsgid = new int[4];
    private User deskUser;
    private ScheduledFuture future;
    private int currentMsgid;
    private String lang;
    private Translation translation;
    private JsonObject gameSequence = new JsonObject();
    private int id;

    private boolean ended = false;
    private int[] offsets;

    private static Map<Long, Game> games = new HashMap<>();
    public static boolean maintMode = false; // true=disallow starting games
    private static TelegramBot bot;
    private static ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(2);
    private static Map<Integer, Game> uidGames = new HashMap<>();
    private static final int CARDS_PER_PLAYER = 5;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private static int[] remindSeconds = new int[]{15, 30, 60, 90, 120, 180};

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
        this.group = msg.chat();
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
        if (!started && !players.contains(from) && !uidGames.containsKey(from.id()) && playerCount() < 4) {
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
                        if (playerCount() >= 4 || players.contains(msg.from())) {
                            // oops, you're the 5th player bye
                            return;
                        }
                        players.add(msg.from());
                        uidGames.put(msg.from().id(), Game.this);
                        int count = playerCount();
                        Game.this.execute(new SendMessage(msg.chat().id(), String.format(Game.this.translation.JOINED_ANNOUNCEMENT(), msg.from().id(), msg.from().firstName(), count)).parseMode(ParseMode.HTML), new EmptyCallback<>());
//                        Game.this.logf("%d / 4 players joined", count);
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

    public int playerCount() {
        return players.size();
    }

    public boolean started() {
        return started;
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

    private void updateDeck(int i) {
    }

    private void kill(boolean isError) {
        this.log("Game ended");
        if (started) {
            // the game is forcibly killed, kill aht buttons of the current player too
            if (isError) {
                this.execute(new EditMessageText(players.get(currentTurn).id(), currentMsgid, this.translation.GAME_ENDED_ERROR()));
            } else {
                this.execute(new EditMessageText(players.get(currentTurn).id(), currentMsgid, this.translation.GAME_ENDED()));
            }
            for (int i = 0; i < 4; ++i) {
                this.execute(new EditMessageReplyMarkup(players.get(i).id(), deckMsgid[i]));
            }
        }
        cancelFuture();
        // remove this game instance
        this.execute(new SendMessage(gid, this.translation.GAME_ENDED_ANNOUNCEMENT()), new EmptyCallback<>());
        for (int i = 0; i < playerCount(); i++) {
            uidGames.remove(players.get(i).id());
        }
        games.remove(gid);
        ended = true;
    }

    public void kill() {
        kill(false);
    }

    private void tryStart() {
        // see if theres enough players to start the game
        if (playerCount() > 1) {
            // start
        } else {
            kill(false);
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
            schedule(this::kill, startTime - System.currentTimeMillis());
        }
    }

    private void cancelFuture() {
        if (future != null && !future.isDone() && !future.isCancelled()) {
//            this.log("cancelled task");
            future.cancel(true);
            future = null;
//            try {
//                throw new Exception();
//            } catch (Exception e) {
//                this.log(e.getStackTrace()[1]);
//            }
        }
    }

    private void schedule(Runnable runnable, long l) {
        cancelFuture();
        future = executor.schedule(runnable, l, TimeUnit.MILLISECONDS);
    }

    public String getLang() {
        return lang;
    }


    private void log(Object o) {
        String date = sdf.format(new Date());
        System.out.printf("[%s][Game %d] %s\n", date, id, o);
    }

    private void logf(String format, Object ...objs) {
        String date = sdf.format(new Date());
        Object[] _objs = new Object[objs.length + 2];
        _objs[1] = id;
        _objs[0] = date;
        System.arraycopy(objs, 0, _objs, 2, objs.length);
        System.out.printf("[%s][Game %d] " + format + "\n", _objs);
    }

    private <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(T request) {
        this.execute(request, null);
    }

    private <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(T request, Callback<T, R> callback) {
        this.execute(request, callback, 0);
    }

    private <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(T request, Callback<T, R> callback, int failCount) {
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
                Game.this.logf("HTTP Error: %s %s\n%s\n", e.getClass().toString(), e.getMessage(), e.getStackTrace()[0]);
                if (failCount < 5) { // linear backoff, max 5 retries
                    new Thread(() -> {
                        try {
                            Thread.sleep(5000 * failCount + 5000);
                        } catch (InterruptedException ignored) {
                        }
                        Game.this.execute(request, callback, failCount + 1);
                    }).start();
                } else {
                    Game.this.kill(true);
                }
            }
        });
    }

    public boolean callback(CallbackQuery query) {
        return false;
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
