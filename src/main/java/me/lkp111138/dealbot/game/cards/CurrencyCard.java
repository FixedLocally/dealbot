package me.lkp111138.dealbot.game.cards;

import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.translation.Translation;

public class CurrencyCard implements Card {
    private final int currencyValue;
    private final String title;
    private final Translation translation;

    public CurrencyCard(int currencyValue, String title, Translation translation) {
        this.currencyValue = currencyValue;
        this.title = title;
        this.translation = translation;
    }

    @Override
    public boolean isObjectable() {
        return false;
    }

    @Override
    public int currencyValue() {
        return currencyValue;
    }

    @Override
    public boolean isCurrency() {
        return true;
    }

    @Override
    public boolean isAction() {
        return false;
    }

    @Override
    public boolean isProperty() {
        return false;
    }

    @Override
    public String getCardTitle() {
        return title;
    }

    @Override
    public void execute(GamePlayer player, String[] args) {
        player.addCurrency(this);
        player.promptForCard();
        SendMessage send = new SendMessage(player.getTgid(), translation.YOU_DEPOSITED(getCardTitle()));
        player.getGame().execute(send);
        send = new SendMessage(player.getGame().getGid(), translation.SOMEONE_DEPOSITED(player.getName(), getCardTitle()));
        player.getGame().execute(send);
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String toString() {
        return "CurrencyCard{" +
                "currencyValue=" + currencyValue +
                '}';
    }
}
