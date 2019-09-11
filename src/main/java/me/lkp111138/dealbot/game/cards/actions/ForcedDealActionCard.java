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
public class ForcedDealActionCard extends ActionCard {
    public ForcedDealActionCard(Translation translation) {
        super(translation);
    }

    public int currencyValue() {
        return 3;
    }

    @Override
    public String getDescription() {
        return translation.FORCED_DEAL_DESC();
    }

    @Override
    public String getCardFunctionalTitle() {
        return translation.FORCED_DEAL();
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
            buttons[i][0] = new InlineKeyboardButton(translation.CANCEL()).callbackData(nonce + ":use_cancel");
            EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), translation.FORCED_DEAL_TARGET());
            edit.replyMarkup(new InlineKeyboardMarkup(buttons));
            player.getGame().execute(edit);
        }
        if (args.length == 1) {
            // player chosen, choose property from them
            int index = Integer.parseInt(args[0]);
            GamePlayer victim = player.getGame().getGamePlayers().get(index);
            List<InlineKeyboardButton[]> buttons = new ArrayList<>();
            for (Integer group : victim.getPropertyDecks().keySet()) {
                if (victim.getPropertyDecks().get(group).size() >= PropertyCard.propertySetCounts[group]) {
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
        if (args.length == 3) {
            // property chosen, choose property from self
            int index = Integer.parseInt(args[0]);
            List<InlineKeyboardButton[]> buttons = new ArrayList<>();
            for (Integer group : player.getPropertyDecks().keySet()) {
                if (player.getPropertyDecks().get(group).size() >= PropertyCard.propertySetCounts[group]) {
                    // is a full set
                    continue;
                }
                List<Card> get = player.getPropertyDecks().get(group);
                for (int i = 0; i < get.size(); i++) {
                    Card card = get.get(i);
                    if (card instanceof WildcardPropertyCard) {
                        buttons.add(new InlineKeyboardButton[]{new InlineKeyboardButton("[" + translation.PROPERTY_GROUP(group) + "] " + card.getCardTitle())
                                .callbackData(nonce + ":card_arg:" + index + ":" + args[1] + ":" + args[2] + ":" + group + ":" + i)});
                    } else {
                        buttons.add(new InlineKeyboardButton[]{new InlineKeyboardButton(card.getCardTitle())
                                .callbackData(nonce + ":card_arg:" + index + ":" + args[1] + ":" + args[2] + ":" + group + ":" + i)});
                    }
                }
            }
            buttons.add(new InlineKeyboardButton[]{new InlineKeyboardButton(translation.CANCEL()).callbackData(nonce + ":use_cancel")});
            EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), translation.FORCED_DEAL_CHOOSE_GIVE());
            edit.replyMarkup(new InlineKeyboardMarkup(buttons.toArray(new InlineKeyboardButton[0][0])));
            player.getGame().execute(edit);
        }
        if (args.length == 5) {
            // ask if they were to say no about us force dealing
            int index = Integer.parseInt(args[0]);
            int group = Integer.parseInt(args[1]);
            int order = Integer.parseInt(args[2]);
            int selfGroup = Integer.parseInt(args[3]);
            int selfOrder = Integer.parseInt(args[4]);
            GamePlayer victim = player.getGame().getGamePlayers().get(index);
            Card card = victim.getPropertyDecks().get(group).get(order);
            Card selfCard = player.getPropertyDecks().get(selfGroup).get(selfOrder);
            EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), translation.YOU_HAVE_USED_AGAINST(getCardFunctionalTitle(), victim.getName()));
            player.getGame().execute(edit);
            SendMessage send = new SendMessage(player.getGame().getGid(), translation.SOMEONE_HAVE_USED_AGAINST(player.getName(), getCardFunctionalTitle(), victim.getName()));
            player.getGame().execute(send);
            DealBot.triggerAchievement(victim.getTgid(), DealBot.Achievement.NICE_DEAL_WITH_U);
            victim.promptSayNo(translation.FORCED_DEAL_SAY_NO_PROMPT(player, card, group, selfCard), () -> {
                System.out.println("running forced deal");
                List<Card> props = player.getPropertyDecks().getOrDefault(group, new ArrayList<>());
                props.add(card);
                player.getPropertyDecks().put(group, props);
                victim.removeProperty((PropertyCard) card, group);
                victim.addProperty((PropertyCard) selfCard, ((PropertyCard) selfCard).getGroup());
                player.removeProperty((PropertyCard) selfCard, ((PropertyCard) selfCard).getGroup());
                player.promptForCard();
            }, () -> {
                // tell the sender its objected
                player.getGame().log("Objection!");
                SendMessage _send = new SendMessage(player.getTgid(),  translation.VICTIM_SAID_NO(victim.getName()));
                player.getGame().execute(_send);
                player.promptForCard();
            }, player);
        }
    }
}
