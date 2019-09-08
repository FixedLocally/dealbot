package me.lkp111138.dealbot.game.cards.actions;

import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;

public class DoubleRentActionCard extends ActionCard {
    @Override
    public String getCardFunctionalTitle() {
        return "Double Rent";
    }

    @Override
    public void use(GamePlayer player, String[] args) {
        player.setDoubleRentBuff(true);
        player.getGame().execute(new SendMessage(player.getTgid(), "The next rent you collect will be doubled."));
        player.promptForCard();
    }

    @Override
    public int currencyValue() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Double the next rent you collect.";
    }

    @Override
    public String toString() {
        return "DoubleRentActionCard{}";
    }
}
