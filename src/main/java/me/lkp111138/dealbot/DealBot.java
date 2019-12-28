package me.lkp111138.dealbot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import java.util.List;

public class DealBot extends TelegramBot implements UpdatesListener {
    public DealBot(String botToken) {
        super(botToken);

        setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(this::processUpdate);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processUpdate(Update update) {

    }
}
