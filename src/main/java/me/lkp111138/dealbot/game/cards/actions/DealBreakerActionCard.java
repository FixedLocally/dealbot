package me.lkp111138.dealbot.game.cards.actions;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;
import me.lkp111138.dealbot.game.cards.Card;
import me.lkp111138.dealbot.game.cards.PropertyCard;
import me.lkp111138.dealbot.translation.Translation;

import java.util.ArrayList;
import java.util.List;

/**
 * The 'Deal Breaker' card. Takes one complete set of property from an opponent.
 */
public class DealBreakerActionCard extends ActionCard {
    public DealBreakerActionCard(Translation translation) {
        super(translation);
    }

    @Override
    public int currencyValue() {
        return 5;
    }

    @Override
    public String getDescription() {
        return "Takes a complete set of property from a player, including any buildings.";
    }

    @Override
    public String getCardFunctionalTitle() {
        return translation.DEAL_BREAKER();
    }

    @Override
    public void use(GamePlayer player, String[] args) {
        int nonce = player.getGame().nextNonce();
        if (args.length == 0) {
            // choose player
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
            buttons[i][0] = new InlineKeyboardButton("Cancel").callbackData(nonce + ":use_cancel");
            EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), "Whose deal to break?");
            edit.replyMarkup(new InlineKeyboardMarkup(buttons));
            player.getGame().execute(edit);
        }
        if (args.length == 1) {
            // player chosen, choose property
            int index = Integer.parseInt(args[0]);
            GamePlayer victim = player.getGame().getGamePlayers().get(index);
            List<InlineKeyboardButton[]> buttons = new ArrayList<>();
            for (Integer group : victim.getPropertyDecks().keySet()) {
                if (victim.getPropertyDecks().get(group).size() >= PropertyCard.propertySetCounts[group]) {
                    // is a full set
                    buttons.add(new InlineKeyboardButton[]{new InlineKeyboardButton("Group " + group)
                            .callbackData(nonce + ":card_arg:" + index + ":" + group)});
                }
            }
            buttons.add(new InlineKeyboardButton[]{new InlineKeyboardButton("Cancel").callbackData(nonce + ":use_cancel")});
            EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), "Which deal to break?");
            edit.replyMarkup(new InlineKeyboardMarkup(buttons.toArray(new InlineKeyboardButton[0][0])));
            player.getGame().execute(edit);
        }
        if (args.length == 2) {
            // ask if they were to say no about us breaking the deal
            int index = Integer.parseInt(args[0]);
            int group = Integer.parseInt(args[1]);
            GamePlayer victim = player.getGame().getGamePlayers().get(index);
            EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), "You have used Deal breaker against " + victim.getName());
            player.getGame().execute(edit);
            victim.promptSayNo(String.format("%s has used Deal Breaker against your group %s property. Would you like to say no?", player.getName(), group), () -> {
                List<Card> cards = victim.getPropertyDecks().get(group);
                List<Card> props = player.getPropertyDecks().getOrDefault(group, new ArrayList<>());
                props.addAll(cards);
                player.getPropertyDecks().put(group, props);
                victim.getPropertyDecks().remove(group);
                player.promptForCard();
            }, () -> {
                // tell the sender its objected
                player.getGame().log("Objection!");
                SendMessage send = new SendMessage(player.getTgid(),  victim.getName() + " has used Just Say No!");
                player.getGame().execute(send);
                send = new SendMessage(player.getTgid(), victim.getName() + " has used Just Say No!");
                player.getGame().execute(send);
                player.promptForCard();
            });
        }
    }
}
