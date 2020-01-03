package me.lkp111138.dealbot.game.card;

import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.game.CardArgumentRequest;
import me.lkp111138.dealbot.game.Player;
import me.lkp111138.dealbot.game.card.state.CardState;
import me.lkp111138.dealbot.game.card.state.CardStateInMainDeck;

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
        return null;
    }
}
