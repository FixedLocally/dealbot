package me.lkp111138.dealbot.game.card.action;

import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.game.CardArgumentRequest;
import me.lkp111138.dealbot.game.Player;
import me.lkp111138.dealbot.game.card.Card;
import me.lkp111138.dealbot.game.card.state.CardState;
import me.lkp111138.dealbot.game.card.state.CardStateInMainDeck;

public class GoPassActionCard extends ActionCard {
    private int id;
    private CardState state = new CardStateInMainDeck();

    public GoPassActionCard(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getNameKey() {
        return "card.name.go_pass";
    }

    @Override
    public String getDescriptionKey() {
        return "card.desc.go_pass";
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
        return 1;
    }

    @Override
    CardArgumentRequest executeAction(DealBot bot, Player player, String[] arg) {
        Card card1 = player.getGame().draw();
        Card card2 = player.getGame().draw();
        if (card1 != null) {
            String msg = bot.translate(player.getUserId(), "game.have_drawn");
            msg += "\n- " + bot.translate(player.getUserId(), card1.getNameKey());
            if (card2 != null) {
                msg += "\n- " + bot.translate(player.getUserId(), card2.getNameKey());
            }
            bot.execute(new SendMessage(player.getUserId(), msg));
        }
        return null;
    }
}
