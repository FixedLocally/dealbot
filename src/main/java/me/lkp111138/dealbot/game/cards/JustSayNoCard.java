package me.lkp111138.dealbot.game.cards;

import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.translation.Translation;

public class JustSayNoCard implements Card {
    private final String title;
    private final Translation translation;

    public JustSayNoCard(Translation translation) {
        this.translation = translation;
        this.title = translation.JUST_SAY_NO();
    }

    @Override
    public boolean isObjectable() {
        return true;
    }

    @Override
    public int currencyValue() {
        return 4;
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
        player.addMove();
        player.addHand(this);
        player.promptForCard();
    }

    @Override
    public String getDescription() {
        return translation.JUST_SAY_NO_DESC();
    }
}
