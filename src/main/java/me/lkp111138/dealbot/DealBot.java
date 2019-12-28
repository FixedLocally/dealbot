package me.lkp111138.dealbot;

import com.google.gson.Gson;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DealBot extends TelegramBot implements UpdatesListener {
    private Map<String, Map<String, String>> translations = new HashMap<>();
    public DealBot(String botToken) throws FileNotFoundException {
        super(botToken);
        setUpdatesListener(this);
        initTranslations();
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(this::processUpdate);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void initTranslations() throws FileNotFoundException {
        translations = new Gson().fromJson(new FileReader(new File("translations.json")), Map.class);
    }

    private void processUpdate(Update update) {

    }

    /**
     * Translates a string by key
     * @param language the language to translate to
     * @param key the key to be translated
     * @param args arguments to the translated string
     * @return the translated string with appropriate substitutions, or the key itself if cannot be translated
     */
    public String getTranslation(String language, String key, String... args) {
        Map<String, String> lang = translations.getOrDefault(language, translations.get("en"));
        String s = lang.getOrDefault(key, key);
        for (String arg : args) {
            s = s.replace("%s", arg);
        }
        return s;
    }
}
