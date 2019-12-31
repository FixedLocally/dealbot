package me.lkp111138.dealbot.game.card;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.game.CardArgumentRequest;
import me.lkp111138.dealbot.game.Player;
import me.lkp111138.dealbot.game.card.state.CardState;
import me.lkp111138.dealbot.game.card.state.CardStateInMainDeck;
import me.lkp111138.dealbot.game.card.state.CardStateInPlayerProperty;

import java.util.Map;

public class RainbowWildcardPropertyCard implements PropertyCard {
    private int id;

    public RainbowWildcardPropertyCard(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    private CardState state = new CardStateInMainDeck();

    @Override
    public String getNameKey() {
        return "card.name.property.wildcard";
    }

    @Override
    public String getDescriptionKey() {
        return "card.desc.property.wildcard";
    }

    @Override
    public CardState getState() {
        return state;
    }

    public void setState(CardState state) {
        this.state = state;
    }

    @Override
    public int getCurrencyValue() {
        return 0;
    }

    @Override
    public CardArgumentRequest execute(DealBot bot, Player player, String[] arg) {
        if (arg.length == 0) {
            Map<Integer, Integer> counts = player.getPropertyCounts();
            InlineKeyboardButton[][] buttons = new InlineKeyboardButton[10][1];
            for (int i = 0; i < 10; i++) {
                buttons[i][0] = new InlineKeyboardButton(bot.translate(player.getUserId(), "game.property.colour." + i) + String.format("(%d / %d)", counts.get(i), PropertyCard.propertySetCounts[i]))
                        .callbackData("arg:" + i);
            }
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(buttons);
            return new CardArgumentRequest(keyboard, bot.translate(player.getUserId(), "game.choose_wildcard_colour"));
        }
        int colour = Integer.parseInt(arg[0]);
        setState(new CardStateInPlayerProperty(player, colour));
        return null;
    }
}
