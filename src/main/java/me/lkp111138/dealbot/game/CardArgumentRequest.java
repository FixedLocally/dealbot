package me.lkp111138.dealbot.game;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

public class CardArgumentRequest {
    private InlineKeyboardButton[][] keyboard;
    private String message;

    public CardArgumentRequest(InlineKeyboardButton[][] keyboard, String message) {
        this.keyboard = keyboard;
        this.message = message;
    }

    public InlineKeyboardButton[][] getKeyboard() {
        return keyboard;
    }

    public String getMessage() {
        return message;
    }
}
