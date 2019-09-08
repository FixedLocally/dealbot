package me.lkp111138.dealbot.game.cards.actions;

import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;

public class ItsMyBirthdayActionCard extends ActionCard {
    @Override
    public String getCardFunctionalTitle() {
        return "It's my birthday!";
    }

    @Override
    public void use(GamePlayer player, String[] args) {
        player.getGame().collectRentFromAll(2, 10);
        SendMessage send = new SendMessage(player.getTgid(), "You have used " + getCardFunctionalTitle());
        player.getGame().execute(send);
        send = new SendMessage(player.getGame().getGid(), player.getName() + " has used " + getCardFunctionalTitle());
        player.getGame().execute(send);
    }

    @Override
    public int currencyValue() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "It's your birthday! Everyone pays you $ 2M as a gift.";
    }

    @Override
    public String toString() {
        return "ItsMyBirthdayActionCard{}";
    }
}
