package me.lkp111138.dealbot.game.cards;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import me.lkp111138.dealbot.game.GamePlayer;

public abstract class ActionCard implements Card {
    @Override
    public final boolean isObjectable() {
        return true;
    }

    @Override
    public final boolean isCurrency() {
        return true;
    }

    @Override
    public final boolean isAction() {
        return true;
    }

    @Override
    public final boolean isProperty() {
        return false;
    }

    @Override
    public final String getCardTitle() {
        return getCardFunctionalTitle() + " [$ " + currencyValue() + "M]";
    }

    public abstract String getCardFunctionalTitle();

    @Override
    public final void execute(GamePlayer player, String[] args) {
        // use as $ or action card?
        EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), String.format("Use as %s currency or an action?\nCard description: %s", this.getCardTitle(), this.getDescription()));
        InlineKeyboardButton[][] buttons = new InlineKeyboardButton[3][1];
        int nonce = player.getGame().nextNonce();
        buttons[0][0] = new InlineKeyboardButton("As currency").callbackData(nonce + ":use_as:money");
        buttons[1][0] = new InlineKeyboardButton("As action").callbackData(nonce + ":use_as:action");
        buttons[2][0] = new InlineKeyboardButton("Cancel").callbackData(nonce + ":use_cancel");
        edit.replyMarkup(new InlineKeyboardMarkup(buttons));
        player.getGame().execute(edit);
    }

    public abstract void use(GamePlayer player, String[] args);
}
