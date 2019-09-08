package me.lkp111138.dealbot.game.cards.actions;

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
    }

    @Override
    public int currencyValue() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "It's your birthday! Everyone pays you $ 2M as a gift.";
    }
}
