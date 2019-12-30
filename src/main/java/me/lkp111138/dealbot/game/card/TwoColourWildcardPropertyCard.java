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

public class TwoColourWildcardPropertyCard implements PropertyCard {
    private int id;
    private int colour1;
    private int colour2;
    private int value;
    private CardState state = new CardStateInMainDeck();

    public TwoColourWildcardPropertyCard(int id, int colour1, int colour2, int value) {
        this.id = id;
        this.colour1 = colour1;
        this.colour2 = colour2;
        this.value = value;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getNameKey() {
        return "card.name.property.wildcard" + colour1 + colour2;
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
        return value;
    }

    @Override
    public CardArgumentRequest execute(DealBot bot, Player player, String[] arg) {
        if (arg.length == 0) {
            Map<Integer, Integer> counts = player.getPropertyCounts();
            InlineKeyboardButton[][] buttons = new InlineKeyboardButton[2][1];
            buttons[0][0] = new InlineKeyboardButton(bot.translate(player.getUserId(), "game.property.colour." + colour1) + String.format("(%d / %d)", counts.get(colour1), PropertyCard.propertySetCounts[colour1]))
                    .callbackData("arg:" + colour1);
            buttons[1][0] = new InlineKeyboardButton(bot.translate(player.getUserId(), "game.property.colour." + colour2) + String.format("(%d / %d)", counts.get(colour2), PropertyCard.propertySetCounts[colour2]))
                    .callbackData("arg:" + colour2);
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(buttons);
            return new CardArgumentRequest(keyboard, bot.translate(player.getUserId(), "game.choose_wildcard_colour"));
        }
        int colour = Integer.parseInt(arg[0]);
        setState(new CardStateInPlayerProperty(player, colour));
        return null;
    }
}
