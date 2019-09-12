package me.lkp111138.dealbot.game.cards;

import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.translation.Translation;

public class JustSayNoCard extends ActionCard {
    private final String title;
    private final Translation translation;

    public JustSayNoCard(Translation translation) {
        super(translation);
        this.translation = translation;
        this.title = translation.JUST_SAY_NO();
    }

    @Override
    public int currencyValue() {
        return 4;
    }

    @Override
    public String getCardFunctionalTitle() {
        return title;
    }

    @Override
    public void use(GamePlayer player, String[] args) {
        player.addMove();
        player.addHand(this);
        player.getGame().removeFromUsed(this);
        player.promptForCard();
    }

    @Override
    public String getDescription() {
        return translation.JUST_SAY_NO_DESC();
    }
}
