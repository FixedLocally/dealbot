package me.lkp111138.dealbot.game.cards;

import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.actions.HotelActionCard;
import me.lkp111138.dealbot.game.cards.actions.HouseActionCard;

import java.util.List;

public class PropertyCard implements Card {
    protected final int currencyValue;
    protected final String title;
    protected int group;

    // there are currently 10 groups of properties
    public static final int[] propertySetCounts = {2, 3, 3, 3, 3, 3, 3, 2, 4, 2};
    public static final int[][] propertyRents = {
            {1, 2},
            {1, 2, 3},
            {1, 2, 4},
            {1, 3, 5},
            {2, 3, 6},
            {2, 4, 6},
            {2, 4, 7},
            {3, 8},
            {1, 2, 3, 4},
            {1, 2}
    };

    public static int getRent(int group, List<Card> props) {
        int count = 0;
        int realCount = 0;
        int extra = 0;
        for (Card prop : props) {
            if (prop instanceof PropertyCard) {
                ++count;
                ++realCount;
            }
            if (prop instanceof WildcardPropertyCard && ((WildcardPropertyCard) prop).getGroups().length == 10) {
                --realCount;
            }
            if (prop instanceof HouseActionCard) {
                extra += 3;
            }
            if (prop instanceof HotelActionCard) {
                extra += 4;
            }
        }
        if (realCount == 0) {
            return 0;
        }
        if (count <= 0) {
            return 0;
        }
        if (count >= propertyRents[group].length) {
            return propertyRents[group][propertyRents[group].length - 1] + extra;
        }
        // those non full sets do not enjoy yhr benefits from houses and hotels
        return propertyRents[group][count - 1];
    }

    public PropertyCard(int currencyValue, String title, int group) {
        this.currencyValue = currencyValue;
        this.title = title;
        this.group = group;
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
        return false;
    }

    @Override
    public boolean isAction() {
        return false;
    }

    @Override
    public boolean isProperty() {
        return true;
    }

    @Override
    public String getCardTitle() {
        return "[" + group + "] " + title;
    }

    @Override
    public String getDescription() {
        return title;
    }

    @Override
    public void execute(GamePlayer player, String[] args) {
        if (player.addProperty(this, group)) {
            player.promptForCard();
            SendMessage send = new SendMessage(player.getTgid(), "You have placed " +
                    getCardTitle() + " in your properties.");
            player.getGame().execute(send);
            send = new SendMessage(player.getGame().getGid(), player.getName() + " have placed " +
                    getCardTitle() + " in their properties.");
            player.getGame().execute(send);
        } else {
            // deck full
            player.addHand(this);
            player.addMove();
            SendMessage send = new SendMessage(player.getTgid(), "This group is full!");
            player.getGame().execute(send);
        }
    }

    public int getGroup() {
        return group;
    }

    @Override
    public String toString() {
        return "PropertyCard{" +
                "title='" + title + '\'' +
                ", group=" + group +
                '}';
    }
}
