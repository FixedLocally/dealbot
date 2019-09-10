package me.lkp111138.dealbot.game.cards.actions;

import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;
import me.lkp111138.dealbot.translation.Translation;

public class ItsMyBirthdayActionCard extends ActionCard {
    public ItsMyBirthdayActionCard(Translation translation) {
        super(translation);
    }

    @Override
    public String getCardFunctionalTitle() {
        return translation.ITS_MY_BDAY();
    }

    @Override
    public void use(GamePlayer player, String[] args) {
        player.getGame().collectRentFromAll(2, 10);
        EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(),
                translation.COLLECTING_BDAY());
        player.getGame().execute(edit);
        SendMessage send = new SendMessage(player.getTgid(), translation.YOU_HAVE_USED(getCardFunctionalTitle()));
        player.getGame().execute(send);
        send = new SendMessage(player.getGame().getGid(), translation.SOMEONE_HAVE_USED(player.getName(), getCardFunctionalTitle()));
        player.getGame().execute(send);
    }

    @Override
    public int currencyValue() {
        return 2;
    }

    @Override
    public String getDescription() {
        return translation.BDAY_DESC();
    }

    @Override
    public String toString() {
        return "ItsMyBirthdayActionCard{}";
    }
}
