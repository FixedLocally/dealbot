package me.lkp111138.dealbot.game;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

public class CardArgumentRequest {
    private InlineKeyboardMarkup keyboard;
    private String message;

    public CardArgumentRequest(InlineKeyboardMarkup keyboard, String message) {
        this.keyboard = keyboard;
        this.message = message;
    }

    public InlineKeyboardMarkup getKeyboard() {
        return keyboard;
    }

    public String getMessage() {
        return message;
    }
}
