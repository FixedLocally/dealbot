package me.lkp111138.dealbot.game.cards.actions;

import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;
import me.lkp111138.dealbot.translation.Translation;

public class DoubleRentActionCard extends ActionCard {
    protected DoubleRentActionCard(Translation translation) {
        super(translation);
    }

    @Override
    public String getCardFunctionalTitle() {
        return translation.DBL_RENT();
    }

    @Override
    public void use(GamePlayer player, String[] args) {
        player.setDoubleRentBuff(true);
        player.getGame().execute(new SendMessage(player.getTgid(), "The next rent you collect in this turn will be doubled."));
        player.getGame().execute(new SendMessage(player.getGame().getGid(),
                player.getName() + " has used " + getCardFunctionalTitle()));
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
