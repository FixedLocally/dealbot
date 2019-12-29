package me.lkp111138.dealbot;

import com.google.gson.Gson;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.AnswerPreCheckoutQuery;
import me.lkp111138.dealbot.command.*;
import me.lkp111138.dealbot.game.Game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DealBot extends TelegramBot implements UpdatesListener {
    private Map<String, Map<String, String>> translations = new HashMap<>();
    private Map<Long, String> userLang = new HashMap<>();
    private Map<String, Command> commands = new HashMap<>();

    private Command fallback = new FallbackCommand();

    public DealBot(String botToken) throws FileNotFoundException {
        super(botToken);
        setUpdatesListener(this);
        initTranslations();
        initCommands();
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(this::processUpdate);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void initTranslations() throws FileNotFoundException {
        translations = new Gson().fromJson(new FileReader(new File("translations.json")), Map.class);
    }

    private void initCommands() {
        commands.put("play", new PlayCommand());
        commands.put("extend", new ExtendCommand());
        commands.put("join", new JoinCommand());
        commands.put("flee", new FleeCommand());
    }

    private void processUpdate(Update update) {
        Message message = update.message();
        CallbackQuery query = update.callbackQuery();
        PreCheckoutQuery preCheckoutQuery = update.preCheckoutQuery();
        if (message != null) {
            MessageEntity[] entities = message.entities();
            int sender = message.from().id();
            // TODO: blacklist
            // Supergroup upgrade, change existing group ID
            if (message.migrateFromChatId() != null) {
                // migrate settings if any
                try (Connection conn = Main.getConnection()) {
                    PreparedStatement stmt = conn.prepareStatement("update `groups` set gid=? where gid=?");
                    stmt.setLong(1, message.migrateFromChatId());
                    stmt.setLong(2, message.chat().id());
                    stmt.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            // check for kicking
            if (message.leftChatMember() != null && message.leftChatMember().id().toString().equals(Main.getConfig("bot.uid"))) {
                System.out.printf("was kicked in group %s [%s], 10min ban applied\n", message.chat().id(), message.chat().title());
                // is kicked, ban for 10mins
                executeBan(message.chat().id(), "COMMAND", 600, "kick autoban");
                // kill existing game
                Game g = Game.byGroup(message.chat().id());
                if (g != null) {
                    g.kill();
                }
            }
            // process commands
            if (entities != null && entities.length > 0 && entities[0].type().equals(MessageEntity.Type.bot_command)) {
                int offset = entities[0].offset();
                String command = message.text().substring(offset + 1);
                String[] segments = command.split(" ");
                String[] cmd = segments[0].split("@");
                if (cmd.length > 1 && !cmd[1].equals(Main.getConfig("bot.username"))) {
                    // command not intended for me
                    return;
                }
                try {
                    commands.getOrDefault(cmd[0], fallback).respond(this, message, segments);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        if (query != null) {
//            System.out.printf("query from %s: %s\n", query.from().id(), query.data());

            System.out.println("unknown query from " + query.from().id() + ": " + query.data());
            this.execute(new AnswerCallbackQuery(query.id()));
            return;
        }
        if (preCheckoutQuery != null) {
            this.execute(new AnswerPreCheckoutQuery(preCheckoutQuery.id()));
            return;
        }
        System.out.println("unknown update: " + update.toString());
    }

    private void executeBan(Long id, String type, int time, String reason) {
        // TODO
    }

    /**
     * Translates a string by key
     * @param language the language to translate to
     * @param key the key to be translated
     * @param args arguments to the translated string
     * @return the translated string with appropriate substitutions, or the key itself if cannot be translated
     */
    public String translate(String language, String key, Object... args) {
        Map<String, String> lang = translations.getOrDefault(language, translations.get("en"));
        String s = lang.get(key);
        if (s == null) {
            if (lang.containsKey("inherits")) {
                s = translations.get(lang.get("inherits")).getOrDefault(key, key);
            } else {
                s = key;
            }
        }
        for (Object arg : args) {
            s = s.replaceFirst("%s", arg.toString());
        }
        return s;
    }

    /**
     * Translates a string by key
     * @param gid the group or user id to translate for
     * @param key the key to be translated
     * @param args arguments to the translated string
     * @return the translated string with appropriate substitutions, or the key itself if cannot be translated
     */
    public String translate(long gid, String key, Object... args) {
        String lang = getLanguage(gid);
        return translate(lang, key, args);
    }

    /**
     * Gets the language code for a group or user
     * @param id the group or user ID
     * @return the language code
     */
    public String getLanguage(long id) {
        return userLang.computeIfAbsent(id, l -> {
            try (Connection conn = Main.getConnection()) {
                PreparedStatement stmt;
                if (id < 0) {
                    stmt = conn.prepareStatement("SELECT lang FROM `groups` WHERE gid=?");
                } else {
                    stmt = conn.prepareStatement("SELECT lang FROM tg_users WHERE tgid=?");
                }
                stmt.setLong(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString(1);
                } else {
                    return "en";
                }
            } catch (SQLException e) {
                return "en";
            }
        });
    }
}
