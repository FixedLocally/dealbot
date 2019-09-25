package me.lkp111138.dealbot.translation;

import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.Card;

public class HongKongChinese extends TraditionalChinese {
    @Override
    public String BOT_NAME() {
        return "jokedealbot";
    }
    @Override
    public String ERROR() {
        return "出咗個問題：";
    }
    @Override
    public String JOIN_SUCCESS() {
        return "你成功加入咗 <b>%s</b> 嘅遊戲！遊戲編號：%d";
    }
    @Override
    public String BACK_TO() {
        return "翻去 ";
    }
    @Override
    public String JOINED_ANNOUNCEMENT() {
        return "[ <a href=\"tg://user?id=%d\">%s</a> ] 加入咗遊戲！ 已經有 <b>%d</b> 個玩家，最多可以 <b>5</b> 個。";
    }
    @Override
    public String START_ME_FIRST() {
        return "撳下面個制先！";
    }
    @Override
    public String START_ME() {
        return "撳！";
    }
    @Override
    public String EXTENDED_ANNOUNCEMENT() {
        return "加入時間延長咗30秒，仲有 %d 秒。 /join";
    }
    @Override
    public String GAME_STARTING_ANNOUNCEMENT() {
        return "遊戲開始緊，請等等...";
    }
    @Override
    public String PROPERTY_NAME(int index) {
        return super.PROPERTY_NAME(index);
    }
    @Override
    public String NO_OF_FULL_SETS(int sets) {
        return super.NO_OF_FULL_SETS(sets);
    }
    @Override
    public String WILD_CARD() {
        return super.WILD_CARD();
    }
    @Override
    public String WILD_CARD(int g1, int g2) {
        return super.WILD_CARD(g1, g2);
    }
    @Override
    public String ITS_MY_BDAY() {
        return super.ITS_MY_BDAY();
    }
    @Override
    public String DEBT_COLLECTOR() {
        return super.DEBT_COLLECTOR();
    }
    @Override
    public String GO_PASS() {
        return super.GO_PASS();
    }
    @Override
    public String DBL_RENT() {
        return super.DBL_RENT();
    }
    @Override
    public String JUST_SAY_NO() {
        return super.JUST_SAY_NO();
    }
    @Override
    public String DEAL_BREAKER() {
        return super.DEAL_BREAKER();
    }
    @Override
    public String SLY_DEAL() {
        return super.SLY_DEAL();
    }
    @Override
    public String HOUSE() {
        return super.HOUSE();
    }
    @Override
    public String HOTEL() {
        return super.HOTEL();
    }
    @Override
    public String FORCED_DEAL() {
        return super.FORCED_DEAL();
    }
    @Override
    public String WILDCARD_RENT(int g1, int g2) {
        return super.WILDCARD_RENT(g1, g2);
    }
    @Override
    public String RAINBOW_RENT() {
        return super.RAINBOW_RENT();
    }
    @Override
    public String CURRENT_STATE() {
        return super.CURRENT_STATE();
    }
    @Override
    public String CARDS_IN_HAND() {
        return super.CARDS_IN_HAND();
    }
    @Override
    public String CARDS_IN_CURRENCY_DECK() {
        return super.CARDS_IN_CURRENCY_DECK();
    }
    @Override
    public String PROPERTIES() {
        return super.PROPERTIES();
    }
    @Override
    public String PROPERTY_GROUP(int group) {
        return super.PROPERTY_GROUP(group);
    }
    @Override
    public String JUST_SAY_NO_DESC() {
        return "呢張卡可以抵銷一個針對你嘅行動，包括其他作出反對。";
    }
    @Override
    public String PASS() {
        return "Pass";
    }
    @Override
    public String MANAGE_CARD_MENU() {
        return "管理多功能物業牌同建築物";
    }
    @Override
    public String CHOOSE_AN_ACTION(int remaining, int secs) {
        return String.format("揀一個行動（仲有 %d 個），你有 %d 秒嘅時間！", remaining, secs);
    }
    @Override
    public String SELF_CURRENCY_DECK(int count, int total) {
        return super.SELF_CURRENCY_DECK(count, total);
    }
    @Override
    public String JUST_SAY_NO_BTN(long count) {
        return super.JUST_SAY_NO_BTN(count);
    }
    @Override
    public String NO() {
        return super.NO();
    }
    @Override
    public String PAYMENT_COLLECTION_MESSAGE_SAY_NO(int group, String collector, int amount) {
        if (group < 10) {
            return String.format("%s 向你收緊 $ %dM 作為 %s 嘅租金！你想唔想作出反對？",
                    collector, amount, PROPERTY_GROUP(group));
        }
        if (group == 10) {
            return String.format("%s 向你收緊 $ %dM 做生日禮物！你想唔想作出反對？",
                    collector, amount);
        }
        if (group == 11) {
            return String.format("%s 向你收緊 $ %dM 嘅債務！你想唔想作出反對？",
                    collector, amount);
        }
        return "";
    }
    @Override
    public String PAYMENT_COLLECTION_MESSAGE(int group, String collector, int amount, int secs) {
        if (group < 10) {
            return String.format("%s 向你收緊 $ %dM 作為 %s 嘅租金！你有 %d 秒揀點樣畀。",
                    collector, amount, PROPERTY_GROUP(group), secs);
        }
        if (group == 10) {
            return String.format("%s 向你收緊 $ %dM 做生日禮物！你有 %d 秒揀點樣畀。",
                    collector, amount, secs);
        }
        if (group == 11) {
            return String.format("%s 向你收緊 $ %dM 嘅債務！你有 %d 秒揀點樣畀。",
                    collector, amount, secs);
        }
        return "";
    }
    @Override
    public String PAY(int value) {
        return String.format("交錢（$ %dM）", value);
    }
    @Override
    public String CHOOSE_CARD_TO_MANAGE() {
        return "揀一張牌嚟管理：";
    }
    @Override
    public String CHOOSE_RELOCATE() {
        return "揀個位嚟擺呢張牌：";
    }
    @Override
    public String GROUP_FULL() {
        return "呢組物業滿咗";
    }
    @Override
    public String PAYMENT_TOO_LOW() {
        return "呢度唔夠錢";
    }
    @Override
    public String SAID_NO() {
        return "你用咗作出反對";
    }
    @Override
    public String SB_SAID_NO(String name) {
        return name + " 用咗作出反對";
    }
    @Override
    public String SAID_YES() {
        return "你冇用到作出反對";
    }
    @Override
    public String PAYMENT_THX() {
        return "多謝交錢 - ";
    }
    @Override
    public String DISPOSE_CARD(int remaining) {
        return String.format("請棄一啲牌 (仲有 %d 張)", remaining);
    }
    @Override
    public String BUILD_THIS_ON(String name) {
        return String.format("將呢間 %s 起喺邊一組物業？", name);
    }
    @Override
    public String DEAL_BREAKER_DESC() {
        return "喺一個玩家度攞走一套完整物業，包括任何建築物";
    }
    @Override
    public String WHOSE_DEAL_TO_BREAK() {
        return "要接管誰邊個嘅物業？";
    }
    @Override
    public String VICTIM_SAID_NO(String name) {
        return name + " 用咗作出反對！";
    }

    @Override
    public String DEAL_BREAKER_SAY_NO_PROMPT(String name, int group) {
        return String.format("%s 對你用咗物業接管，想接管你嘅 %s 物業。你想唔想作出反對？", name, PROPERTY_GROUP(group));
    }
    @Override
    public String YOU_HAVE_USED_AGAINST(String card, String against) {
        return "你對 " + against + " 用咗 " + card;
    }
    @Override
    public String YOU_HAVE_USED(String card) {
        return "你用咗 " + card;
    }
    @Override
    public String SOMEONE_HAVE_USED_AGAINST(String user, String card, String against) {
        return user + " 對 " + against + " 用咗 " + card;
    }
    @Override
    public String SOMEONE_HAVE_USED(String user, String card) {
        return user + " 用咗 " + card;
    }
    @Override
    public String DEBT_COLLECTOR_PROMPT(String victim) {
        return String.format("向 %s 收緊 $ 5M 嘅債務", victim);
    }
    @Override
    public String DEBT_COLLECTOR_CHOOSE_PROMPT() {
        return "揀一個玩家嚟還呢個債務。";
    }
    @Override
    public String DEBT_COLLECTOR_DESC() {
        return "從一個玩家收 $ 5M。";
    }
    @Override
    public String DBL_RENT_DESC() {
        return "下次收租嘅時候，金額會計雙倍。";
    }
    @Override
    public String DBL_RENT_MSG(int multiplier) {
        return String.format("呢輪下一次收租嘅時候，金額會 %d 倍計算。", multiplier);
    }
    @Override
    public String FORCED_DEAL_DESC() {
        return "用自己嘅一項物業強制換取另一個玩家的一項物業，但係唔可以係完整一套物業之一。";
    }
    @Override
    public String FORCED_DEAL_TARGET() {
        return "你要同邊個強制交易？";
    }
    @Override
    public String FORCED_DEAL_CHOOSE_TARGET() {
        return "你想要邊張牌？";
    }
    @Override
    public String FORCED_DEAL_CHOOSE_GIVE() {
        return "你要用邊張牌嚟交易？";
    }
    @Override
    public String FORCED_DEAL_SAY_NO_PROMPT(GamePlayer player, Card card, int group, Card selfCard) {
        return String.format("%s 對你用咗強制交易，打算用你 %s 物業裡面嘅 %s 換 %s。你想唔想作出反對？", player.getName(), card.getCardTitle(), PROPERTY_GROUP(group), selfCard.getCardTitle());
    }
    @Override
    public String GO_PASS_DESC() {
        return "從牌堆攞兩張卡";
    }
    @Override
    public String YOU_HAVE_DRAWN() {
        return "你從牌堆抽咗以下嘅牌:\n";
    }
    @Override
    public String BUILDING_DESC(String name, int amt) {
        return String.format("喺你嘅一套完整物業上起 %s 令佢嘅租金增加 $ %dM", name, amt);
    }
    @Override
    public String COLLECTING_BDAY() {
        return "對所有人收集緊生日禮物。";
    }

    @Override
    public String BDAY_DESC() {
        return "今日你生日！所有人都要俾 $ 2M 你作為生日禮物。";
    }

    @Override
    public String RENT_CARD_DESC(int g1, int g2) {
        return String.format("向所有玩家收 %s 或 %s 嘅租金。", PROPERTY_GROUP(g1), PROPERTY_GROUP(g2));
    }

    @Override
    public String RAINBOW_RENT_CARD_DESC() {
        return "向一個玩家收任意一組物業嘅租金";
    }

    @Override
    public String YOU_HAVE_USED_RENT_FOR(String card, int group, int amount) {
        return String.format("你用咗 %s 收 %s 嘅租金，總共 $ %dM。", card, PROPERTY_GROUP(group), amount);
    }

    @Override
    public String SOMEONE_HAVE_USED_RENT_FOR(String name, String card, int group, int amount) {
        return String.format("%s 用咗 %s 收 %s 嘅租金，總共 $ %dM。", name, card, PROPERTY_GROUP(group), amount);
    }

    @Override
    public String YOU_HAVE_USED_RENT_FOR_AGAINST(String card, String victim, int group, int amount) {
        return String.format("你用咗 %1$s 向 %3$s 收取 %2$s 嘅租金，總共 $ %4$dM。", card, PROPERTY_GROUP(group), victim, amount);
    }

    @Override
    public String SOMEONE_HAVE_USED_RENT_FOR_AGAINST(String name, String card, String victim, int group, int amount) {
        return String.format("%1$s 用咗 %2$s 向 %4$s 收取 %3$s 嘅租金，總共 $ %5$dM。", name, card, PROPERTY_GROUP(group), victim, amount);
    }

    @Override
    public String COLLECTING_RENT(String from, int group, int amount) {
        return String.format("向 %s 收取 %s 嘅租金，金額係 $ %dM。", from == null ? "所有人" : from, PROPERTY_GROUP(group), amount);
    }

    @Override
    public String RENT_CHOOSE_GROUP() {
        return "揀一組物業嚟收租：";
    }

    @Override
    public String RENT_CHOOSE_PLAYER() {
        return "揀一個玩家嚟收租：";
    }

    @Override
    public String SLY_DEAL_DESC() {
        return "盜取一個玩家的一項物業，但係唔可以係完整物業之一。";
    }

    @Override
    public String SLY_DEAL_CHOOSE_PLAYER() {
        return "你要盜取邊個嘅物業？";
    }

    @Override
    public String SLY_DEAL_SAY_NO_PROMPT(String name, int group, String target) {
        return String.format("%s 對你用咗盜取，想要你 %s 物業裡面嘅 %s. 你想唔想作出反對？", name, PROPERTY_GROUP(group), target);
    }

    @Override
    public String ACTION_CARD_DESC(String title, String desc) {
        return String.format("將 %s 存入銀行定係作為行動？\n卡牌描述：%s", title, desc);
    }

    @Override
    public String AS_CURRENCY() {
        return super.AS_CURRENCY();
    }

    @Override
    public String AS_ACTION() {
        return super.AS_ACTION();
    }

    @Override
    public String YOU_DEPOSITED(String title) {
        return super.YOU_DEPOSITED(title);
    }

    @Override
    public String SOMEONE_DEPOSITED(String name, String title) {
        return super.SOMEONE_DEPOSITED(name, title);
    }

    @Override
    public String YOU_PLACED_PROP(String title) {
        return "你將 " + title + " 放喺你嘅物業區。";
    }

    @Override
    public String SOMEONE_PLACED_PROP(String name, String title) {
        return name + " 將 " + title + " 放喺佢嘅物業區。";
    }

    @Override
    public String YOU_PLACED_PROP_AS(String title, int group) {
        return "你將 " + title + " 放咗喺你嘅 " + PROPERTY_GROUP(group) + " 物業區。";
    }

    @Override
    public String SOMEONE_PLACED_PROP_AS(String name, String title, int group) {
        return name + " 將 " + title + " 放咗喺 " + PROPERTY_GROUP(group) + " 物業區。";
    }

    @Override
    public String WILDCARD_CHOOSE_GROUP() {
        return "將呢張牌當做咩色嘅物業？";
    }

    @Override
    public String SB_PAID_YOU(String name, String payment) {
        return name + " 俾咗 " + payment + "你";
    }

    @Override
    public String SB_PAID_SB(String name, String payee, String payment) {
        return name + " 俾咗 " + payment + " " + payee;
    }

    @Override
    public String SAID_NO_PROMPT_SAY_NO(String name) {
        return name + " 用咗作出反對！你想唔想用另外一張作出反對嚟抵銷？";
    }

    @Override
    public String SB_DISPOSED(String name, String card) {
        return name + " 棄置咗 " + card;
    }

    @Override
    public String YOU_DISPOSED(String card) {
        return "你棄置咗 " + card;
    }

    @Override
    public String PASS_ANNOUNCEMENT() {
        return "%s 揀咗 Pass";
    }

    @Override
    public String WON_ANNOUNCEMENT(int tgid, String name) {
        return String.format("<a href=\"tg://user?id=%1$s\">%2$s</a> 成功收集三套完整物業，贏咗呢場遊戲！", tgid, name);
    }

    @Override
    public String PASS_TIMEOUT() {
        return super.PASS_TIMEOUT();
    }

    @Override
    public String PASS_CLICK() {
        return super.PASS_CLICK();
    }

    @Override
    public String NEW_GAME_PROMPT() {
        return "\n撳 /play 嚟開新遊戲";
    }
    @Override
    public String GAME_ENDED() {
        return "遊戲完咗";
    }
    @Override
    public String GAME_ENDED_ANNOUNCEMENT() {
        return "遊戲完咗。撳 /play 嚟開新遊戲";
    }
    @Override
    public String AFK_KILL() {
        return "好似所有人都忙緊，遊戲結束！";
    }
    @Override
    public String YOUR_TURN_ANNOUNCEMENT() {
        return "輪到你啦， <a href=\"tg://user?id=%d\">%s</a>，你有 %d 秒出牌！";
    }

    @Override
    public String ME_CMD_PMED() {
        return super.ME_CMD_PMED();
    }

    @Override
    public String PICK_CARDS() {
        return "撳呢度揀牌";
    }
    @Override
    public String JOIN_PROMPT() {
        return "仲有 %d 秒，快啲入嚟一齊玩 /join";
    }
    @Override
    public String NO_GAME_TO_JOIN() {
        return "而家冇人玩緊，撳 /play 開新遊戲";
    }
    @Override
    public String GAME_STARTING() {
        return "遊戲就嚟開始！ 撳 /join 加入。";
    }
    @Override
    public String GAME_STARTED() {
        return "遊戲開始咗！請等呢場完咗再開過";
    }
    @Override
    public String GAME_START_ANNOUNCEMENT() {
        return "[ <a href=\"tg://user?id=%d\">%s</a> ] 開始咗新遊戲！你有 %d 秒加入 /join\n\n遊戲編號：%d";
    }
    @Override
    public String NOTHING_ON_DESK() {
        return "\n檯面上乜都冇。\n";
    }
    @Override
    public String SORT_SUIT() {
        return "按花色排序";
    }
    @Override
    public String SORT_FACE() {
        return "按點數排序";
    }
    @Override
    public String FLEE_ANNOUNCEMENT() {
        return "<a href=\"tg://user?id=%d\">%s</a> 退出咗遊戲！ 仲有 %d 個玩家";
    }
    @Override
    public String MAINT_MODE_NOTICE() {
        return "維修緊，請遲啲再試。";
    }
    @Override
    public String CLOSE() {
        return "關閉";
    }
    @Override
    public String ACHIEVEMENT_UNLOCKED() {
        return "解鎖成就！\n";
    }

    @Override
    public String ACHV_TITLE(DealBot.Achievement ach) {
        return super.ACHV_TITLE(ach);
    }

    @Override
    public String ACHV_DESC(DealBot.Achievement ach) {
        return super.ACHV_DESC(ach);
    }

    @Override
    public String ACHV_MSG(DealBot.Achievement ach) {
        return super.ACHV_MSG(ach);
    }

    @Override
    public String ACHV_UNLOCKED() {
        return "解鎖咗嘅成就：\n";
    }
    @Override
    public String A_TOTAL_OF() {
        return "總共 %d 個";
    }

    @Override
    public String HELP() {
        return "<b>指令列表</b>\n" +
                "\n" +
                "/stats - 顯示統計數據。\n" +
                "/achv - 顯示你已解鎖的成就。\n" +
                "/help - 顯示此列表。\n" +
                "\n" +
                "<b>群組指令</b>\n" +
                "\n" +
                "/play - 開始新遊戲。\n" +
                "/startgame - 與 /play 相同\n" +
                "/join - 加入遊戲。\n" +
                "/flee - 在遊戲開始前離開遊戲。\n" +
                "/extend - 延長加入遊戲階段\n" +
                "\n" +
                "<b>僅限群組管理員</b>\n" +
                "\n" +
                "/config - 在私訊中更改群組遊戲設定。\n" +
                "/setlang - 更改群組語言\n" +
                "\n" +
                "<b>技術指令</b>\n" +
                "\n" +
                "/runinfo - 顯示某些資料。\n" +
                "/ping - 未知的指令。";
    }

    @Override
    public String ACHIEVEMENT_TITLE(String achv_key) {
        return super.ACHIEVEMENT_TITLE(achv_key);
    }

    @Override
    public String ACHIEVEMENT_DESC(String achv_key) {
        switch (achv_key) {
            case "FIRST_GAME":
                return "玩一場遊戲。";
            case "FIRST_WIN":
                return "贏一場遊戲。";
            case "PLAY_WITH_MINT":
                return "個遊戲開發者玩一場。";
            case "FIRST_BLOOD":
                return "輸一場遊戲，同時失去籌碼。";
            case "ROOKIE":
                return "玩 50 場遊戲。";
            case "FAMILIARIZED":
                return "玩 200 場遊戲。";
            case "ADDICTED":
                return "玩 1000 場遊戲。";
            case "AMATEUR":
                return "贏 20 場遊戲。";
            case "ADEPT":
                return "贏 100 場遊戲。";
            case "EXPERT":
                return "贏 500 場遊戲。";
            case "LOSE_IT_ALL":
                return "在一場遊戲中輸 13 張牌。";
            case "DEEP_FRIED":
                return "贏一場遊戲，當中你的對手並未打出任何一張牌。";
            default:
                return achv_key;
        }
    }

    @Override
    public String JOIN_69_PROTEST() {
        return "香港嘅情況日益嚴峻 - 警察肆意攻擊同拘捕市民，" +
                "而政府一直拒絕回應五大訴求。\n" +
                "請幫手去各大社交平台分享 #StandWithHongKong 嚟喚起國際關注。\n" +
                "唔該曬你。" +
                "打 /toggle69 唔再顯示呢個訊息";
    }

    @Override
    public String GAME_ENDED_ERROR() {
        return "出事！我完咗場遊戲先！";
    }

    @Override
    public String NEXT_GAME_QUEUED(String name) {
        return String.format("當遊戲喺 %s 度開始嗰陣，我會叫你", name);
    }

    @Override
    public String GAME_STARTING_IN(String name) {
        return String.format("遊戲即將喺 %s 開始", name);
    }

    @Override
    public String CANCEL() {
        return "取消";
    }

    @Override
    public String SB_IS_ELIMINATED(String name) {
        return String.format("%s 連續掛咗三輪機！出局！", name);
    }

    @Override
    public String LONE_WIN(int tgid, String name) {
        return String.format("其他人均已出局，<a href=\"tg://user?id=%1$s\">%2$s</a> 尚未出局，並自動勝出遊戲！", tgid, name);
    }
}
