package me.lkp111138.dealbot.game.card.action;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.game.CardArgumentRequest;
import me.lkp111138.dealbot.game.Player;
import me.lkp111138.dealbot.game.card.Card;
import me.lkp111138.dealbot.game.card.state.CardStateInPlayerCurrency;

public abstract class ActionCard implements Card {
    @Override
    public final CardArgumentRequest execute(DealBot bot, Player player, String[] arg) {
        switch (arg.length) {
            case 0:
                String message = String.format("%s\n%s%s",
                        bot.translate(player.getUserId(), "game.action_card_prompt", bot.translate(player.getUserId(), getNameKey())),
                        bot.translate(player.getUserId(), "game.card_desc"), bot.translate(player.getUserId(), getDescriptionKey()));
                InlineKeyboardButton[][] buttons = new InlineKeyboardButton[2][1];
                buttons[0][0] = new InlineKeyboardButton(bot.translate(player.getUserId(), "card.use_as_action"))
                        .callbackData("arg:action");
                buttons[1][0] = new InlineKeyboardButton(bot.translate(player.getUserId(), "card.use_as_currency"))
                        .callbackData("arg:money");
                return new CardArgumentRequest(buttons, message);
            case 1:
                if (arg[0].equals("action")) {
                    String[] _arg = new String[arg.length - 1];
                    System.arraycopy(arg, 1, _arg, 0, _arg.length);
                    return executeAction(bot, player, _arg);
                } else {
                    setState(new CardStateInPlayerCurrency(player));
                    return null;
                }
            default:
                String[] _arg = new String[arg.length - 1];
                System.arraycopy(arg, 1, _arg, 0, _arg.length);
                return executeAction(bot, player, _arg);
        }
    }

    /**
     * Runs the action corresponds to this card
     * @param bot The bot instance
     * @param player The user of this card
     * @param arg Arguments passed to this card
     * @return An inline keyboard if the card needs further arguments or null
     */
    abstract CardArgumentRequest executeAction(DealBot bot, Player player, String[] arg);
}
