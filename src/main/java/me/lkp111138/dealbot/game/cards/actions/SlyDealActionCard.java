package me.lkp111138.dealbot.game.cards.actions;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;
import me.lkp111138.dealbot.game.cards.Card;
import me.lkp111138.dealbot.game.cards.PropertyCard;
import me.lkp111138.dealbot.game.cards.WildcardPropertyCard;
import me.lkp111138.dealbot.translation.Translation;

import java.util.ArrayList;
import java.util.List;

/**
 * The 'Deal Breaker' card. Takes one complete set of property from an opponent.
 */
public class SlyDealActionCard extends ActionCard {
    public SlyDealActionCard(Translation translation) {
        super(translation);
    }

    @Override
    public int currencyValue() {
        return 3;
    }

    @Override
    public String getDescription() {
        return translation.SLY_DEAL_DESC();
    }

    @Override
    public String getCardFunctionalTitle() {
        return translation.SLY_DEAL();
    }

    @Override
    public void use(GamePlayer player, String[] args) {
        int nonce = player.getGame().nextNonce();
        if (args.length == 0) {
            // choose player
            playerChooser(player, nonce, translation.CANCEL(), translation.SLY_DEAL_CHOOSE_PLAYER());
        }
        if (args.length == 1) {
            // player chosen, choose property
            propertyChooser(args, player, nonce, translation);
        }
        if (args.length == 3) {
            // ask if they were to say no about us stealing
            int index = Integer.parseInt(args[0]);
            int group = Integer.parseInt(args[1]);
            int order = Integer.parseInt(args[2]);
            GamePlayer victim = player.getGame().getGamePlayers().get(index);
            Card card = victim.getPropertyDecks().get(group).get(order);
            EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), translation.YOU_HAVE_USED_AGAINST(getCardFunctionalTitle(), victim.getName()));
            player.getGame().execute(edit);
            SendMessage send = new SendMessage(player.getGame().getGid(), translation.SOMEONE_HAVE_USED_AGAINST(player.getName(), getCardFunctionalTitle(), victim.getName()));
            player.getGame().execute(send);
            victim.promptSayNo(0, translation.SLY_DEAL_SAY_NO_PROMPT(player.getName(), group, card.getCardTitle()), () -> {
                DealBot.triggerAchievement(victim.getTgid(), DealBot.Achievement.WHERE_DID_IT_GO);
                List<Card> props = player.getPropertyDecks().getOrDefault(group, new ArrayList<>());
                props.add(card);
                player.getPropertyDecks().put(group, props);
                victim.removeProperty(card, group);
                player.getGame().resumeTurn();
            }, player.getGame()::resumeTurn, player);
        }
    }

    static void playerChooser(GamePlayer player, int nonce, String cancel, String s) {
        List<GamePlayer> players = player.getGame().getGamePlayers();
        InlineKeyboardButton[][] buttons = new InlineKeyboardButton[players.size()][1];
        int i = 0;
        for (int j = 0; j < players.size(); j++) {
            GamePlayer gamePlayer = players.get(j);
            if (gamePlayer == player) {
                continue;
            }
            buttons[i++][0] = new InlineKeyboardButton(gamePlayer.getName()).callbackData(nonce + ":card_arg:" + j);
        }
        buttons[i][0] = new InlineKeyboardButton(cancel).callbackData(nonce + ":use_cancel");
        EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), s);
        edit.replyMarkup(new InlineKeyboardMarkup(buttons));
        player.getGame().execute(edit);
    }

    static void propertyChooser(String[] args, GamePlayer player, int nonce, Translation translation) {
        int index = Integer.parseInt(args[0]);
        GamePlayer victim = player.getGame().getGamePlayers().get(index);
        List<InlineKeyboardButton[]> buttons = new ArrayList<>();
        for (Integer group : victim.getPropertyDecks().keySet()) {
            if (PropertyCard.realCount(victim.getPropertyDecks().get(group)) >= PropertyCard.propertySetCounts[group]) {
                // is a full set
                continue;
            }
            List<Card> get = victim.getPropertyDecks().get(group);
            for (int i = 0; i < get.size(); i++) {
                Card card = get.get(i);
                if (card instanceof WildcardPropertyCard) {
                    buttons.add(new InlineKeyboardButton[]{new InlineKeyboardButton("[" + translation.PROPERTY_GROUP(group) + "] " + card.getCardTitle())
                            .callbackData(nonce + ":card_arg:" + index + ":" + group + ":" + i)});
                } else {
                    buttons.add(new InlineKeyboardButton[]{new InlineKeyboardButton(card.getCardTitle())
                            .callbackData(nonce + ":card_arg:" + index + ":" + group + ":" + i)});
                }
            }
        }
        buttons.add(new InlineKeyboardButton[]{new InlineKeyboardButton(translation.CANCEL()).callbackData(nonce + ":use_cancel")});
        EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), translation.FORCED_DEAL_CHOOSE_TARGET());
        edit.replyMarkup(new InlineKeyboardMarkup(buttons.toArray(new InlineKeyboardButton[0][0])));
        player.getGame().execute(edit);
    }
}
