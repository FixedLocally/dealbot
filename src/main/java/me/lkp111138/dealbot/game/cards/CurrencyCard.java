package me.lkp111138.dealbot.game.cards;

import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.game.GamePlayer;

public class CurrencyCard implements Card {
    private final int currencyValue;
    private final String title;

    public CurrencyCard(int currencyValue, String title) {
        this.currencyValue = currencyValue;
        this.title = title;
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
        SendMessage send = new SendMessage(player.getTgid(), "You have deposited " +
                getCardTitle() + " into your bank.");
        player.getGame().execute(send);
        send = new SendMessage(player.getGame().getGid(), player.getName() + " have deposited " +
                getCardTitle() + " into their bank.");
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
