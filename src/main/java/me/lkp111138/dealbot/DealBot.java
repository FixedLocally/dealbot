package me.lkp111138.dealbot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.AnswerPreCheckoutQuery;
import me.lkp111138.dealbot.commands.*;
import me.lkp111138.dealbot.game.Game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

//import me.lkp111138.dealbot.game.achievement.*;

public class DealBot {
    private final Command fallback = new FallbackCommand();
    private final Map<String, Command> commands = new HashMap<>();
    private final TelegramBot bot;

    private static Map<Long, String> group_lang = new HashMap<>();
    private static Map<Long, Ban> bans = new HashMap<>();

    DealBot(TelegramBot bot) {
        this.bot = bot;
        init();
    }

    private void init() {
        this.bot.setUpdatesListener(list -> {
            for (Update update : list) {
                processUpdate(update);
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

        // commands
        commands.put("start", new StartCommand());
        commands.put("play", new PlayCommand());
        commands.put("startgame", commands.get("play"));
        commands.put("join", new JoinCommand());
        commands.put("flee", new FleeCommand());
        commands.put("stats", new StatCommand());
        commands.put("killgame", new KillGameCommand());
        commands.put("extend", new ExtendCommand());
//        commands.put("config", new ConfigCommand());
        commands.put("maintmode", new MaintModeCommand());
//        commands.put("runinfo", new RunInfoCommand());
        commands.put("setlang", new SetLangCommand());
        commands.put("help", new HelpCommand());
        commands.put("feedback", new FeedbackCommand());
        commands.put("achv", new AchvCommand());
        commands.put("toggle69", new Toggle69Command());
        commands.put("forcestart", new ForceStartCommand());
        commands.put("fs", commands.get("forcestart"));
        commands.put("donate", new DonateCommand());
        commands.put("nextgame", new NextGameCommand());

        // achievements
//        Achievement.registerAchievement(new FirstGameAchievement());
//        Achievement.registerAchievement(new FirstWinAchievement());
//        Achievement.registerAchievement(new PlayWithMintAchievement());
//        Achievement.registerAchievement(new FirstBloodAchievement());
//        Achievement.registerAchievement(new RookieAchievement());
//        Achievement.registerAchievement(new FamiliarizedAchievement());
//        Achievement.registerAchievement(new AddictedAchievement());
//        Achievement.registerAchievement(new AmateurAchievement());
//        Achievement.registerAchievement(new AdeptAchievement());
//        Achievement.registerAchievement(new ExpertAchievement());
//        Achievement.registerAchievement(new LoseItAllAchievement());
//        Achievement.registerAchievement(new DeepFriedAchievement());

        // bans
        try (PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT tgid, until, type from bans WHERE until>?")) {
            stmt.setInt(1, (int) (System.currentTimeMillis() / 1000));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                long tgid = rs.getLong(1);
                Ban oldBan = bans.get(tgid);
                Ban ban = new Ban(rs.getLong(1), rs.getInt(2), rs.getString(3));
                if (oldBan == null || oldBan.expiry < ban.expiry) {
                    bans.put(tgid, ban);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void processUpdate(Update update) {
        // get update type
        Message msg = update.message();
        CallbackQuery query = update.callbackQuery();
        PreCheckoutQuery preCheckoutQuery = update.preCheckoutQuery();
        if (msg != null) {
            MessageEntity[] entities = msg.entities();
            int sender = msg.from().id();
            // blacklist
            if ("COMMAND".equals(queryBan(sender))) {
                return;
            }
            if ("COMMAND".equals(queryBan(msg.chat().id()))) {
                return;
            }
            if (msg.migrateFromChatId() != null) {
                // migrate settings if any
                try (Connection conn = Main.getConnection()) {
                    PreparedStatement stmt = conn.prepareStatement("update `groups` set gid=? where gid=?");
                    stmt.setLong(1, msg.migrateFromChatId());
                    stmt.setLong(2, msg.chat().id());
                    stmt.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (msg.leftChatMember() != null && msg.leftChatMember().id().toString().equals(Main.getConfig("bot.uid"))) {
                System.out.printf("was kicked in group %s [%s], 10min ban applied\n", msg.chat().id(), msg.chat().title());
                // is kicked, ban for 10mins
                executeBan(msg.chat().id(), "COMMAND", 600, "kick autoban");
                // kill existing game
                Game g = Game.byGroup(msg.chat().id());
                if (g != null) {
                    g.kill();
                }
            }
            if (entities != null && entities.length > 0 && entities[0].type().equals(MessageEntity.Type.bot_command)) {
                int offset = entities[0].offset();
                String command = msg.text().substring(offset + 1);
                String[] segments = command.split(" ");
                String[] cmd = segments[0].split("@");
                if (cmd.length > 1 && !cmd[1].equals(Main.getConfig("bot.username"))) {
                    // command not intended for me
                    return;
                }
                try {
                    commands.getOrDefault(cmd[0], fallback).respond(bot, msg, segments);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        if (query != null) {
            System.out.printf("query from %s: %s\n", query.from().id(), query.data());
            Game g = Game.byUser(query.from().id());
            if (g != null) {
                try {
                    if (g.callback(query)) {
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (ConfigCommand.callback(bot, query)) {
                return;
            }
            if (SetLangCommand.callback(bot, query)) {
                return;
            }
            if (NextGameCommand.callback(bot, query)) {
                return;
            }
            System.out.println("unknown query from " + query.from().id() + ": " + query.data());
            bot.execute(new AnswerCallbackQuery(query.id()));
            return;
        }
        if (preCheckoutQuery != null) {
            bot.execute(new AnswerPreCheckoutQuery(preCheckoutQuery.id()));
            return;
        }
        System.out.println("unknown update: " + update.toString());
    }

    public static String lang(long gid) {
        return group_lang.computeIfAbsent(gid, aLong -> {
            try (Connection conn = Main.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("SELECT lang FROM `groups` WHERE gid=?");
                stmt.setLong(1, gid);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString(1);
                } else {
                    return "en";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return "en";
            }
        });
    }

    public static void setLang(long gid, String lang) {
        try (Connection conn = Main.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("update `groups` set lang=? where gid=?");
            stmt.setString(1, lang);
            stmt.setLong(2, gid);
            group_lang.put(gid, lang);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Query user or group ban status
     * @param tgid the id to be ueried
     * @return the type of ban, or null if none
     */
    public static String queryBan(long tgid) {
        Ban ban = bans.get(tgid);
        if (ban == null) {
            return null;
        }
        return ban.type;
    }

    public static void executeBan(long tgid, String type, int length, String reason) {
        try (Connection conn = Main.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT into bans (tgid, until, count, type, reason) VALUES (?, ?, ?, ?, ?)");
            int expiry = (int) (System.currentTimeMillis() / 1000) + length;
            stmt.setLong(1, tgid);
            stmt.setInt(2, expiry);
            stmt.setInt(3, 0);
            stmt.setString(4, type);
            stmt.setString(5, reason);
            stmt.execute();
            stmt.close();
            Ban ban = new Ban(tgid, expiry, type);
            Ban oldBan = bans.get(tgid);
            if (oldBan != null && oldBan.expiry < ban.expiry) {
                bans.put(tgid, ban);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static class Ban {
        final long tgid;
        final int expiry;
        final String type;

        private Ban(long tgid, int expiry, String type) {
            this.tgid = tgid;
            this.expiry = expiry;
            this.type = type;
        }

        @Override
        public String toString() {
            return "Ban{" +
                    "tgid=" + tgid +
                    ", expiry=" + expiry +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}
