package me.lkp111138.dealbot.game.card;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.game.CardArgumentRequest;
import me.lkp111138.dealbot.game.Player;
import me.lkp111138.dealbot.game.card.state.CardState;
import me.lkp111138.dealbot.game.card.state.CardStateInMainDeck;
import me.lkp111138.dealbot.game.card.state.CardStateInPlayerCurrency;

public class JustSayNoCard implements Card {
    private int id;
    private CardState state = new CardStateInMainDeck();

    public JustSayNoCard(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getNameKey() {
        return "card.name.objection";
    }

    @Override
    public String getDescriptionKey() {
        return "card.desc.objection";
    }

    @Override
    public CardState getState() {
        return state;
    }

    @Override
    public void setState(CardState state) {
        this.state = state;
    }

    @Override
    public int getCurrencyValue() {
        return 4;
    }

    @Override
    public CardArgumentRequest execute(DealBot bot, Player player, String[] arg) {
        if (arg.length == 0) {
            String message = String.format("%s\n%s%s",
                    bot.translate(player.getUserId(), "game.objection_card_prompt", bot.translate(player.getUserId(), getNameKey())),
                    bot.translate(player.getUserId(), "game.card_desc"), bot.translate(player.getUserId(), getDescriptionKey()));
            InlineKeyboardButton[][] buttons = new InlineKeyboardButton[1][1];
            buttons[0][0] = new InlineKeyboardButton(bot.translate(player.getUserId(), "card.use_as_currency"))
                    .callbackData("arg:money");
            return new CardArgumentRequest(buttons, message);
        } else {
            setState(new CardStateInPlayerCurrency(player));
            return null;
        }
    }
}
