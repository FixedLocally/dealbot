package me.lkp111138.dealbot.game;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

public class CardArgumentRequest {
    private InlineKeyboardButton[][] keyboard;
    private String message;
    private Runnable objectionable;
    private Type type;

    /**
     * Constructs an object that represents updating the current message
     */
    public CardArgumentRequest(InlineKeyboardButton[][] keyboard, String message) {
        this.keyboard = keyboard;
        this.message = message;
        this.type = Type.UPDATE;
    }

    /**
     * Constructs an object that pauses the turn and starts the objection process
     */
    public CardArgumentRequest(Runnable objectionable) {
        this.objectionable = objectionable;
        this.type = Type.OBJECTION;
    }

    public InlineKeyboardButton[][] getKeyboard() {
        return keyboard;
    }

    public String getMessage() {
        return message;
    }

    public Runnable getObjectionable() {
        return objectionable;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        UPDATE,
        OBJECTION
    }
}
