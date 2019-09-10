package me.lkp111138.dealbot.game.cards;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.translation.Translation;

public abstract class ActionCard implements Card {
    protected final Translation translation;

    protected ActionCard(Translation translation) {
        this.translation = translation;
    }

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
        EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), translation.ACTION_CARD_DESC(this.getCardTitle(), this.getDescription()));
        InlineKeyboardButton[][] buttons = new InlineKeyboardButton[3][1];
        int nonce = player.getGame().nextNonce();
        buttons[0][0] = new InlineKeyboardButton(translation.AS_CURRENCY()).callbackData(nonce + ":use_as:money");
        buttons[1][0] = new InlineKeyboardButton(translation.AS_ACTION()).callbackData(nonce + ":use_as:action");
        buttons[2][0] = new InlineKeyboardButton(translation.CANCEL()).callbackData(nonce + ":use_cancel");
        edit.replyMarkup(new InlineKeyboardMarkup(buttons));
        player.getGame().execute(edit);
    }

    public abstract void use(GamePlayer player, String[] args);
}
